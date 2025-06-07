package com.app.ornisapp.wastage

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WastageData : AppCompatActivity() {
    private lateinit var date:TextView
    private lateinit var months:TextView

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wastage_data)

        date=findViewById(R.id.date)
        months=findViewById(R.id.month)

        date.setOnClickListener {
            showDatePicker()
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