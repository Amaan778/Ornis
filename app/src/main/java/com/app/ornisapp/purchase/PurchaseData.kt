package com.app.ornisapp.purchase

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.R
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PurchaseData : AppCompatActivity() {
    private lateinit var date: TextView
    private lateinit var months:TextView
    private lateinit var item_name:EditText
    private lateinit var item_quant:EditText
    private lateinit var item_priceperunit:EditText
    private lateinit var total_amounts:EditText
    private lateinit var payment:EditText
    private lateinit var save:Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_data)

        date=findViewById(R.id.date)
        months=findViewById(R.id.month)
        item_name=findViewById(R.id.item_name)
        item_quant=findViewById(R.id.item_quant)
        item_priceperunit=findViewById(R.id.item_priceperunit)
        total_amounts=findViewById(R.id.total_amount)
        payment=findViewById(R.id.payment)
        save=findViewById(R.id.save)

        date.setOnClickListener {
            showDatePicker()
        }

        save.setOnClickListener {
            submitPurchase()
        }

    }

    private fun submitPurchase() {
        val name = item_name.text.toString().trim()
        val quantity = item_quant.text.toString().toIntOrNull() ?: 0
        val perUnit = item_priceperunit.text.toString().toIntOrNull() ?: 0
        val paymentMode = payment.text.toString().trim()
        val date = date.text.toString().trim()
        val month = months.text.toString().trim()

        if (name.isEmpty() || quantity == 0 || perUnit == 0 || paymentMode.isEmpty() || date.isEmpty() || month.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val totalAmount = quantity * perUnit
        total_amounts.setText(totalAmount.toString())
        val formattedDate = date.replace("/", "-")

        val purchase = Purchase(
            itemName = name,
            quantity = quantity,
            pricePerUnit = perUnit,
            totalAmount = totalAmount,
            paymentMode = paymentMode,
            date = formattedDate,
            month = month
        )

        FirebaseDatabase.getInstance().reference.child("purchase").child(month).child(formattedDate)
            .push()
            .setValue(purchase)
            .addOnSuccessListener {
                Toast.makeText(this, "Purchase saved: ₹$totalAmount", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }

//        comment here
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                // Update the calendar with selected date
                calendar.set(Calendar.YEAR, selectedYear)
                calendar.set(Calendar.MONTH, selectedMonth)
                calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                // Format and display date
                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                date.text = format.format(calendar.time)

                // TextView 2: Show month only
                val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
                months.text = monthFormat.format(calendar.time)
            }, year, month, day)

        datePickerDialog.show()
    }
    private fun clearFields() {
        item_name.text.clear()
        item_quant.text.clear()
        item_priceperunit.text.clear()
        payment.setText("")
        total_amounts.setText("")
        date.text = ""
        months.text = ""
    }
}