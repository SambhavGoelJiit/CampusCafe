package com.example.campuscafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.campuscafe.Fragment.CartFragment
import com.example.campuscafe.databinding.ActivityPayoutBinding
import com.example.campuscafe.databinding.FragmentCartBinding
import com.example.campuscafe.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayoutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodItemNames: ArrayList<String>
    private lateinit var foodItemPrices: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var totalAmount: String
    private lateinit var name: String
    private lateinit var phone: String
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()
        setUserData()

        val intent = intent
        foodItemNames = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrices = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>
        totalAmount = "₹" + calculateTotalAmount().toString()
        binding.custAmount.isEnabled = false
        binding.custAmount.setText(totalAmount)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.placeMyOrder.setOnClickListener {
            name = binding.custName.text.toString().trim()
            phone = binding.custPhone.text.toString().trim()

            if (name.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Enter All Details", Toast.LENGTH_SHORT).show()
            } else {
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderDetails = OrderDetails(
            userId,
            name,
            foodItemNames,
            foodItemPrices,
            foodItemQuantities,
            phone,
            false,
            false,
            itemPushKey,
            time
        )
        val orderReference = databaseReference.child("OrderDetails").child(itemPushKey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            removeItemFromCart()
            addOrderToHistory(orderDetails)
            Toast.makeText(this, "Order Placed", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CartFragment::class.java)
            startActivity(intent)
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed To Order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addOrderToHistory(orderDetails: OrderDetails) {
        databaseReference.child("userMainApp").child(userId).child("PurHistory")
            .child(orderDetails.itemPushKey!!).setValue(orderDetails).addOnSuccessListener {
            }
    }

    private fun removeItemFromCart() {
        val cartItemRef = databaseReference.child("userMainApp").child(userId).child("cartItems")
        cartItemRef.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0;
        for (i in 0 until foodItemPrices.size) {
            var price = foodItemPrices[i]
            val lastChar = price.last()
            val priceIntValue = if (lastChar == '₹') {
                price.dropLast(1).toInt()
            } else {
                price.toInt()
            }
            var quantity = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userReference = databaseReference.child("userMainApp").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val names =
                            snapshot.child("usernameModel").getValue(String::class.java) ?: ""
                        val phones = snapshot.child("phoneModel").getValue(String::class.java) ?: ""

                        binding.apply {
                            custName.setText(names)
                            custPhone.setText(phones)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}