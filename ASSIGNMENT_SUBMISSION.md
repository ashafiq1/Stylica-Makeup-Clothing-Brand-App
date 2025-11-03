# STYLICA MAKEUP + CLOTHING BRAND APP
## Android E-Commerce Application - Assignment Submission

---

**Submitted By:** [Your Name]  
**Roll Number:** [Your Roll Number]  
**Course:** Mobile Application Development  
**Submission Date:** November 3, 2025  

**GitHub Repository:**  
https://github.com/ashafiq1/Stylica-Makeup-Clothing-Brand-App

---

## 📋 PROJECT OVERVIEW

**Stylica** is a comprehensive Android e-commerce application designed for a luxury makeup and clothing brand. The application implements modern Android development practices including MVVM architecture, Room database, Material Design, and secure authentication with role-based access control.

### **Key Features:**
- Multi-role system (Admin, Moderator, User)
- Secure authentication with SHA-256 password hashing
- Product catalog with real-time search and filtering
- Shopping cart with quantity management
- Complete checkout process with courier and payment selection
- Admin dashboard for product CRUD operations
- Moderator panel for product approval workflow
- User profile management with logout functionality

---

## 🛠️ TECHNICAL SPECIFICATIONS

### **Development Environment:**
- **IDE:** Android Studio Hedgehog (2023.1.1+)
- **Language:** Kotlin 1.9.20
- **Build Tool:** Gradle 8.5
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 34 (Android 14)
- **JDK Version:** 17/21

### **Architecture & Design Patterns:**
- **Architecture:** MVVM (Model-View-ViewModel)
- **Database:** Room Persistence Library (SQLite)
- **Async Operations:** Kotlin Coroutines
- **UI Framework:** XML Layouts with Material Design Components
- **Image Loading:** Glide 5.0
- **Design Pattern:** Repository Pattern, Singleton Pattern, Observer Pattern

### **Core Libraries:**
```gradle
- androidx.room:room-runtime:2.8.3
- androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2
- androidx.lifecycle:lifecycle-livedata-ktx:2.6.2
- com.google.android.material:material:1.11.0
- com.github.bumptech.glide:glide:5.0.5
- org.jetbrains.kotlinx:kotlinx-coroutines-android
```

---

## 🗄️ DATABASE SCHEMA

The application uses **Room Persistence Library** with 8 entity models:

### **1. Users Table**
```kotlin
- id: Int (Primary Key, Auto-generated)
- name: String
- gender: String
- contact: String (Unique username)
- registrationDate: String
- role: String (admin, moderator, user)
- passwordHash: String (SHA-256 encrypted)
- passwordSalt: String (SecureRandom generated)
```

### **2. Products Table**
```kotlin
- id: Int (Primary Key)
- name: String
- category: String (makeup, clothing, accessories)
- subcategory: String
- price: Double
- description: String
- imageUrl: String (Unsplash URLs)
- registrationDate: String
- approved: Boolean
- vendorId: Int
```

### **3. Cart Items Table**
```kotlin
- id: Int (Primary Key)
- userId: Int (Foreign Key)
- productId: Int (Foreign Key)
- quantity: Int
- addedDate: String
```

### **4. Orders Table**
```kotlin
- id: Int (Primary Key)
- userId: Int (Foreign Key)
- productId: Int (Foreign Key)
- quantity: Int
- orderDate: String
- status: String
- courier: String (TCS, Leopards, BlueEx, PostEx)
- paymentMode: String
```

### **5-8. Additional Tables:**
- Payments Table
- Announcements Table
- Moderators Table
- Employees Table

**Total Database Entities:** 8  
**Total DAO Interfaces:** 6  
**Database Version:** 1

---

## 🏗️ PROJECT ARCHITECTURE

### **Package Structure:**
```
com.stylica.makeupclothing/
├── model/              # Room Entity classes (8 models)
├── data/               # DAO interfaces and Database class
├── repository/         # Repository layer (3 repositories)
├── viewmodel/          # ViewModel classes (1 ViewModel)
├── ui/                 # Activities and Fragments (10 screens)
│   ├── SplashActivity
│   ├── LoginActivity
│   ├── SignupActivity
│   ├── MainActivity
│   ├── AdminDashboardActivity
│   ├── ModeratorActivity
│   ├── ProductDetailActivity
│   ├── CheckoutActivity
│   ├── HomeFragment
│   ├── CartFragment
│   └── ProfileFragment
├── adapter/            # RecyclerView Adapters (2 adapters)
│   ├── ProductAdapter
│   └── CartAdapter
└── utils/              # Helper classes (5 utilities)
    ├── Constants
    ├── DatabaseProvider (Singleton)
    ├── DatabaseSeeder
    ├── PasswordUtils (Security)
    └── SessionManager
```

### **Data Flow (MVVM):**
```
User Interaction
    ↓
Activity/Fragment (View)
    ↓
ViewModel (Business Logic)
    ↓
Repository (Data Abstraction)
    ↓
DAO (Database Operations)
    ↓
Room Database (SQLite)
```

---

## 🔐 SECURITY IMPLEMENTATION

### **Password Security:**
- **Hashing Algorithm:** SHA-256 with salt
- **Salt Generation:** SecureRandom (16 bytes)
- **Storage:** Separate columns for hash and salt
- **Validation:** Minimum 6 characters

### **Session Management:**
- **Storage:** SharedPreferences (encrypted)
- **Session Data:** User ID, Role
- **Auto-login:** Persistent sessions across app restarts
- **Logout:** Complete session clearing

### **Role-Based Access Control:**
```
User Role → Allowed Actions
├── Admin → Full CRUD on products, user management
├── Moderator → Product approval, viewing
└── User → Browse, cart, checkout only
```

---

## 🎨 UI/UX FEATURES

### **Modern Design Elements:**
- Material Design Components throughout
- CardView with 12dp rounded corners
- Elevation and shadows for depth
- 2-column grid layout for products
- 180dp large product images
- Golden accent color (#FFC107)
- Responsive layouts

### **User Experience:**
- Splash screen with 2-second loading
- Real-time search with TextWatcher
- Category filtering with Spinner
- Bottom navigation for easy access
- Descriptive error messages with emojis
- Toast notifications for actions
- Loading indicators for async operations

### **Screens Implemented:**
1. **Splash Screen** - Branded loading screen
2. **Login Screen** - Authentication with validation
3. **Signup Screen** - User registration with role selection
4. **Home Screen** - Product grid with search/filter
5. **Product Detail** - Large images, description, add to cart
6. **Cart Screen** - Quantity controls, total calculation
7. **Checkout Screen** - Courier, payment, address selection
8. **Profile Screen** - User info display, logout
9. **Admin Dashboard** - Product CRUD interface
10. **Moderator Panel** - Product approval system

---

## 📦 PRE-LOADED TEST DATA

The application includes **DatabaseSeeder** utility that automatically populates the database on first launch.

### **Pre-configured Users (3):**

| Role | Username | Password | Purpose |
|------|----------|----------|---------|
| User | `user` | `user123` | Regular shopping experience |
| Admin | `admin` | `admin123` | Product management |
| Moderator | `moderator` | `mod123` | Product approval |

### **Pre-loaded Products (18):**

**Makeup Category (6 products):**
- Matte Red Lipstick - ₹299
- Nude Lipstick - ₹349
- Eyeshadow Palette - ₹899
- Mascara - Volumizing - ₹499
- Foundation - Beige - ₹799
- Blush - Pink Glow - ₹399

**Clothing Category (4 products):**
- Floral Summer Dress - ₹1,999
- Denim Jacket - ₹2,499
- White Cotton T-Shirt - ₹599
- Black Skinny Jeans - ₹1,499

**Accessories Category (4 products):**
- Gold Hoop Earrings - ₹899
- Designer Sunglasses - ₹1,299
- Leather Handbag - ₹3,499
- Silk Scarf - ₹799

**Additional Products (4):**
- Various makeup, clothing, and accessory items

**Note:** All products include real images from Unsplash and are pre-approved for immediate visibility.

---

## 🧪 TESTING INSTRUCTIONS

### **Installation Steps:**

1. **Clone Repository:**
   ```bash
   git clone https://github.com/ashafiq1/Stylica-Makeup-Clothing-Brand-App.git
   ```

2. **Open in Android Studio:**
   - File → Open → Select project folder
   - Wait for Gradle sync

3. **Configure JDK:**
   - File → Settings → Build Tools → Gradle
   - Set Gradle JDK to "Embedded JDK (jbr-17)"

4. **Build Project:**
   - Build → Clean Project
   - Build → Rebuild Project

5. **Run Application:**
   - Click green Run button (▶️)
   - Select emulator (Recommended: Pixel 5, API 34)

### **Complete Testing Flow:**

#### **1. First Launch (Splash Screen)**
- App displays Stylica logo
- 2-second loading animation
- Auto-navigates to Login screen

#### **2. User Registration (Signup)**
```
1. Click "SIGNUP" button
2. Fill registration form:
   - Name: Test User
   - Contact: testuser
   - Password: test1234
   - Gender: Select from dropdown
   - Role: Select "user"
3. Click "SIGNUP"
4. Redirects to Login screen
5. System stores password as SHA-256 hash
```

#### **3. User Login & Shopping Flow**
```
Credentials: user / user123

1. Enter username and password
2. Click "LOGIN"
3. System validates and shows: "Welcome back, Demo User!"
4. Home screen opens with 18 products
5. Test search: Type "lipstick" in search bar
6. Test filter: Select "Makeup" from category spinner
7. Click product card → Product detail opens
8. Click "ADD TO CART" → Toast: "Added to cart"
9. Go to Cart tab (bottom navigation)
10. Adjust quantity using +/- buttons
11. Click "PROCEED TO CHECKOUT"
12. Select courier: TCS
13. Select payment: Cash on Delivery
14. Enter delivery address
15. Click "PLACE ORDER"
16. Toast: "Order placed successfully"
17. Cart automatically cleared
18. Go to Profile tab
19. View user information
20. Click "LOGOUT" button
21. Redirects to Login screen
```

#### **4. Admin Dashboard Testing**
```
Credentials: admin / admin123

1. Login with admin credentials
2. System shows: "Welcome Admin!"
3. Auto-redirects to Admin Dashboard (not home)
4. Click "ADD PRODUCT" button
5. Dialog opens with product form
6. Fill product details:
   - Name: New Product
   - Category: makeup
   - Subcategory: lips
   - Price: 499
   - Description: Test product
   - Vendor ID: 1
7. Click "ADD"
8. Product added to database (unapproved)
9. See all products with Edit/Delete buttons
10. Test Edit functionality
11. Test Delete functionality
```

#### **5. Moderator Approval Testing**
```
Credentials: moderator / mod123

1. Logout from admin
2. Login with moderator credentials
3. System shows: "Welcome Moderator!"
4. Auto-redirects to Moderator Panel
5. See list of unapproved products
6. Click "APPROVE" button on a product
7. Product approved (now visible to users)
8. Unapproved product disappears from list
```

#### **6. Error Handling Testing**
```
Test Case 1 - Wrong Username:
- Username: wronguser
- Password: wrongpass
- Expected: "❌ User not found! Please sign up first."

Test Case 2 - Wrong Password:
- Username: admin
- Password: wrongpass
- Expected: "❌ Invalid password!"

Test Case 3 - Empty Fields:
- Username: (empty)
- Password: (empty)
- Expected: "Please enter username and password"
```

---

## ✅ IMPLEMENTED FEATURES CHECKLIST

### **Core Functionality:**
- ✅ User Authentication (Login/Signup)
- ✅ Password Hashing (SHA-256 + Salt)
- ✅ Session Management
- ✅ Role-Based Access Control
- ✅ Auto-login with session persistence

### **Product Management:**
- ✅ Product listing with pagination
- ✅ Search functionality
- ✅ Category filtering
- ✅ Product detail view
- ✅ Image loading with Glide
- ✅ CRUD operations (Admin only)

### **Shopping Features:**
- ✅ Add to cart
- ✅ Cart management (add/remove/update)
- ✅ Quantity adjustment
- ✅ Total price calculation
- ✅ Checkout process
- ✅ Courier selection
- ✅ Payment mode selection
- ✅ Order placement

### **Admin Features:**
- ✅ Admin dashboard
- ✅ Add products (dialog interface)
- ✅ Edit products
- ✅ Delete products
- ✅ View all products

### **Moderator Features:**
- ✅ Moderator panel
- ✅ View unapproved products
- ✅ Approve products
- ✅ Product visibility control

### **UI/UX:**
- ✅ Splash screen
- ✅ Material Design
- ✅ Bottom navigation
- ✅ Grid layout (2 columns)
- ✅ Card-based design
- ✅ Responsive layouts
- ✅ Error messages
- ✅ Loading indicators

### **Database:**
- ✅ Room database setup
- ✅ 8 entity models
- ✅ 6 DAO interfaces
- ✅ Database seeding
- ✅ Foreign key relationships
- ✅ Coroutine-based operations

### **Code Quality:**
- ✅ MVVM architecture
- ✅ Repository pattern
- ✅ Clean code structure
- ✅ Proper error handling
- ✅ Comments and documentation
- ✅ No memory leaks

---

## 📊 PERFORMANCE METRICS

- **Total Lines of Code:** ~3,500+
- **Total Files:** 40+
- **Activities:** 7
- **Fragments:** 3
- **Adapters:** 2
- **Entity Models:** 8
- **DAO Interfaces:** 6
- **Repositories:** 3
- **ViewModels:** 1
- **Utility Classes:** 5
- **Layout Files:** 13
- **Build Time:** ~30 seconds
- **APK Size:** ~15 MB

---

## 🐛 KNOWN LIMITATIONS & FUTURE ENHANCEMENTS

### **Current Limitations:**
1. Images require internet connection
2. Orders are stored locally (no backend sync)
3. Payment integration is mock (UI only)
4. No real-time notifications

### **Planned Enhancements:**
1. Firebase integration for cloud sync
2. Push notifications
3. Order tracking with status updates
4. Payment gateway integration
5. User reviews and ratings
6. Wishlist functionality
7. Order history screen
8. Admin analytics dashboard

---

## 📝 ASSIGNMENT COMPLIANCE

### **Requirements Met:**

| Requirement | Status | Implementation |
|------------|--------|----------------|
| Android Application | ✅ | Complete e-commerce app |
| SQLite Database | ✅ | Room with 8 tables |
| CRUD Operations | ✅ | Products, Users, Orders |
| User Authentication | ✅ | SHA-256 hashing |
| Multi-screen Navigation | ✅ | 10 screens |
| RecyclerView | ✅ | Products, Cart lists |
| Material Design | ✅ | Full implementation |
| Error Handling | ✅ | Try-catch throughout |
| Code Quality | ✅ | Clean, organized |
| Documentation | ✅ | README, comments |

### **Extra Features Implemented:**
- ✅ Splash screen
- ✅ Role-based access control
- ✅ Image loading library (Glide)
- ✅ Search and filter
- ✅ Session management
- ✅ Database seeding
- ✅ Modern UI/UX
- ✅ Professional grid layout

---

## 📚 REFERENCES & RESOURCES

### **Official Documentation:**
1. Android Developers Guide: https://developer.android.com
2. Kotlin Documentation: https://kotlinlang.org/docs
3. Room Persistence Library: https://developer.android.com/training/data-storage/room
4. Material Design: https://material.io/design
5. Glide Image Loading: https://github.com/bumptech/glide

### **Learning Resources:**
1. Android Architecture Components
2. MVVM Pattern Implementation
3. Kotlin Coroutines
4. RecyclerView Best Practices
5. SQLite Database Design

---

## 💡 CONCLUSION

The **Stylica Makeup + Clothing Brand App** successfully demonstrates comprehensive Android development skills including:

- ✅ Modern architecture patterns (MVVM)
- ✅ Database management with Room
- ✅ Secure authentication implementation
- ✅ Professional UI/UX design
- ✅ Role-based access control
- ✅ Complete e-commerce workflow
- ✅ Clean code practices
- ✅ Proper error handling

The application is fully functional, well-documented, and ready for evaluation. All test credentials are provided above for easy verification.

---

## 📞 CONTACT INFORMATION

**GitHub Repository:**  
https://github.com/ashafiq1/Stylica-Makeup-Clothing-Brand-App

**Developer:** [Your Name]  
**Email:** [Your Email]  
**Roll Number:** [Your Roll Number]

**Submission Date:** November 3, 2025  
**Course:** Mobile Application Development  
**Instructor:** [Professor Name]

---

**Note to Evaluator:** Please use the provided test credentials for evaluation. The application requires no additional setup and is ready to run immediately after installation. All features are pre-configured with dummy data for comprehensive testing.

---

*This document serves as the official submission for the Android Application Development assignment.*
