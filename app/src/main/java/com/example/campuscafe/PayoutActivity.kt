package com.example.campuscafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.campuscafe.databinding.ActivityPayoutBinding
import com.example.campuscafe.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.properties.Delegates

@Suppress("NAME_SHADOWING")
class PayoutActivity : AppCompatActivity() {
    lateinit var binding: ActivityPayoutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var foodItemNames: ArrayList<String>
    private lateinit var foodItemPrices: ArrayList<String>
    private lateinit var foodItemQuantities: ArrayList<Int>
    private lateinit var totalAmount: String
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        setUserData()

        val intent = intent
        foodItemNames = intent.getStringArrayListExtra("FoodItemName") as ArrayList<String>
        foodItemPrices = intent.getStringArrayListExtra("FoodItemPrice") as ArrayList<String>
        foodItemQuantities = intent.getIntegerArrayListExtra("FoodItemQuantity") as ArrayList<Int>
        totalAmount = calculateTotalAmount().toString()
        val strTotal = "₹$totalAmount"
        binding.custAmount.setText(strTotal)

        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.placeMyOrder.setOnClickListener {
            var availableItemCount = 0

            for (item in foodItemNames) {
                checkItemAvailability(item) { available ->
                    if (available) {
                        availableItemCount++
                        if (availableItemCount == foodItemNames.size) {
                            placeOrder()
                        }
                    } else {
                        Toast.makeText(
                            this@PayoutActivity,
                            "$item unavailable at the moment, Please remove from cart to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun checkItemAvailability(foodName: String, callback: (Boolean) -> Unit) {
        val menuRef = FirebaseDatabase.getInstance().getReference("menu")
        menuRef.orderByChild("foodName").equalTo(foodName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid ?: ""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseReference.child("OrderDetails").push().key
        val orderTotalAmount = calculateTotalAmount()
        val userRef = databaseReference.child("userMainApp")


        userRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userBalance =
                    snapshot.child("tokenModel").getValue(String::class.java)?.toInt() ?: 0
                val name = snapshot.child("usernameModel").getValue(String::class.java)
                val phone = snapshot.child("phoneModel").getValue(String::class.java)
                if (userBalance >= orderTotalAmount) {
                    val orderDetails = OrderDetails(
                        userId,
                        name,
                        totalAmount,
                        foodItemNames,
                        foodItemPrices,
                        foodItemQuantities,
                        phone,
                        orderAccepted = false,
                        orderDispatched = false,
                        orderCompleted = false,
                        itemPushKey = itemPushKey,
                        currentTime = time
                    )
                    val orderReference =
                        databaseReference.child("OrderDetails").child(itemPushKey!!)
                    orderReference.setValue(orderDetails).addOnSuccessListener {
                        removeItemFromCart()
                        val updatedBalance = userBalance - orderTotalAmount
                        snapshot.ref.child("tokenModel").setValue(updatedBalance.toString())
                        Toast.makeText(this@PayoutActivity, "Order Placed", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@PayoutActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(this@PayoutActivity, "Failed To Order", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this@PayoutActivity, "Insufficient Balance", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun removeItemFromCart() {
        val cartItemRef = databaseReference.child("userMainApp").child(userId).child("cartItems")
        cartItemRef.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0

        for (i in foodItemPrices.indices) {
            val price = foodItemPrices[i]
            val priceIntValue = extractPriceValue(price)
            val quantity = foodItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun extractPriceValue(price: String): Int {
        val priceWithoutSymbol = price.replace(regex = "[^\\d.]".toRegex(), replacement = "")
        return priceWithoutSymbol.toIntOrNull() ?: 0
    }

    private fun setUserData() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            val userReference = databaseReference.child("userMainApp").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name =
                            snapshot.child("usernameModel").getValue(String::class.java) ?: ""
                        val phone = snapshot.child("phoneModel").getValue(String::class.java) ?: ""
                        val token = snapshot.child("tokenModel").getValue(String::class.java) ?: ""

                        binding.apply {
                            custName.setText(name)
                            custPhone.setText(phone)
                            custToken.setText(token)

                            custName.isEnabled = false
                            custPhone.isEnabled = false
                            custToken.isEnabled = false
                            custAmount.isEnabled = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
    }
}