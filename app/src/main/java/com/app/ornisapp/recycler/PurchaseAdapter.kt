package com.app.ornisapp.recycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.ornisapp.R
import com.app.ornisapp.purchase.Purchase
import com.app.ornisapp.wastage.Waste

class PurchaseAdapter(private val list: List<Pair<String, Purchase>>) :
    RecyclerView.Adapter<PurchaseAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.recy_name)
        val itemQuantity: TextView = itemView.findViewById(R.id.rec_quant)
        val itemPricePerUnit: TextView = itemView.findViewById(R.id.rec_priceperunit)
        val totalAmount: TextView = itemView.findViewById(R.id.rec_totalamount)
        val paymentMode: TextView = itemView.findViewById(R.id.payment_mode)
        val saleDate: TextView = itemView.findViewById(R.id.date) // Make sure this exists in XML
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.wastagerecycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val current = list[position].second

        holder.itemName.text = current.itemName
        holder.itemQuantity.text = current.quantity.toString()
        holder.itemPricePerUnit.text = current.pricePerUnit.toString()
        holder.totalAmount.text = current.totalAmount.toString()
        holder.paymentMode.text = current.paymentMode.toString()
        holder.saleDate.text=current.date
    }
}