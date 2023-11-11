package com.example.campuscafe.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.campuscafe.R
import com.example.campuscafe.databinding.FragmentProfileBinding
import com.example.campuscafe.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setUserData()

        binding.apply {
            name.isEnabled = false
            email.isEnabled = false
            phone.isEnabled = false
            binding.editButton.setOnClickListener {
                name.isEnabled = !name.isEnabled
                email.isEnabled = !email.isEnabled
                phone.isEnabled = !phone.isEnabled
                editButton.visibility = View.INVISIBLE
            }
        }

        binding.saveInfoButton.setOnClickListener {
            val name = binding.name.text.toString()
            val email = binding.email.text.toString()
            val phone = binding.phone.text.toString()

            updateUserData(name, email, phone)

            binding.name.isEnabled = false
            binding.email.isEnabled = false
            binding.phone.isEnabled = false
            binding.editButton.visibility = View.VISIBLE
        }

        return binding.root
    }

    private fun updateUserData(name: String, email: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("userMainApp").child(userId)
            val userData = hashMapOf(
                "usernameModel" to name,
                "emailModel" to email,
                "phoneModel" to phone
            )
            userRef.setValue(userData).addOnSuccessListener {
                Toast.makeText(requireContext(), "Update Successful", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Update Unsuccessful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUserData() {
        val userId: String? = auth.currentUser?.uid
        if (userId != null) {
            val userRef = database.getReference("userMainApp").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            binding.apply {
                                name.setText(userProfile.usernameModel)
                                email.setText(userProfile.emailModel)
                                phone.setText(userProfile.phoneModel)
                            }

                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}