package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Order
import com.example.myapplication.model.Product
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    private val _allOrders = MutableLiveData<MutableList<Order>>(mutableListOf())
    
    private val _orders = MediatorLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    init {
        _orders.addSource(_currentUser) { updateVisibleOrders() }
        _orders.addSource(_allOrders) { updateVisibleOrders() }
        
        auth.currentUser?.let { firebaseUser ->
            val email = firebaseUser.email ?: ""
            fetchUserData(email)
            fetchUserOrders(email)
        }
    }

    private fun updateVisibleOrders() {
        val userEmail = _currentUser.value?.email
        val filtered = _allOrders.value?.filter { it.userEmail == userEmail } ?: emptyList()
        Log.d("MainViewModel", "Updating visible orders for $userEmail. Found: ${filtered.size}")
        _orders.value = filtered
    }

    private fun fetchUserData(email: String) {
        db.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                user?.profileImageBase64?.let { base64 ->
                    user.profileImage = decodeBase64(base64)
                }
                _currentUser.value = user
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Error fetching user", e)
            }
    }

    private fun fetchUserOrders(email: String) {
        Log.d("MainViewModel", "Starting fetch for user: $email")
        db.collection("orders")
            .whereEqualTo("userEmail", email)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("MainViewModel", "Success! Found ${documents.size()} total orders in Firestore for $email")
                val ordersList = mutableListOf<Order>()
                for (document in documents) {
                    try {
                        val order = document.toObject(Order::class.java)
                        ordersList.add(order)
                    } catch (e: Exception) {
                        Log.e("MainViewModel", "Error parsing order: ${document.id}", e)
                    }
                }
                // Sort locally to avoid needing Firestore composite indexes
                ordersList.sortByDescending { it.date }
                _allOrders.value = ordersList
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Firestore error fetching orders for $email", e)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        val loginEmail = if (email == "1") "1@example.com" else email
        val loginPassword = if (password == "1") "password123" else password

        auth.signInWithEmailAndPassword(loginEmail, loginPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserData(loginEmail)
                    fetchUserOrders(loginEmail)
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    fun createAccount(email: String, password: String, profileImage: Bitmap?, onResult: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(email, email.substringBefore("@"), password)
                    if (profileImage != null) {
                        user.profileImageBase64 = encodeToBase64(profileImage)
                        user.profileImage = profileImage
                    }
                    saveUserToFirestore(user)
                    _currentUser.value = user
                    _allOrders.value = mutableListOf()
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users").document(user.email).set(user)
            .addOnFailureListener { e -> Log.e("MainViewModel", "Error saving user", e) }
    }

    fun updateProfileImage(bitmap: Bitmap) {
        val user = _currentUser.value ?: return
        val base64Image = encodeToBase64(bitmap)
        
        user.profileImageBase64 = base64Image
        user.profileImage = bitmap
        
        saveUserToFirestore(user)
        _currentUser.value = user
    }

    private fun encodeToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, false)
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val byteArray = baos.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun decodeBase64(base64String: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _allOrders.value = mutableListOf()
        _cartItems.value = mutableListOf()
    }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.product.id == product.id }
        if (existingItem != null) existingItem.quantity++
        else currentItems.add(CartItem(product))
        _cartItems.value = currentItems
    }

    fun removeFromCart(product: Product) {
        val currentItems = _cartItems.value ?: mutableListOf()
        currentItems.removeAll { it.product.id == product.id }
        _cartItems.value = currentItems
    }

    fun getTotalPrice(): Double = _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0

    fun clearCart() { _cartItems.value = mutableListOf() }

    fun placeOrder() {
        val userEmail = _currentUser.value?.email ?: return
        val items = _cartItems.value?.toList() ?: emptyList()
        if (items.isEmpty()) return

        val orderId = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"
        val newOrder = Order(
            id = orderId,
            userEmail = userEmail,
            items = items,
            totalPrice = getTotalPrice(),
            status = "Processing",
            date = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        )

        db.collection("orders").document(orderId).set(newOrder)
            .addOnSuccessListener {
                val currentOrders = _allOrders.value?.toMutableList() ?: mutableListOf()
                currentOrders.add(0, newOrder)
                _allOrders.value = currentOrders
                Log.d("MainViewModel", "Order saved successfully: $orderId")
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Error placing order", e)
            }

        clearCart()
    }
}
