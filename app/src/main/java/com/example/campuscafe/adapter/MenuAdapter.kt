package com.example.campuscafe.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campuscafe.databinding.MenuItemBinding
import com.example.campuscafe.model.CartItems
import com.example.campuscafe.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val context: Context,
    private val database: FirebaseDatabase,
    private val userId: String
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size
    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(menuItem: MenuItem) {
            binding.apply {
                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice
                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(context).load(uri).into(menuImage)

                menuAddToCart.setOnClickListener {
                    addToCart(menuItem)
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        private fun addToCart(menuItem: MenuItem) {
            val cartItem = CartItems(
                foodName = menuItem.foodName,
                foodPrice = menuItem.foodPrice,
                foodImage = menuItem.foodImage,
                foodQuantity = 1
            )

            val cartItemsReference = database.reference
                .child("userMainApp")
                .child(userId)
                .child("cartItems")

            cartItemsReference.orderByChild("foodName").equalTo(cartItem.foodName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (itemSnapshot in snapshot.children) {
                                val existingCartItem = itemSnapshot.getValue(CartItems::class.java)
                                existingCartItem?.let {
                                    val newQuantity =
                                        (it.foodQuantity?.toInt() ?: 0) + cartItem.foodQuantity!!
                                    itemSnapshot.ref.child("foodQuantity").setValue(newQuantity)
                                    showToast("Item Already in cart, Qty increased by 1")
                                }
                            }
                        } else {
                            val newItemRef = cartItemsReference.push()
                            newItemRef.setValue(cartItem)
                            showToast("Item Added to Cart")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
}