package com.app.ornisapp.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.R
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

        date.setOnClickListener {
            showDatePicker()
        }


        save.setOnClickListener {

            val quant = item_quantity.text.toString().toInt()
            val unit = item_perunit.text.toString().toInt()

            val result = quant * unit
            item_totalamount.setText(result.toString())
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