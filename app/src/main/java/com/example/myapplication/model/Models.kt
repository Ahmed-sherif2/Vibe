package com.example.myapplication.model

import android.graphics.Bitmap
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var email: String = "",
    var name: String = "",
    var password: String? = null,
    var profileImageUrl: String? = null,
    var profileImageBase64: String? = null,
    @get:Exclude @set:Exclude var profileImage: Bitmap? = null
) : Parcelable

@Parcelize
data class Product(
    var id: Int = 0,
    var name: String = "",
    var price: Double = 0.0,
    var brand: String = "",
    var imageUrl: String = "",
    var description: String = ""
) : Parcelable

data class Brand(
    var name: String = "",
    var logoUrl: String? = null
)

@Parcelize
data class CartItem(
    var product: Product = Product(),
    var quantity: Int = 1
) : Parcelable

@Parcelize
data class Order(
    var id: String = "",
    var userEmail: String = "",
    var items: List<CartItem> = emptyList(),
    var totalPrice: Double = 0.0,
    var status: String = "", // "Processing", "Shipped", "Delivered"
    var date: String = ""
) : Parcelable
