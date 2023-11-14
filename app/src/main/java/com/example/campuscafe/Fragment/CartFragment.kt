package com.example.campuscafe.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscafe.PayoutActivity
import com.example.campuscafe.adapter.CartAdapter
import com.example.campuscafe.adapter.OnCartItemDeletedListener
import com.example.campuscafe.databinding.FragmentCartBinding
import com.example.campuscafe.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment(), OnCartItemDeletedListener {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""

        cartAdapter = CartAdapter(mutableListOf(), requireContext(), database, userId, this)

        binding.cartRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cartRecyclerView.adapter = cartAdapter

        binding.proceedButton.setOnClickListener {
            getOrderItemDetails()
        }


        retrieveCartItems()
        return binding.root
    }

    override fun onCartItemDeleted() {
        // Refresh logic here
        retrieveCartItems()
    }

    @SuppressLint("SetTextI18n")
    private fun updateProceedButtonVisibility() {
        if (cartAdapter.itemCount > 0) {
            binding.proceedButton.isEnabled = true
            binding.emptyCartText.visibility = View.GONE
        } else {
            binding.proceedButton.isEnabled = false
            binding.emptyCartText.visibility = View.VISIBLE
            binding.emptyCartText.text = "Cart is Empty"
        }
    }

    private fun retrieveCartItems() {
        val cartItemsReference = database.reference
            .child("userMainApp")
            .child(userId)
            .child("cartItems")

        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartItems>()
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItems::class.java)
                    cartItem?.let {
                        cartItems.add(it)
                    }
                }
                cartAdapter.cartItems = cartItems
                cartAdapter.notifyDataSetChanged()
                updateProceedButtonVisibility()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateCartQuantitiesInDatabase() {
        val cartAdapter = binding.cartRecyclerView.adapter as CartAdapter

        val foodRef: DatabaseReference = database.reference
            .child("userMainApp").child(userId).child("cartItems")

        for (cartItem in cartAdapter.cartItems) {
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
    }

    private fun getOrderItemDetails() {
        val orderIdRef: DatabaseReference =
            database.reference.child("userMainApp").child(userId).child("cartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodQuantity = cartAdapter.getUpdatedItemsQuantity()

        orderIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                }
                orderNow(foodName, foodPrice, foodQuantity)
                updateCartQuantitiesInDatabase()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "OrderNot Generated", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodQuantity: MutableList<Int>
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemQuantity", foodQuantity as ArrayList<Int>)
            startActivity(intent)
        }
    }
}
