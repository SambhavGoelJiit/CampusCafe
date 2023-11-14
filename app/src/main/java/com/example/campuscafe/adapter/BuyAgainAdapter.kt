package com.example.campuscafe.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.campuscafe.OrderDetailsActivity
import com.example.campuscafe.databinding.HistoryItemBinding
import com.example.campuscafe.model.OrderDetails
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class BuyAgainAdapter(
    private val orderItems: List<OrderDetails>,
    private val context: Context
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding =
            HistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        val currentItem = orderItems[position]
        holder.bind(currentItem)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderDetails", currentItem)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = orderItems.size

    inner class BuyAgainViewHolder(private val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("SetTextI18n")
        fun bind(orderItem: OrderDetails) {
            with(binding) {
                buyAgainFoodName.text = (orderItem.uid + orderItem.currentTime.toString()) ?: ""
                buyAgainFoodPrice.text = orderItem.total ?: ""

                recieveButton.visibility = View.INVISIBLE

                recieveButton.setOnClickListener {
                    val orderKey = orderItem.itemPushKey
                    orderKey?.let { key ->
                        val orderDetailsRef: DatabaseReference =
                            FirebaseDatabase.getInstance().reference.child("OrderDetails")
                        orderDetailsRef.child(key).child("orderCompleted").setValue(true)
                            .addOnSuccessListener {
                                recieveButton.visibility = View.GONE
                            }
                    }
                }

                if (orderItem.orderCompleted) {
                    orderStatus.setTextColor(Color.parseColor("#808080"))
                    orderStatus.text = "Completed"
                    recieveButton.visibility = View.INVISIBLE
                } else if (orderItem.orderDispatched) {
                    orderStatus.setTextColor(Color.parseColor("#00FF00"))
                    orderStatus.text = "Order Ready"
                    recieveButton.visibility = View.VISIBLE
                } else if (orderItem.orderAccepted) {
                    orderStatus.setTextColor(Color.parseColor("#CA9F18"))
                    orderStatus.text = "Food is being prepared"
                } else {
                    orderStatus.setTextColor(Color.parseColor("#FF0000"))
                    orderStatus.text = "Waiting for acceptance"
                }
            }
        }
    }
}

