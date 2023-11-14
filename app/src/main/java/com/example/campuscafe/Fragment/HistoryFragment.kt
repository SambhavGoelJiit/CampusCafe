package com.example.campuscafe.Fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscafe.OrderDetailsActivity
import com.example.campuscafe.R
import com.example.campuscafe.adapter.BuyAgainAdapter
import com.example.campuscafe.databinding.FragmentHistoryBinding
import com.example.campuscafe.model.OrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private var listOfOrderItems: MutableList<OrderDetails> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveOrderHistory()

        binding.currOrder.setOnClickListener {
            seeItemsRecentBuy()
        }

        binding.recievedButton.setOnClickListener {
            updateOrderStatus()
        }

        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItems[0].itemPushKey
        val completeOrderRef = database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderRef.child("paymentReceived").setValue(true)
    }

    private fun seeItemsRecentBuy() {
        listOfOrderItems.firstOrNull()?.let { recentBuy ->
            val intent = Intent(requireContext(), OrderDetailsActivity::class.java)
            intent.putExtra("RecentBuyOrderItem", recentBuy)
            startActivity(intent)
        }
    }

    private fun retrieveOrderHistory() {
        binding.currOrder.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid ?: ""
        val buyItemRef: DatabaseReference =
            database.reference.child("userMainApp").child(userId).child("PurHistory")
        val sortQuery = buyItemRef.orderByChild("currentTime")

        sortQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (buySnapshot in snapshot.children) {
                    val buyHistoryItem = buySnapshot.getValue(OrderDetails::class.java)
                    buyHistoryItem?.let {
                        listOfOrderItems.add(it)
                    }
                }
                listOfOrderItems.reverse()

                if (listOfOrderItems.isNotEmpty()) {
                    setDataInRecentBuyItem()
                    setPreviousBuyItemsRecyclerView()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setDataInRecentBuyItem() {
        binding.currOrder.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding) {
                buyAgainFoodName.text = it.foodNames?.firstOrNull() ?: ""
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull() ?: ""
                val isOrderAccepted = listOfOrderItems[0].orderAccepted
                if(isOrderAccepted){
                    orderStatus.background.setTint(Color.GREEN)
                    recievedButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setPreviousBuyItemsRecyclerView() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size) {
            listOfOrderItems[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItems[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
        }
        val rv = binding.buyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice, requireContext())
        rv.adapter = buyAgainAdapter
    }
}