package com.example.campuscafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscafe.adapter.RecentBuyAdapter
import com.example.campuscafe.databinding.ActivityOrderDetailsBinding
import com.example.campuscafe.model.OrderDetails

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodPrices: ArrayList<String>
    private lateinit var allFoodQuantities: ArrayList<Int>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val recentOrderItems = intent.getSerializableExtra("RecentBuyOrderItem") as? OrderDetails
        recentOrderItems?.let { orderDetails ->
            allFoodNames = (orderDetails.foodNames as? ArrayList<String>) ?: ArrayList()
            allFoodPrices = (orderDetails.foodPrices as? ArrayList<String>) ?: ArrayList()
            allFoodQuantities = (orderDetails.foodQuantities as? ArrayList<Int>) ?: ArrayList()
        }
        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.orderDetailRV
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodPrices, allFoodQuantities)
        rv.adapter = adapter
    }
}