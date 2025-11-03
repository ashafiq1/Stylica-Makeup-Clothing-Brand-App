package repository

import data.AppDatabase
import model.User

class UserRepository(private val database: AppDatabase) {
    
    suspend fun insertUser(user: User) {
        database.userDao().insertUser(user)
    }
    
    suspend fun updateUser(user: User) {
        database.userDao().updateUser(user)
    }
    
    suspend fun deleteUser(user: User) {
        database.userDao().deleteUser(user)
    }
    
    suspend fun getUserById(id: Int): User? {
        return database.userDao().getUserById(id)
    }
    
    suspend fun getAllUsers(): List<User> {
        return database.userDao().getAllUsers()
    }
    
    suspend fun searchByName(name: String): List<User> {
        return database.userDao().searchByName("%$name%")
    }
}
