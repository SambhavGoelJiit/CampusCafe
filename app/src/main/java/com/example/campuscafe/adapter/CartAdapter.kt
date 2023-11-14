package com.example.campuscafe.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.campuscafe.databinding.CartItemBinding
import com.example.campuscafe.model.CartItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

interface OnCartItemDeletedListener {
    fun onCartItemDeleted()
}
class CartAdapter(
    var cartItems: MutableList<CartItems>,
    private val context: Context,
    private val database: FirebaseDatabase,
    private val userId: String,
    private val deleteListener: OnCartItemDeletedListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun getItemIndex(foodName: String): Int {
        for (i in cartItems.indices) {
            if (cartItems[i].foodName == foodName) {
                return i
            }
        }
        return -1
    }

    fun getUpdatedItemsQuantity(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        for (i in cartItems.indices) {
            cartItems[i].foodQuantity?.let { itemQuantity.add(it) }
        }
        return itemQuantity
    }

    fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        if (positionRetrieve < cartItems.size) {
            onComplete(
                cartItems[positionRetrieve].foodName ?: ""
            )
        } else {
            onComplete(null)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItems.size

    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                cartFoodName.text = cartItems[position].foodName ?: ""
                cartItemPrice.text = cartItems[position].foodPrice ?: ""
                val uri = Uri.parse(cartItems[position].foodImage ?: "")
                Glide.with(context).load(uri).into(cartImage)
                cartItemQuantity.text = cartItems[position].foodQuantity.toString()

                minusButton.setOnClickListener {
                    decreaseQuantity(position)
                }
                plusButton.setOnClickListener {
                    increaseQuantity(position)
                }
                deleteButton.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        deleteItem(itemPosition)
                    }
                }
            }
        }

        private fun decreaseQuantity(position: Int) {
            val currentItem = cartItems[position]
            if (currentItem.foodQuantity!! > 1) {
                currentItem.foodQuantity = currentItem.foodQuantity!! - 1
                updateQuantityInDatabase(currentItem)
                binding.cartItemQuantity.text = currentItem.foodQuantity.toString()
            }
        }

        private fun increaseQuantity(position: Int) {
            val currentItem = cartItems[position]
            if (currentItem.foodQuantity!! < 10) {
                currentItem.foodQuantity = currentItem.foodQuantity!! + 1
                updateQuantityInDatabase(currentItem)
                binding.cartItemQuantity.text = currentItem.foodQuantity.toString()
            }
        }

        private fun updateQuantityInDatabase(cartItem: CartItems) {
            // Update the quantity directly in the database using cartItem.foodName as the identifier
            val foodRef: DatabaseReference = database.reference
                .child("userMainApp").child(userId).child("cartItems")
            foodRef.orderByChild("foodName").equalTo(cartItem.foodName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (itemSnapshot in snapshot.children) {
                            itemSnapshot.ref.child("foodQuantity")
                                .setValue(cartItem.foodQuantity)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }

        private fun deleteItem(position: Int) {
            val currentItem = cartItems[position]
            val foodRef: DatabaseReference = database.reference
                .child("userMainApp").child(userId).child("cartItems")
            foodRef.orderByChild("foodName").equalTo(currentItem.foodName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (itemSnapshot in snapshot.children) {
                            itemSnapshot.ref.removeValue().addOnSuccessListener {
                                cartItems.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, cartItems.size)
                                Toast.makeText(context, "Item Removed From Cart", Toast.LENGTH_SHORT).show()
                                deleteListener.onCartItemDeleted()
                            }.addOnFailureListener {
                                Toast.makeText(context, "Failed To Delete", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }
}