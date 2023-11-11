package com.example.campuscafe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.campuscafe.databinding.ActivityLoginBinding
import com.example.campuscafe.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.firebase.ui.auth.R.string.default_web_client_id))
            .requestEmail()
            .build()

        auth = Firebase.auth
        databaseReference = FirebaseDatabase.getInstance().getReference()
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        binding.loginButton.setOnClickListener {
            email = binding.userEmailLogin.text.toString().trim()
            password = binding.userPasswordLogin.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "All Fields Necessary", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                        updateUi(user)
                        finish()
                    } else {
                        Toast.makeText(this, "Create An Account", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SignActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.googleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            startActivityForResult(signIntent, 10001)
        }

        binding.dontHaveAccButton.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            val userId = user.uid
            val userReference = databaseReference.child("userMainApp").child(userId)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userData = snapshot.getValue(UserModel::class.java)
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }


        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Google Login Successful",
                            LENGTH_SHORT
                        ).show()
                        updateUi(task.result?.user)
                        finish()
                    } else {
                        Toast.makeText(this, "Google Login Error", LENGTH_SHORT)
                            .show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUi(currentUser)
        }

    }
}