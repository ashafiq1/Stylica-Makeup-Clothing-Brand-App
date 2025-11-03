package com.stylica.makeupclothing.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREF_NAME,
        Context.MODE_PRIVATE
    )
    
    fun saveUserSession(userId: Int, userRole: String) {
        prefs.edit().apply {
            putInt(Constants.PREF_USER_ID, userId)
            putString(Constants.PREF_USER_ROLE, userRole)
            putBoolean(Constants.PREF_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    fun getUserId(): Int {
        return prefs.getInt(Constants.PREF_USER_ID, -1)
    }
    
    fun getUserRole(): String {
        return prefs.getString(Constants.PREF_USER_ROLE, Constants.ROLE_USER) ?: Constants.ROLE_USER
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(Constants.PREF_IS_LOGGED_IN, false)
    }
    
    fun logout() {
        prefs.edit().clear().apply()
    }
}
