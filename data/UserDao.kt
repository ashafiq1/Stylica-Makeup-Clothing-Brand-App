package data

import androidx.room.*
import model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE name LIKE :name")
    suspend fun searchByName(name: String): List<User>

    @Query("SELECT * FROM users WHERE contact = :contact")
    suspend fun getUserByContact(contact: String): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}