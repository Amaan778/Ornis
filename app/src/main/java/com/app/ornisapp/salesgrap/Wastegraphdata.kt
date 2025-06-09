package com.app.ornisapp.salesgrap

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.ornisapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class Wastegraphdata : AppCompatActivity() {

    private lateinit var barChart: BarChart
    private lateinit var timeFilterSpinner: Spinner

    enum class TimeRange {
        DAILY, WEEKLY, MONTHLY, QUARTERLY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wastegraphdata)

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
                    "Daily" -> fetchWastageData(TimeRange.DAILY)
                    "Weekly" -> fetchWastageData(TimeRange.WEEKLY)
                    "Monthly" -> fetchWastageData(TimeRange.MONTHLY)
                    "Quarterly" -> fetchWastageData(TimeRange.QUARTERLY)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchWastageData(range: TimeRange) {
        val ref = FirebaseDatabase.getInstance().getReference("wastage")
        val groupedLosses = mutableMapOf<String, Int>()

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groupedLosses.clear()

                for (monthSnap in snapshot.children) {
                    for (dateSnap in monthSnap.children) {
                        val dateKey = dateSnap.key ?: continue
                        val parsedDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateKey)
                        val calendar = Calendar.getInstance()

                        parsedDate?.let {
                            calendar.time = it

                            val label = when (range) {
                                TimeRange.DAILY -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(it)
                                TimeRange.WEEKLY -> "Wk ${calendar.get(Calendar.WEEK_OF_YEAR)}"
                                TimeRange.MONTHLY -> SimpleDateFormat("MMM", Locale.getDefault()).format(it)
                                TimeRange.QUARTERLY -> "Q${(calendar.get(Calendar.MONTH) / 3) + 1}"
                            }

                            for (wasteSnap in dateSnap.children) {
                                val totalLoss = wasteSnap.child("totalLoss").getValue(Int::class.java) ?: 0
                                groupedLosses[label] = (groupedLosses[label] ?: 0) + totalLoss
                            }
                        }
                    }
                }

                showBarChart(groupedLosses)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Wastegraphdata, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showBarChart(dataMap: Map<String, Int>) {
        val labels = dataMap.keys.toList()
        val entries = dataMap.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Wastage (Total Loss)")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.valueTextColor = android.graphics.Color.BLACK
        barDataSet.valueTextSize = 14f

        val barData = BarData(barDataSet)
        barChart.data = barData

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
