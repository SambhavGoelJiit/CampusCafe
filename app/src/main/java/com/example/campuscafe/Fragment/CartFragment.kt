package com.example.campuscafe.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.campuscafe.CongratsBottomSheet
import com.example.campuscafe.PayoutActivity
import com.example.campuscafe.R
import com.example.campuscafe.adapter.CartAdapter
import com.example.campuscafe.databinding.FragmentCartBinding

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container,false)

        val cartFoodName = listOf("Burger", "Pizza", "Momos", "Sandwich", "Burger", "Pizza", "Momos", "Sandwich", "Burger")
        val cartItemPrice = listOf("₹10", "₹5", "₹6", "₹7","₹10", "₹5", "₹6", "₹7", "₹15")
        val cartImage = listOf(
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu1,
            R.drawable.menu2,
            R.drawable.menu3,
            R.drawable.menu4,
            R.drawable.menu1,
        )

        val adapter = CartAdapter(ArrayList(cartFoodName), ArrayList(cartItemPrice), ArrayList(cartImage))
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecyclerView.adapter = adapter
        binding.proceedButton.setOnClickListener{
            val intent = Intent(requireContext(), PayoutActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object {

    }
}