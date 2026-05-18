package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ItemBrandBinding
import com.example.myapplication.databinding.ItemCartBinding
import com.example.myapplication.databinding.ItemOrderBinding
import com.example.myapplication.databinding.ItemProductBinding
import com.example.myapplication.model.Brand
import com.example.myapplication.model.CartItem
import com.example.myapplication.model.Order
import com.example.myapplication.model.Product

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.binding.productName.text = product.name
        holder.binding.productBrand.text = product.brand
        holder.binding.productPrice.text = "$${product.price}"

        val context = holder.itemView.context
        val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
        if (resId != 0) {
            Glide.with(context).load(resId).into(holder.binding.productImage)
        }

        holder.binding.addToCartButton.setOnClickListener { onAddToCart(product) }
    }

    override fun getItemCount() = products.size
}

class BrandAdapter(
    private val brands: List<Brand>,
    private val onBrandClick: (Brand) -> Unit
) : RecyclerView.Adapter<BrandAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemBrandBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBrandBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val brand = brands[position]
        holder.binding.brandName.text = brand.name
        
        val context = holder.itemView.context
        brand.logoUrl?.let { logoName ->
            val resId = context.resources.getIdentifier(logoName, "drawable", context.packageName)
            if (resId != 0) {
                Glide.with(context).load(resId).into(holder.binding.brandLogo)
            }
        }

        holder.itemView.setOnClickListener { onBrandClick(brand) }
    }

    override fun getItemCount() = brands.size
}

class CartAdapter(
    private val items: List<CartItem>,
    private val onRemove: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.productName.text = item.product.name
        holder.binding.productPrice.text = "$${item.product.price}"
        holder.binding.quantityText.text = "Qty: ${item.quantity}"

        val context = holder.itemView.context
        val product = item.product
        val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
        if (resId != 0) {
            Glide.with(context).load(resId).into(holder.binding.productImage)
        }

        holder.binding.removeButton.setOnClickListener { onRemove(item.product) }
    }

    override fun getItemCount() = items.size
}

class OrderAdapter(
    private val orders: List<Order>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orders[position]
        holder.binding.orderId.text = order.id
        holder.binding.orderDate.text = order.date
        holder.binding.orderStatus.text = order.status
        holder.binding.orderTotal.text = "$${String.format("%.2f", order.totalPrice)}"

        // Show image of the first product in the order
        if (order.items.isNotEmpty()) {
            val context = holder.itemView.context
            val product = order.items[0].product
            val resId = context.resources.getIdentifier(product.imageUrl, "drawable", context.packageName)
            if (resId != 0) {
                Glide.with(context).load(resId).into(holder.binding.orderProductImage)
            }
        }
    }

    override fun getItemCount() = orders.size
}
