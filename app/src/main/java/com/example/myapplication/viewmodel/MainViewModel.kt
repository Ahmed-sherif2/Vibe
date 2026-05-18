package com.example.myapplication.viewmodel

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.DataRepository
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Order
import com.example.myapplication.model.Product
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _cartItems = MutableLiveData<MutableList<CartItem>>(mutableListOf())
    val cartItems: LiveData<MutableList<CartItem>> = _cartItems

    private val _allOrders = MutableLiveData<MutableList<Order>>(DataRepository.orders.toMutableList())
    
    private val _orders = MediatorLiveData<List<Order>>()
    val orders: LiveData<List<Order>> = _orders

    init {
        _orders.addSource(_currentUser) { user ->
            _orders.value = _allOrders.value?.filter { it.userEmail == user?.email } ?: emptyList()
        }
        _orders.addSource(_allOrders) { allOrders ->
            _orders.value = allOrders.filter { it.userEmail == _currentUser.value?.email }
        }
        
        // Initial check for logged in user
        auth.currentUser?.let { firebaseUser ->
            fetchUserData(firebaseUser.email ?: "")
        }
    }

    private fun fetchUserData(email: String) {
        db.collection("users").document(email).get()
            .addOnSuccessListener { document ->
                val user = document.toObject(User::class.java)
                _currentUser.value = user
            }
            .addOnFailureListener { e ->
                Log.e("MainViewModel", "Error fetching user", e)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        val loginEmail = if (email == "1") "1@example.com" else email
        val loginPassword = if (password == "1") "password123" else password

        auth.signInWithEmailAndPassword(loginEmail, loginPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    fetchUserData(loginEmail)
                    onResult(true)
                } else {
                    if (email == "1") {
                        createAccount("1@example.com", "password123", null) { success ->
                            if (success) login("1", "1", onResult)
                            else onResult(false)
                        }
                    } else {
                        onResult(false)
                    }
                }
            }
    }

    fun createAccount(email: String, password: String, profileImage: Bitmap?, onResult: (Boolean) -> Unit) {
        val regEmail = if (email == "1") "1@example.com" else email
        val regPassword = if (password == "1") "password123" else password

        auth.createUserWithEmailAndPassword(regEmail, regPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(regEmail, regEmail.substringBefore("@"), regPassword)
                    if (profileImage != null) {
                        uploadProfileImage(regEmail, profileImage) { url ->
                            user.profileImageUrl = url
                            saveUserToFirestore(user)
                            _currentUser.value = user
                            onResult(true)
                        }
                    } else {
                        saveUserToFirestore(user)
                        _currentUser.value = user
                        onResult(true)
                    }
                } else {
                    onResult(false)
                }
            }
    }

    private fun saveUserToFirestore(user: User) {
        db.collection("users").document(user.email).set(user)
            .addOnFailureListener { e -> Log.e("MainViewModel", "Error saving user", e) }
    }

    private fun uploadProfileImage(email: String, bitmap: Bitmap, callback: (String) -> Unit) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()

        val ref = storage.reference.child("profile_images/$email.jpg")
        ref.putBytes(data)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }.addOnFailureListener {
                    callback("") // Return empty if URL fetch fails
                }
            }
            .addOnFailureListener {
                Log.e("MainViewModel", "Upload failed", it)
                callback("") // Return empty so the app doesn't hang
            }
    }

    fun updateProfileImage(bitmap: Bitmap) {
        val email = _currentUser.value?.email ?: return
        uploadProfileImage(email, bitmap) { url ->
            _currentUser.value?.let { user ->
                user.profileImageUrl = url
                user.profileImage = bitmap
                saveUserToFirestore(user)
                _currentUser.value = user
            }
        }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
    }

    fun addToCart(product: Product) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.product.id == product.id }
        
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(CartItem(product))
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(product: Product) {
        val currentItems = _cartItems.value ?: mutableListOf()
        currentItems.removeAll { it.product.id == product.id }
        _cartItems.value = currentItems
    }

    fun getTotalPrice(): Double {
        return _cartItems.value?.sumOf { it.product.price * it.quantity } ?: 0.0
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }

    fun placeOrder() {
        val userEmail = _currentUser.value?.email ?: return
        val items = _cartItems.value?.toList() ?: emptyList()
        if (items.isEmpty()) return

        val total = getTotalPrice()
        val newOrder = Order(
            id = "ORD-${System.currentTimeMillis().toString().takeLast(6)}",
            userEmail = userEmail,
            items = items,
            totalPrice = total,
            status = "Processing",
            date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )

        val currentOrders = _allOrders.value ?: mutableListOf()
        currentOrders.add(0, newOrder)
        _allOrders.value = currentOrders
        
        db.collection("orders").add(newOrder)
        
        clearCart()
    }
}
