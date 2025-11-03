package com.stylica.makeupclothing.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: String,
    val contact: String,
    val registrationDate: String,
    val role: String, // "admin", "moderator", "user"
    val passwordHash: String = "",
    val passwordSalt: String = ""
)
