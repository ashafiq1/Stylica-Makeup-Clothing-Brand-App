package com.stylica.makeupclothing.ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.stylica.makeupclothing.R
import kotlinx.coroutines.launch
import com.stylica.makeupclothing.model.Order
import com.stylica.makeupclothing.utils.Constants
import com.stylica.makeupclothing.utils.DatabaseProvider
import com.stylica.makeupclothing.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    private var userId: Int = -1
    private var buyNowProductId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        userId = SessionManager(this).getUserId()
        buyNowProductId = intent.getIntExtra("BUY_NOW_PRODUCT_ID", -1)

        val spinnerCourier = findViewById<Spinner>(R.id.spinnerCourier)
        val radioGroupPayment = findViewById<RadioGroup>(R.id.radioGroupPayment)
        val editTextAddress = findViewById<EditText>(R.id.editTextAddress)
        val buttonPlaceOrder = findViewById<Button>(R.id.buttonPlaceOrder)

        val couriers = arrayOf("DHL", "FedEx", "UPS", "Local Delivery")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, couriers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourier.adapter = adapter

        buttonPlaceOrder.setOnClickListener {
            placeOrder(
                courier = spinnerCourier.selectedItem.toString(),
                paymentMode = getSelectedPaymentMode(radioGroupPayment),
                address = editTextAddress.text.toString().trim()
            )
        }
    }

    private fun getSelectedPaymentMode(radioGroup: RadioGroup): String {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radioButtonCash -> Constants.PAYMENT_MODE_CASH
            R.id.radioButtonCard -> Constants.PAYMENT_MODE_CARD
            R.id.radioButtonOnline -> Constants.PAYMENT_MODE_ONLINE
            else -> Constants.PAYMENT_MODE_CASH
        }
    }

    private fun placeOrder(courier: String, paymentMode: String, address: String) {
        if (address.isEmpty()) {
            Toast.makeText(this, "Please enter a delivery address", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == -1) {
            Toast.makeText(this, "Session error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)

                if (buyNowProductId != -1) {
                    // Buy Now mode: place order for single product
                    val order = Order(
                        userId = userId,
                        productId = buyNowProductId,
                        quantity = 1,
                        orderDate = getCurrentDate(),
                        status = Constants.ORDER_STATUS_PENDING,
                        courier = courier,
                        paymentMode = paymentMode
                    )
                    database.orderDao().insertOrder(order)
                    Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    // Cart checkout mode
                    val cartItems = database.cartItemDao().getCartItemsByUser(userId)

                    if (cartItems.isEmpty()) {
                        Toast.makeText(this@CheckoutActivity, "Your cart is empty", Toast.LENGTH_SHORT).show()
                        return@launch
                    }

                    cartItems.forEach { cartItem ->
                        val order = Order(
                            userId = userId,
                            productId = cartItem.productId,
                            quantity = cartItem.quantity,
                            orderDate = getCurrentDate(),
                            status = Constants.ORDER_STATUS_PENDING,
                            courier = courier,
                            paymentMode = paymentMode
                        )
                        database.orderDao().insertOrder(order)
                    }

                    database.cartItemDao().clearCart(userId)
                    Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    }
}
