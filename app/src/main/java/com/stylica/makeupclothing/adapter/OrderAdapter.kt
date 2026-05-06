package com.stylica.makeupclothing.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.stylica.makeupclothing.R
import com.stylica.makeupclothing.model.Order

data class OrderItem(
    val order: Order,
    val productName: String,
    val productPrice: Double
)

class OrderAdapter(
    private val items: MutableList<OrderItem>,
    private val onStatusChange: (Order, String) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textOrderId: TextView = itemView.findViewById(R.id.textOrderId)
        val textStatus: TextView = itemView.findViewById(R.id.textOrderStatus)
        val textProductName: TextView = itemView.findViewById(R.id.textOrderProductName)
        val textQty: TextView = itemView.findViewById(R.id.textOrderQty)
        val textTotal: TextView = itemView.findViewById(R.id.textOrderTotal)
        val textDate: TextView = itemView.findViewById(R.id.textOrderDate)
        val textPayment: TextView = itemView.findViewById(R.id.textOrderPayment)
        val buttonUpdateStatus: MaterialButton = itemView.findViewById(R.id.buttonUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = items[position]
        val order = item.order

        holder.textOrderId.text = "Order #${order.id}"
        holder.textProductName.text = item.productName
        holder.textQty.text = "Qty: ${order.quantity}"
        holder.textTotal.text = "Rs ${(order.quantity * item.productPrice).toLong()}"
        holder.textDate.text = order.orderDate.take(10)
        holder.textPayment.text = order.paymentMode ?: "N/A"

        // Status badge color
        when (order.status) {
            "pending" -> {
                holder.textStatus.text = "Pending"
                holder.textStatus.setBackgroundColor(Color.parseColor("#FF9800"))
                holder.buttonUpdateStatus.text = "Mark as Confirmed"
                holder.buttonUpdateStatus.visibility = View.VISIBLE
            }
            "confirmed" -> {
                holder.textStatus.text = "Confirmed"
                holder.textStatus.setBackgroundColor(Color.parseColor("#2196F3"))
                holder.buttonUpdateStatus.text = "Mark as Delivered"
                holder.buttonUpdateStatus.visibility = View.VISIBLE
            }
            "delivered" -> {
                holder.textStatus.text = "Delivered"
                holder.textStatus.setBackgroundColor(Color.parseColor("#4CAF50"))
                holder.buttonUpdateStatus.visibility = View.GONE
            }
            else -> {
                holder.textStatus.text = order.status
                holder.textStatus.setBackgroundColor(Color.parseColor("#9E9E9E"))
                holder.buttonUpdateStatus.visibility = View.GONE
            }
        }

        holder.buttonUpdateStatus.setOnClickListener {
            val nextStatus = if (order.status == "pending") "confirmed" else "delivered"
            onStatusChange(order, nextStatus)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<OrderItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
