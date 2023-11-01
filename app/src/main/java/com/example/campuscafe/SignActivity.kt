package com.example.campuscafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.campuscafe.databinding.ActivityLoginBinding
import com.example.campuscafe.databinding.ActivitySignBinding

class SignActivity : AppCompatActivity() {

    private val binding: ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.createAccButton.setOnClickListener{
            val intent = Intent(this, ChooseLocationActivity::class.java)
            startActivity(intent)
        }
    }
}