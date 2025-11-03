package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private lateinit var sessionManager: SessionManager
    private lateinit var textViewUserName: TextView
    private lateinit var textViewUserContact: TextView
    private lateinit var textViewUserGender: TextView
    private lateinit var textViewUserRole: TextView
    private lateinit var textViewRegistrationDate: TextView
    private lateinit var buttonLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        sessionManager = SessionManager(requireContext())
        
        // Initialize views
        textViewUserName = view.findViewById(R.id.textViewUserName)
        textViewUserContact = view.findViewById(R.id.textViewUserContact)
        textViewUserGender = view.findViewById(R.id.textViewUserGender)
        textViewUserRole = view.findViewById(R.id.textViewUserRole)
        textViewRegistrationDate = view.findViewById(R.id.textViewRegistrationDate)
        buttonLogout = view.findViewById(R.id.buttonLogout)
        
        // Load user data
        loadUserProfile()
        
        // Setup logout button
        buttonLogout.setOnClickListener {
            logout()
        }
        
        return view
    }
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val userId = sessionManager.getUserId()
                if (userId != -1) {
                    val database = DatabaseProvider.getDatabase(requireContext())
                    val user = database.userDao().getUserById(userId)
                    
                    user?.let {
                        textViewUserName.text = it.name
                        textViewUserContact.text = it.contact
                        textViewUserGender.text = it.gender
                        textViewUserRole.text = it.role.uppercase()
                        textViewRegistrationDate.text = it.registrationDate.substringBefore(" ")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    private fun logout() {
        // Clear session
        sessionManager.logout()
        
        // Navigate to login
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}
