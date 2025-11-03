# Stylica Makeup + Clothing Brand App

A full-featured Android e-commerce application for luxury makeup and clothing products with multi-role support (Admin, Moderator, User).

## 🎯 Features

### User Features
- Browse products by category (Makeup, Clothing, Accessories)
- Add products to cart
- Place orders with courier and payment mode selection
- Track order status
- User profile management

### Admin Features
- Add, update, delete products
- Manage user accounts
- View and manage all orders
- Manage moderators and employees
- Create announcements
- Generate reports

### Moderator Features
- Approve vendor products
- Track delivery confirmations
- Manage assigned product categories

## 🏗️ Project Structure

```
├── model/              # Room entities (Product, User, Order, Payment, etc.)
├── data/               # Room DAOs and Database
├── repository/         # Repository pattern for data access
├── viewmodel/          # ViewModels for MVVM architecture
├── ui/                 # Activities and Fragments
├── adapter/            # RecyclerView adapters
├── utils/              # Helper classes and constants
└── res/                # Resources (layouts, menus, etc.)
```

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (SQLite)
- **UI**: XML Layouts + Material Design Components
- **Async**: Kotlin Coroutines
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)

## 📦 Dependencies

- AndroidX Core KTX
- AppCompat
- Material Design Components
- ConstraintLayout
- RecyclerView
- Room Persistence Library
- Lifecycle (ViewModel, LiveData)
- Kotlin Coroutines

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 11 or higher
- Android SDK with API 34

### Installation

1. Open the project in Android Studio
2. Sync Gradle dependencies
3. Build and run on emulator or physical device

### Database Schema

**Users Table**
- id, name, gender, contact, registrationDate, role

**Products Table**
- id, name, category, subcategory, price, description, imageUrl, registrationDate, approved, vendorId

**Orders Table**
- id, userId, productId, quantity, orderDate, status, courier, paymentMode

**Payments Table**
- id, orderId, amount, paymentMode, paymentDate, status

**Cart Items Table**
- id, userId, productId, quantity, addedDate

**Announcements Table**
- id, title, message, createdDate, adminId

**Moderators Table**
- id, userId, assignedCategory, assignedDate

**Employees Table**
- id, userId, department, hireDate

## 📱 Screens

- **Login/Signup**: User authentication
- **Home**: Product listing with categories
- **Product Detail**: Individual product view
- **Cart**: Shopping cart management
- **Checkout**: Order placement
- **Profile**: User profile and settings
- **Admin Dashboard**: CRUD operations for all entities
- **Moderator Panel**: Product approval interface

## 🔐 User Roles

- **Admin**: Full system access
- **Moderator**: Product approval and delivery tracking
- **User**: Browse and purchase products

## 📝 TODO

- [ ] Implement image loading (Glide/Picasso)
- [ ] Add authentication with password hashing
- [ ] Implement product detail screen
- [ ] Add checkout and payment flow
- [ ] Create moderator approval interface
- [ ] Add search and filter functionality
- [ ] Implement order tracking
- [ ] Add notifications
- [ ] Create report generation for admin

## 📄 License

This project is for educational purposes.

## 👥 Contact

For questions or support, please contact the development team.
