package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stylica.makeupclothing.R
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.User
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.PasswordUtils
import java.text.SimpleDateFormat
import java.util.*

class SignupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val nameEditText = findViewById<EditText>(R.id.editTextName)
        val genderEditText = findViewById<EditText>(R.id.editTextGender)
        val contactEditText = findViewById<EditText>(R.id.editTextContact)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val signupButton = findViewById<Button>(R.id.buttonSignup)

        signupButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val gender = genderEditText.text.toString()
            val contact = contactEditText.text.toString()
            val password = passwordEditText.text.toString()
            
            if (name.isNotEmpty() && gender.isNotEmpty() && contact.isNotEmpty() && password.isNotEmpty()) {
                if (!PasswordUtils.isPasswordValid(password)) {
                    Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                
                // Generate salt and hash password
                val salt = PasswordUtils.generateSalt()
                val hashedPassword = PasswordUtils.hashPassword(password, salt)
                
                val user = User(
                    name = name,
                    gender = gender,
                    contact = contact,
                    registrationDate = getCurrentDate(),
                    role = "user",
                    passwordHash = hashedPassword,
                    passwordSalt = salt
                )
                
                lifecycleScope.launch {
                    try {
                        val database = DatabaseProvider.getDatabase(applicationContext)
                        database.userDao().insertUser(user)
                        Toast.makeText(this@SignupActivity, "Signup successful!", Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (e: Exception) {
                        Toast.makeText(this@SignupActivity, "Signup failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
