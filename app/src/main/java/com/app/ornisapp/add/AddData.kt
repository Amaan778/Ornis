package com.app.ornisapp.add

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
import java.util.Date
import java.util.Locale
import kotlin.time.times

class AddData : AppCompatActivity() {
    private lateinit var date:TextView
    private lateinit var item_name:EditText
    private lateinit var item_quantity:EditText
    private lateinit var item_perunit:EditText
    private lateinit var item_totalamount:EditText
    private lateinit var item_payment:EditText
    private lateinit var item_remarks:EditText
    private lateinit var save:Button
    private lateinit var months:TextView

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data)

        date=findViewById(R.id.date)
        item_name=findViewById(R.id.item_name)
        item_quantity=findViewById(R.id.item_quantity)
        item_perunit=findViewById(R.id.item_perunit)
        item_totalamount=findViewById(R.id.item_totalamount)
        item_payment=findViewById(R.id.item_payment)
        item_remarks=findViewById(R.id.item_remarks)
        save=findViewById(R.id.save)
        months=findViewById(R.id.month)

        val database = FirebaseDatabase.getInstance().reference

        date.setOnClickListener {
            showDatePicker()
        }


        save.setOnClickListener {
            // Get input values safely
            val itemName = item_name.text.toString().trim()
            val itemQuantityStr = item_quantity.text.toString().trim()
            val itemPerUnitStr = item_perunit.text.toString().trim()
            val paymentMode = item_payment.text.toString().trim()
            val remarks = item_remarks.text.toString().trim()
            val month = months.text.toString().trim()
            val dateStr = date.text.toString().trim()  // renamed variable to avoid conflict with view 'date'

// Convert quantity and price per unit safely, defaulting to 0
            val quantity = itemQuantityStr.toIntOrNull() ?: 0
            val perUnit = itemPerUnitStr.toIntOrNull() ?: 0

// Calculate total amount
            val total = quantity * perUnit
            item_totalamount.setText(total.toString())

// Validation
            if (itemName.isEmpty()) {
                item_name.error = "Required"
            } else if (itemQuantityStr.isEmpty()) {
                item_quantity.error = "Required"
            } else if (itemPerUnitStr.isEmpty()) {
                item_perunit.error = "Required"
            } else if (total == 0) {
                item_totalamount.error = "Required"
            } else if (paymentMode.isEmpty()) {
                item_payment.error = "Required"
            } else if (remarks.isEmpty()) {
                item_remarks.error = "Required"
            } else {
                // Create sale object using your data class
                val sale = Sale(
                    itemName = itemName,
                    quantity = quantity,
                    pricePerUnit = perUnit,
                    totalAmount = total,
                    paymentMode = paymentMode,
                    remarks = remarks
                )

                // Save to Firebase Realtime Database
                val sanitizedDate = dateStr.replace("/", "-")
                val database = FirebaseDatabase.getInstance().reference
                database.child("sales").child(month).child(sanitizedDate)
                    .push()
                    .setValue(sale)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Sale saved successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save sale: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }



        }

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
}