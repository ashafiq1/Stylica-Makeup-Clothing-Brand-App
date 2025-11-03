package ui

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import d.R
import kotlinx.coroutines.launch
import model.Order
import utils.Constants
import utils.DatabaseProvider
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {
    private var userId: Int = 1 // TODO: Get from session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val spinnerCourier = findViewById<Spinner>(R.id.spinnerCourier)
        val radioGroupPayment = findViewById<RadioGroup>(R.id.radioGroupPayment)
        val editTextAddress = findViewById<EditText>(R.id.editTextAddress)
        val buttonPlaceOrder = findViewById<Button>(R.id.buttonPlaceOrder)

        // Setup courier spinner
        val couriers = arrayOf("DHL", "FedEx", "UPS", "Local Delivery")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, couriers)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourier.adapter = adapter

        buttonPlaceOrder.setOnClickListener {
            placeOrder(
                courier = spinnerCourier.selectedItem.toString(),
                paymentMode = getSelectedPaymentMode(radioGroupPayment),
                address = editTextAddress.text.toString()
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
            Toast.makeText(this, "Please enter delivery address", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val database = DatabaseProvider.getDatabase(applicationContext)
                val cartItems = database.cartItemDao().getCartItemsByUser(userId)

                if (cartItems.isEmpty()) {
                    Toast.makeText(this@CheckoutActivity, "Cart is empty", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Create orders for each cart item
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

                // Clear cart
                database.cartItemDao().clearCart(userId)

                Toast.makeText(this@CheckoutActivity, "Order placed successfully!", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CheckoutActivity, "Failed to place order: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
