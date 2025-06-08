package com.app.ornisapp.salesgrap

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SalesData : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var timeFilterSpinner: Spinner

    enum class TimeRange {
        WEEKLY, MONTHLY, QUARTERLY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sales_data)

        barChart = findViewById(R.id.barChart)
        timeFilterSpinner = findViewById(R.id.timeFilterSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.time_filters,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timeFilterSpinner.adapter = adapter
        }

        timeFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (parent.getItemAtPosition(position).toString()) {
                    "Weekly" -> fetchSalesData(TimeRange.WEEKLY)
                    "Monthly" -> fetchSalesData(TimeRange.MONTHLY)
                    "Quarterly" -> fetchSalesData(TimeRange.QUARTERLY)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchSalesData(range: TimeRange) {
        val salesRef = FirebaseDatabase.getInstance().getReference("sales")
        val groupedSales = mutableMapOf<String, Int>()

        salesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupedSales.clear()

                for (monthSnap in snapshot.children) {
                    for (dateSnap in monthSnap.children) {
                        val dateKey = dateSnap.key ?: continue
                        val parsedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateKey)
                        val calendar = Calendar.getInstance()

                        parsedDate?.let {
                            calendar.time = it

                            val label = when (range) {
                                TimeRange.WEEKLY -> "Wk ${calendar.get(Calendar.WEEK_OF_YEAR)}"
                                TimeRange.MONTHLY -> SimpleDateFormat("MMM", Locale.getDefault()).format(it)
                                TimeRange.QUARTERLY -> "Q${(calendar.get(Calendar.MONTH) / 3) + 1}"
                            }

                            for (saleSnap in dateSnap.children) {
                                val totalAmount = saleSnap.child("totalAmount").getValue(Int::class.java) ?: 0
                                groupedSales[label] = (groupedSales[label] ?: 0) + totalAmount
                            }
                        }
                    }
                }

                showBarChart(groupedSales)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SalesData, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBarChart(salesData: Map<String, Int>) {
        val labels = salesData.keys.toList()
        val entries = salesData.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Sales")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor = android.graphics.Color.BLACK
        barDataSet.valueTextSize = 14f

        val data = BarData(barDataSet)
        barChart.data = data

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.labelRotationAngle = -45f

        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.setFitBars(true)
        barChart.animateY(1000)
        barChart.invalidate()
    }
}