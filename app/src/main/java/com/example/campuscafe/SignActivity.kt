package com.example.campuscafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.campuscafe.databinding.ActivitySignBinding
import com.example.campuscafe.model.UserModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String

    private val binding: ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.createAccButton.setOnClickListener{
            username = binding.userNameSign.text.toString().trim()
            email = binding.userEmailSign.text.toString().trim()
            password = binding.userPasswordSign.text.toString().trim()

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All Fields Are Necessary", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        binding.alreadyHaveAccButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.d("Account", "CreateAccount: Failure", task.exception)
            }
        }
    }

    private fun saveUserData() {
        username = binding.userNameSign.text.toString().trim()
        email = binding.userEmailSign.text.toString().trim()
        password = binding.userPasswordSign.text.toString().trim()
        val user = UserModel(username, email, password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("userMainApp").child(userId).setValue(user)
    }
}