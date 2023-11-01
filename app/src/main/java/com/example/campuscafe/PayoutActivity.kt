package com.example.campuscafe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.campuscafe.databinding.ActivityPayoutBinding

class PayoutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityPayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.placeMyOrder.setOnClickListener {
            val bottomSheetDialog = CongratsBottomSheet()
            bottomSheetDialog.show(supportFragmentManager, "Test")
        }
    }
}