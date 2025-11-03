package com.stylica.makeupclothing.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.SessionManager

class SplashActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        
        sessionManager = SessionManager(this)
        
        // Delay for 2 seconds then check login status
        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Navigate based on user role
                when (sessionManager.getUserRole()) {
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
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}
