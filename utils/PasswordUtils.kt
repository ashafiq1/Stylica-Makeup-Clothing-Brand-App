package utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordUtils {
    
    /**
     * Hashes a password using SHA-256 with a salt
     */
    fun hashPassword(password: String, salt: String): String {
        val saltedPassword = password + salt
        val bytes = saltedPassword.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return bytesToHex(digest)
    }
    
    /**
     * Generates a random salt for password hashing
     */
    fun generateSalt(): String {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return Base64.getEncoder().encodeToString(salt)
    }
    
    /**
     * Verifies a password against a stored hash
     */
    fun verifyPassword(password: String, salt: String, hashedPassword: String): Boolean {
        val hashToVerify = hashPassword(password, salt)
        return hashToVerify == hashedPassword
    }
    
    /**
     * Converts byte array to hexadecimal string
     */
    private fun bytesToHex(bytes: ByteArray): String {
        val hexChars = "0123456789abcdef"
        val result = StringBuilder(bytes.size * 2)
        bytes.forEach {
            val i = it.toInt()
            result.append(hexChars[i shr 4 and 0x0f])
            result.append(hexChars[i and 0x0f])
        }
        return result.toString()
    }
    
    /**
     * Validates password strength
     */
    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }
}
