package com.stylica.makeupclothing.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.stylica.makeupclothing.model.*

@Database(
    entities = [
        Product::class,
        User::class,
        Order::class,
        Payment::class,
        Announcement::class,
        Moderator::class,
        Employee::class,
        CartItem::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentDao(): PaymentDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun cartItemDao(): CartItemDao
}
