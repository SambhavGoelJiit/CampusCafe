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

    private lateinit var orderDetails: OrderDetails // Add this line

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        orderDetails = intent.getSerializableExtra("orderDetails") as? OrderDetails
            ?: return

        setAdapter()
    }

    private fun setAdapter() {
        val rv = binding.orderDetailRV
        val foodNames: ArrayList<String>? = orderDetails.foodNames?.let { ArrayList(it) }
        val foodPrices: ArrayList<String>? = orderDetails.foodPrices?.let { ArrayList(it) }
        val foodQuantities: ArrayList<Int>? = orderDetails.foodQuantities?.let { ArrayList(it) }

        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(
            this,
            foodNames ?: ArrayList(),
            foodPrices ?: ArrayList(),
            foodQuantities ?: ArrayList()
        )
        rv.adapter = adapter
    }
}