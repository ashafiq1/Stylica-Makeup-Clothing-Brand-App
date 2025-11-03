package model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moderators")
data class Moderator(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val assignedCategory: String,
    val assignedDate: String
)
