package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stylica.makeupclothing.R
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.DatabaseSeeder
import com.stylica.makeupclothing.utils.PasswordUtils
import com.stylica.makeupclothing.utils.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        // Seed database with dummy data on first launch
        DatabaseSeeder.seedDatabase(this)
        
        // Check if already logged in
        if (sessionManager.isLoggedIn()) {
            val userRole = sessionManager.getUserRole()
            when (userRole) {
                Constants.ROLE_ADMIN -> {
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                }
                Constants.ROLE_MODERATOR -> {
                    startActivity(Intent(this, ModeratorActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
            finish()
            return
        }
        
        setContentView(R.layout.activity_login)

        val usernameEditText = findViewById<EditText>(R.id.editTextUsername)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)
        val signupButton = findViewById<Button>(R.id.buttonSignup)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            
            if (username.isNotEmpty() && password.isNotEmpty()) {
                performLogin(username, password)
            } else {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        signupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
    
    private fun performLogin(contact: String, password: String) {
        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val user = database.userDao().getUserByContact(contact)
                
                if (user != null) {
                    // Verify password
                    if (PasswordUtils.verifyPassword(password, user.passwordSalt, user.passwordHash)) {
                        sessionManager.saveUserSession(user.id, user.role)
                        
                        // Role-based navigation
                        when (user.role) {
                            Constants.ROLE_ADMIN -> {
                                Toast.makeText(this@LoginActivity, "Welcome Admin!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, AdminDashboardActivity::class.java))
                            }
                            Constants.ROLE_MODERATOR -> {
                                Toast.makeText(this@LoginActivity, "Welcome Moderator!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, ModeratorActivity::class.java))
                            }
                            else -> {
                                Toast.makeText(this@LoginActivity, "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            }
                        }
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "❌ Invalid password!", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "❌ User not found! Please sign up first.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "❌ Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
