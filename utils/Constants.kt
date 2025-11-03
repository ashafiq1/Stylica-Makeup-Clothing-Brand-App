package utils

object Constants {
    // Database
    const val DATABASE_NAME = "stylica_database"
    const val DATABASE_VERSION = 1
    
    // User Roles
    const val ROLE_ADMIN = "admin"
    const val ROLE_MODERATOR = "moderator"
    const val ROLE_USER = "user"
    
    // Order Status
    const val ORDER_STATUS_PENDING = "pending"
    const val ORDER_STATUS_CONFIRMED = "confirmed"
    const val ORDER_STATUS_SHIPPED = "shipped"
    const val ORDER_STATUS_DELIVERED = "delivered"
    const val ORDER_STATUS_CANCELLED = "cancelled"
    
    // Payment Modes
    const val PAYMENT_MODE_CASH = "cash"
    const val PAYMENT_MODE_CARD = "card"
    const val PAYMENT_MODE_ONLINE = "online"
    
    // Payment Status
    const val PAYMENT_STATUS_PENDING = "pending"
    const val PAYMENT_STATUS_COMPLETED = "completed"
    const val PAYMENT_STATUS_FAILED = "failed"
    
    // Product Categories
    const val CATEGORY_MAKEUP = "makeup"
    const val CATEGORY_CLOTHING = "clothing"
    const val CATEGORY_ACCESSORIES = "accessories"
    
    // Shared Preferences
    const val PREF_NAME = "stylica_prefs"
    const val PREF_USER_ID = "user_id"
    const val PREF_USER_ROLE = "user_role"
    const val PREF_IS_LOGGED_IN = "is_logged_in"
}
