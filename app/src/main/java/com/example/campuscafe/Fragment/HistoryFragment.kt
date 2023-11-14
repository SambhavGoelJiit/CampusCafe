package com.example.campuscafe.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
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
    ): View {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        setupSwipeToRefresh(view)
        retrieveOrderHistory()
        return view
    }

    private fun setupSwipeToRefresh(view: View) {
        val swipeRefreshLayout =
            view.findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayoutHistory)
        swipeRefreshLayout.setOnRefreshListener {
            retrieveOrderHistory()
            swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun retrieveOrderHistory() {
        userId = auth.currentUser?.uid ?: ""

        val orderDetailsRef: DatabaseReference = database.reference.child("OrderDetails")
        val query = orderDetailsRef.orderByChild("uid").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfOrderItems.clear()
                for (orderSnapshot in snapshot.children) {
                    val orderItem = orderSnapshot.getValue(OrderDetails::class.java)
                    orderItem?.let {
                        listOfOrderItems.add(it)
                    }
                }
                listOfOrderItems.reverse()

                if (listOfOrderItems.isNotEmpty()) {
                    setPreviousBuyItemsRecyclerView(listOfOrderItems)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun setPreviousBuyItemsRecyclerView(orderItems: List<OrderDetails>) {
        val rv = binding.buyAgainRecyclerView
        rv.layoutManager = LinearLayoutManager(requireContext())
        buyAgainAdapter = BuyAgainAdapter(orderItems, requireContext())
        rv.adapter = buyAgainAdapter
    }
}