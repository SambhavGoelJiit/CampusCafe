package com.example.campuscafe.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscafe.CongratsBottomSheet
import com.example.campuscafe.PayoutActivity
import com.example.campuscafe.R
import com.example.campuscafe.adapter.CartAdapter
import com.example.campuscafe.databinding.FragmentCartBinding
import com.example.campuscafe.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var cartFoodName: MutableList<String>
    private lateinit var cartItemPrice: MutableList<String>
    private lateinit var cartImageUri: MutableList<String>
    private lateinit var cartItemQty: MutableList<Int>
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItems()

        binding.proceedButton.setOnClickListener {
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    private fun retrieveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodRef: DatabaseReference =
            database.reference.child("userMainApp").child(userId).child("cartItems")

        cartFoodName = mutableListOf()
        cartItemPrice = mutableListOf()
        cartImageUri = mutableListOf()
        cartItemQty = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)
                    cartItems?.foodName?.let { cartFoodName.add(it) }
                    cartItems?.foodPrice?.let { cartItemPrice.add(it) }
                    cartItems?.foodImage?.let { cartImageUri.add(it) }
                    cartItems?.foodQuantity?.let { cartItemQty.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {
                val adapter = CartAdapter(
                    cartFoodName,
                    cartItemPrice,
                    cartImageUri,
                    cartItemQty,
                    requireContext()
                )
                binding.cartRecyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.cartRecyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Unable To Fetch Data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {

    }
}