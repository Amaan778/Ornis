package com.app.ornisapp.wastage

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

class WastageData : AppCompatActivity() {
    private lateinit var date:TextView
    private lateinit var months:TextView
    private lateinit var item_name:EditText
    private lateinit var item_quant:EditText
    private lateinit var item_priceperunit:EditText
    private lateinit var total_loss:EditText
    private lateinit var reason:EditText
    private lateinit var save:Button

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wastage_data)

        date=findViewById(R.id.date)
        months=findViewById(R.id.month)
        item_name=findViewById(R.id.item_name)
        item_quant=findViewById(R.id.item_quant)
        item_priceperunit=findViewById(R.id.item_priceperunit)
        total_loss=findViewById(R.id.total_loss)
        reason=findViewById(R.id.reason)
        save=findViewById(R.id.save)

        date.setOnClickListener {
            showDatePicker()
        }

        save.setOnClickListener {
            submitWastage()
        }

    }

    private fun submitWastage() {
        val name = item_name.text.toString().trim()
        val quantity = item_quant.text.toString().toIntOrNull() ?: 0
        val perUnit = item_priceperunit.text.toString().toIntOrNull() ?: 0
        val reasons = reason.text.toString().trim()
        val date = date.text.toString().trim()
        val month = months.text.toString().trim()

        if (name.isEmpty() || quantity == 0 || reasons.isEmpty() || date.isEmpty() || month.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val totalLoss = quantity * perUnit
        total_loss.setText(totalLoss.toString())
        val formattedDate = date.replace("/", "-")

        val wastage = Waste(
            itemName = name,
            quantity = quantity,
            pricePerUnit = perUnit,
            totalLoss = totalLoss,
            reason = reasons,
            date = formattedDate,
            month = month
        )

        FirebaseDatabase.getInstance().reference
            .child("wastage")
            .child(month)
            .child(formattedDate)
            .push()
            .setValue(wastage)
            .addOnSuccessListener {
                Toast.makeText(this, "Wastage saved: ₹$totalLoss loss", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
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
    private fun clearFields() {
        item_name.text.clear()
        item_quant.text.clear()
        item_priceperunit.text.clear()
        total_loss.setText("")
        reason.text.clear()
        date.text = ""
        months.text = ""
    }
}