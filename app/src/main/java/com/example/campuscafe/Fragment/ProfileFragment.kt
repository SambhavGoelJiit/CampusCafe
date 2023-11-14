package com.example.campuscafe.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.campuscafe.LoginActivity
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        setUserData()

        binding.logoutButton.setOnClickListener {
            logoutUser()
        }

        return binding.root
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
                                token.setText(userProfile.tokenModel)
                                name.isEnabled = false
                                email.isEnabled = false
                                phone.isEnabled = false
                                token.isEnabled = false
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun logoutUser() {
        auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
    }
}