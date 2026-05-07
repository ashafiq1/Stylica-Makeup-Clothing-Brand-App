# Stylica — Makeup & Clothing Brand App

A full-featured Android e-commerce application for luxury makeup and clothing products, with multi-role support (Admin, Moderator, User). Built with modern Android architecture following MVVM pattern, Material Design 3 guidelines, and a custom pink brand theme.

---

## Screenshots Flow

| Splash | Login | Home | Product Detail |
|--------|-------|------|----------------|
| Pink gradient + Stylica logo | Role-based routing | Category chips + product grid | Add to Cart / Buy Now |

| Cart | Checkout | Admin Dashboard | Moderator Panel |
|------|----------|-----------------|-----------------|
| Quantity controls + total | Courier + payment selection | 6 clickable stat cards | 4-tab dashboard |

---

## Features

### User
- Secure authentication — SHA-256 + SecureRandom salt password hashing
- Browse 19+ pre-loaded products across 5 categories
- Horizontal category chip bar: **All · Makeup · Clothing · Accessories · Shoes · Sale 50% OFF**
- Sale mode: 50% discounted price + badge overlay on product cards
- Product detail screen with full description, image, price
- Add to Cart (duplicate prevention) and Buy Now
- Cart with quantity controls (+/−/remove) and live total
- Checkout with courier selection (TCS, Leopards, BlueEx, PostEx) and payment mode
- User profile with account info and logout
- Auto-login with session persistence (SharedPreferences)

### Admin
- **Dashboard with 6 clickable stat cards:**
  - Products → scrolls to All Products list
  - Revenue This Month → breakdown dialog (delivered/confirmed/pending)
  - Users → full users list dialog
  - Orders → all orders dialog
  - Moderators → moderators list dialog
  - Couriers → courier partners dialog
- Add product via dialog (name, category spinner, price, image URL, description)
- Delete product with confirmation dialog
- Payment & Transactions summary card
- View all products list with status badges
- Auto-redirect to Admin Dashboard on login

### Moderator
- **4-tab dashboard panel:**
  - **Dashboard tab** — stats cards (Revenue, Products, Sold Out, Total Orders) + order status breakdown (Pending/Confirmed/Delivered) + product quality overview (description & image coverage). All cards are tappable and jump to the relevant tab.
  - **Approvals tab** — pending products list with Approve/Reject buttons; empty state when queue is clear
  - **Orders tab** — all orders with color-coded status badges (orange=Pending, blue=Confirmed, green=Delivered); one-tap status update (Pending → Confirmed → Delivered)
  - **Add Product tab** — inline form to submit a new product as pending for admin review
- Auto-redirect to Moderator Panel on login

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| Architecture | MVVM + Repository Pattern |
| Database | Room (SQLite), version 2 |
| UI | XML Layouts + Material Design Components |
| Async | Kotlin Coroutines + lifecycleScope |
| Image Loading | Glide |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 34 (Android 14) |
| Build | Gradle 8.5, JDK 17 |

---

## Project Structure

```
app/src/main/java/com/stylica/makeupclothing/
├── model/
│   ├── Product.kt          # id, name, category, price, stock, approved, vendorId …
│   ├── User.kt             # id, name, contact, role, passwordHash, passwordSalt …
│   ├── Order.kt            # id, userId, productId, quantity, status, courier …
│   ├── CartItem.kt
│   ├── Payment.kt
│   └── Announcement.kt
├── data/
│   ├── AppDatabase.kt      # Room DB, version 2
│   ├── ProductDao.kt
│   ├── UserDao.kt
│   ├── OrderDao.kt         # includes getOrdersByStatus()
│   ├── CartItemDao.kt      # includes getCartItemByUserAndProduct() (duplicate prevention)
│   └── PaymentDao.kt
├── repository/
│   └── ProductRepository.kt
├── viewmodel/
│   └── ProductViewModel.kt
├── ui/
│   ├── SplashActivity.kt
│   ├── LoginActivity.kt
│   ├── SignupActivity.kt
│   ├── MainActivity.kt
│   ├── AdminDashboardActivity.kt
│   ├── ModeratorActivity.kt
│   ├── ProductDetailActivity.kt
│   ├── CheckoutActivity.kt
│   ├── HomeFragment.kt
│   ├── CartFragment.kt
│   └── ProfileFragment.kt
├── adapter/
│   ├── ProductAdapter.kt       # sale mode + badge overlay
│   ├── CartAdapter.kt
│   ├── AdminProductAdapter.kt  # status badge + delete button
│   ├── PendingProductAdapter.kt # approve/reject buttons
│   └── OrderAdapter.kt         # status badge + one-tap update
└── utils/
    ├── Constants.kt
    ├── DatabaseProvider.kt     # singleton, fallbackToDestructiveMigration
    ├── DatabaseSeeder.kt       # seeds users, products, orders on first launch
    ├── PasswordUtils.kt        # SHA-256 + salt
    └── SessionManager.kt       # SharedPreferences session
```

---

## Getting Started

### Prerequisites
- Android Studio Hedgehog 2023.1.1 or later
- JDK 17 (use Android Studio's embedded JBR)
- Android SDK API 34
- Emulator: Pixel with API 34 recommended

### Clone & Open

```bash
git clone https://github.com/ashafiq1/Stylica-Makeup-Clothing-Brand-App.git
```

1. Open Android Studio → **File → Open** → select the cloned folder
2. Go to **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
3. Set **Gradle JDK** to **Embedded JDK (jbr-17)**
4. Click **File → Sync Project with Gradle Files**
5. **Build → Clean Project** then **Build → Rebuild Project**
6. Start emulator from Device Manager, then press **▶ Run**

> **Important:** If you previously installed an older version of the app, uninstall it from the emulator before running. The database is re-seeded on fresh install.

---

## Test Credentials

| Role | Username | Password | Redirects To |
|------|----------|----------|-------------|
| User | `user` | `user123` | Home screen |
| Admin | `admin` | `admin123` | Admin Dashboard |
| Moderator | `moderator` | `mod123` | Moderator Panel |

---

## Pre-loaded Data (seeded on first launch)

### Products — 14 approved + 5 pending approval

**Makeup (6 approved)**
- Matte Red Lipstick — Rs 299
- Nude Lipstick — Rs 349
- Eyeshadow Palette — Rs 899
- Mascara Volumizing — Rs 499
- Foundation Beige — Rs 799
- Blush Pink Glow — Rs 399 *(sold out, stock = 0)*

**Clothing (4 approved)**
- Floral Summer Dress — Rs 1,999
- Denim Jacket — Rs 2,499
- White Cotton T-Shirt — Rs 599
- Black Skinny Jeans — Rs 1,499

**Accessories (4 approved)**
- Gold Hoop Earrings — Rs 899
- Designer Sunglasses — Rs 1,299
- Leather Handbag — Rs 3,499
- Silk Scarf — Rs 799 *(sold out, stock = 0)*

**Pending Approval (5 — visible only to Moderator)**
- Rose Gold Highlighter — Rs 649
- Embroidered Kurta — Rs 2,199
- Pearl Necklace Set — Rs 1,799
- Velvet Lip Gloss — Rs 449
- Printed Palazzo Pants — Rs 1,299

### Orders — 7 dummy orders
- 2 Delivered, 2 Confirmed, 3 Pending

---

## Database Schema

**products** — id, name, category, subcategory, price, description, imageUrl, registrationDate, approved, vendorId, stock

**users** — id, name, gender, contact, registrationDate, role, passwordHash, passwordSalt

**orders** — id, userId, productId, quantity, orderDate, status, courier, paymentMode

**cart_items** — id, userId, productId, quantity, addedDate

**payments** — id, orderId, amount, paymentMode, paymentDate, status

**announcements** — id, title, message, createdDate, adminId

---

## Security

- Passwords hashed with **SHA-256 + SecureRandom salt** (never stored as plain text)
- Role-Based Access Control — Admin / Moderator / User routes are enforced at login
- Session stored in SharedPreferences, cleared on logout
- Duplicate cart entries prevented at DAO level

---

## Architecture

```
UI (Activity / Fragment)
       ↓
   ViewModel
       ↓
   Repository
       ↓
     DAO
       ↓
 Room Database (SQLite)
```

- **MVVM** for clean UI / data separation
- **Repository pattern** for single source of truth
- **Singleton** DatabaseProvider with double-checked locking
- **Coroutines** + `lifecycleScope` for all async DB operations
- `fallbackToDestructiveMigration` handles schema upgrades during development

---

## Troubleshooting

| Problem | Solution |
|---------|----------|
| Gradle sync fails | File → Settings → Gradle → set JDK to Embedded JBR-17 |
| Unresolved reference R | Build → Clean Project → Rebuild Project |
| App shows old version / no data | Uninstall from emulator, then reinstall |
| Images not loading | Enable internet on emulator (check Wi-Fi icon) |
| Login fails | Check username/password match the table above |
| Build error after pull | Git → Pull → then Clean Project → Rebuild |

---

## Completed Features

- [x] SHA-256 password hashing with salt
- [x] Role-based login routing (Admin / Moderator / User)
- [x] Category chip bar (Makeup, Clothing, Accessories, Shoes, Sale 50% OFF)
- [x] Sale mode with 50% badge overlay
- [x] Add to Cart with duplicate prevention
- [x] Cart quantity controls and live total
- [x] Buy Now → direct checkout
- [x] Checkout with courier + payment selection
- [x] Admin dashboard with 6 clickable stat cards
- [x] Admin: add / delete products
- [x] Moderator 4-tab panel (Dashboard, Approvals, Orders, Add Product)
- [x] Order tracking with status updates (Pending → Confirmed → Delivered)
- [x] Product quality overview in Moderator dashboard
- [x] Revenue breakdown (delivered vs confirmed vs pending)
- [x] Sold-out product tracking (stock field)
- [x] Session persistence with auto-login
- [x] Pink brand theme throughout (#E91E8C)
- [x] Glide image loading with Unsplash product images
- [x] Database seeding (users, products, orders) on first launch

---

## License

Developed for educational purposes as part of an Android Development course project.

## Developer

**GitHub**: [ashafiq1](https://github.com/ashafiq1)
**Repository**: [Stylica-Makeup-Clothing-Brand-App](https://github.com/ashafiq1/Stylica-Makeup-Clothing-Brand-App)

---

**Version**: 2.0 | **Last Updated**: May 2026 | **Status**: Active Development
