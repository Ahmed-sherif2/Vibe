package com.example.myapplication.data

import com.example.myapplication.model.Brand
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Order
import com.example.myapplication.model.Product

object DataRepository {
    val brands = listOf(
        Brand("Nike", "nikelogo"),
        Brand("Adidas", "addidaslogo"),
        Brand("Zara", "zaralogo"),
        Brand("Gant", "gantlogo")
    )

    val products = listOf(
        Product(1, "Nike Air Max", 120.0, "Nike", "nike"),
        Product(2, "Nike Sport T-Shirt", 35.0, "Nike", "nike2"),
        Product(3, "Adidas Superstar", 90.0, "Adidas", "addidas1"),
        Product(4, "Adidas Hoodie", 65.0, "Adidas", "addidas2"),
        Product(5, "Zara Summer Dress", 50.0, "Zara", "zara1"),
        Product(6, "Zara Slim Fit Jeans", 45.0, "Zara", "zara2"),
        Product(7, "Gant Polo Shirt", 85.0, "Gant", "gant1"),
        Product(8, "Gant Leather Jacket", 250.0, "Gant", "gant2"),
        Product(9, "Nike Tank Top", 30.0, "Nike", "niketanktop"),
        Product(10, "Nike Tech Fleece Hoodie", 110.0, "Nike", "niketechfleeshoodie"),
        Product(11, "Adidas Original 70s", 80.0, "Adidas", "addidasorignal70"),
        Product(12, "Adidas Woman Sweater", 70.0, "Adidas", "addidaswomansweater"),
        Product(13, "Adidas Sport Pants", 60.0, "Adidas", "addidaskw"),
        Product(14, "Zara Blazer", 95.0, "Zara", "zara3"),
        Product(15, "Zara Stylish Skirt", 55.0, "Zara", "zara4"),
        Product(16, "Zara Winter Coat", 150.0, "Zara", "zara5"),
        Product(17, "Gant Casual Shirt", 80.0, "Gant", "gant3"),
        Product(18, "Gant V-Neck Sweater", 90.0, "Gant", "gant4"),
        Product(19, "Gant Classic Chinos", 100.0, "Gant", "gant5")
    )

    val orders = listOf(
        Order("ORD-001", "1", listOf(CartItem(products[0])), 120.0, "Delivered", "2023-10-01"),
        Order("ORD-002", "1", listOf(CartItem(products[2])), 90.0, "Shipped", "2023-10-15"),
        Order("ORD-003", "1", listOf(CartItem(products[6])), 85.0, "Processing", "2023-10-28"),
        Order("ORD-004", "1", listOf(CartItem(products[4])), 50.0, "Delivered", "2023-09-20"),
        Order("ORD-005", "1", listOf(CartItem(products[1])), 35.0, "Delivered", "2023-09-15"),
        Order("ORD-006", "1", listOf(CartItem(products[7])), 250.0, "Delivered", "2023-09-01")
    )

    fun getProductsByBrand(brandName: String): List<Product> {
        return products.filter { it.brand.equals(brandName, ignoreCase = true) }
    }

    fun searchProducts(query: String): List<Product> {
        return products.filter { 
            it.name.contains(query, ignoreCase = true) || it.brand.contains(query, ignoreCase = true)
        }
    }
}
