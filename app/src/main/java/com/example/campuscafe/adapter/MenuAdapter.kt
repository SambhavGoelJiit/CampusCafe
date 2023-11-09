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
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        auth = FirebaseAuth.getInstance()
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size
    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = menuItems[position]
            binding.apply {
                menuFoodName.text = item.foodName
                menuPrice.text = item.foodPrice
                val uri = Uri.parse(item.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)

                menuAddToCart.setOnClickListener {
                    val database = FirebaseDatabase.getInstance().reference
                    val userId = auth.currentUser?.uid ?: ""
                    val cartItem = CartItems(
                        item.foodName.toString(),
                        item.foodPrice.toString(),
                        item.foodImage.toString(),
                        1
                    )
                    database.child("userMainApp").child(userId).child("cartItems").push()
                        .setValue(cartItem).addOnSuccessListener {
                            Toast.makeText(requireContext, "Added To Cart", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener{
                            Toast.makeText(requireContext, "Failed To Add To Cart", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }
}