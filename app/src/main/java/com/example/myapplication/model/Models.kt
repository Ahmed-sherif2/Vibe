package com.example.myapplication.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val email: String = "",
    val name: String = "",
    val password: String? = null,
    var profileImage: Bitmap? = null,
    var profileImageUrl: String? = null
) : Parcelable

@Parcelize
data class Product(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val brand: String = "",
    val imageUrl: String = "",
    val description: String = ""
) : Parcelable

data class Brand(
    val name: String = "",
    val logoUrl: String? = null
)

@Parcelize
data class CartItem(
    val product: Product = Product(),
    var quantity: Int = 1
) : Parcelable

@Parcelize
data class Order(
    val id: String = "",
    val userEmail: String = "",
    val items: List<CartItem> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "", // "Processing", "Shipped", "Delivered"
    val date: String = ""
) : Parcelable
