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
import java.util.Date
import java.util.Locale

class ProfitGraph : AppCompatActivity() {
    private lateinit var barChart: BarChart
    private lateinit var timeFilterSpinner: Spinner

    enum class TimeRange {
        DAILY, WEEKLY, MONTHLY, QUARTERLY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profit_graph)

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
                val range = when (parent.getItemAtPosition(position).toString()) {
                    "Daily" -> TimeRange.DAILY
                    "Weekly" -> TimeRange.WEEKLY
                    "Monthly" -> TimeRange.MONTHLY
                    else -> TimeRange.QUARTERLY
                }
                fetchProfitData(range)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchProfitData(range: TimeRange) {
        val salesRef = FirebaseDatabase.getInstance().getReference("sales")
        val purchaseRef = FirebaseDatabase.getInstance().getReference("purchase")
        val wastageRef = FirebaseDatabase.getInstance().getReference("wastage")

        val profitMap = mutableMapOf<String, Int>() // Label -> profit

        salesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(salesSnap: DataSnapshot) {
                val salesMap = mutableMapOf<String, Int>()

                for (monthSnap in salesSnap.children) {
                    for (dateSnap in monthSnap.children) {
                        val dateKey = dateSnap.key ?: continue
                        val date = parseDate(dateKey) ?: continue
                        val label = getLabelFromDate(date, range)

                        for (saleSnap in dateSnap.children) {
                            val amount = saleSnap.child("totalAmount").getValue(Int::class.java) ?: 0
                            salesMap[label] = (salesMap[label] ?: 0) + amount
                        }
                    }
                }

                purchaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(purchaseSnap: DataSnapshot) {
                        val purchaseMap = mutableMapOf<String, Int>()

                        for (monthSnap in purchaseSnap.children) {
                            for (dateSnap in monthSnap.children) {
                                val dateKey = dateSnap.key ?: continue
                                val date = parseDate(dateKey) ?: continue
                                val label = getLabelFromDate(date, range)

                                for (purchaseChild in dateSnap.children) {
                                    val amount = purchaseChild.child("totalAmount").getValue(Int::class.java) ?: 0
                                    purchaseMap[label] = (purchaseMap[label] ?: 0) + amount
                                }
                            }
                        }

                        wastageRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(wastageSnap: DataSnapshot) {
                                val wastageMap = mutableMapOf<String, Int>()

                                for (monthSnap in wastageSnap.children) {
                                    for (dateSnap in monthSnap.children) {
                                        val dateKey = dateSnap.key ?: continue
                                        val date = parseDate(dateKey) ?: continue
                                        val label = getLabelFromDate(date, range)

                                        for (wasteChild in dateSnap.children) {
                                            val loss = wasteChild.child("totalLoss").getValue(Int::class.java) ?: 0
                                            wastageMap[label] = (wastageMap[label] ?: 0) + loss
                                        }
                                    }
                                }

                                // Final calculation of profit = sales - purchase - wastage
                                val allLabels = mutableSetOf<String>()
                                allLabels.addAll(salesMap.keys)
                                allLabels.addAll(purchaseMap.keys)
                                allLabels.addAll(wastageMap.keys)

                                for (label in allLabels) {
                                    val sales = salesMap[label] ?: 0
                                    val purchase = purchaseMap[label] ?: 0
                                    val wastage = wastageMap[label] ?: 0
                                    val profit = sales - purchase - wastage
                                    profitMap[label] = profit
                                }

                                showBarChart(profitMap)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                toast("Wastage error: ${error.message}")
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        toast("Purchase error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                toast("Sales error: ${error.message}")
            }
        })
    }

    private fun showBarChart(dataMap: Map<String, Int>) {
        val labels = dataMap.keys.toList()
        val entries = dataMap.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val barDataSet = BarDataSet(entries, "Profit")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        barDataSet.valueTextColor = Color.BLACK
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

    private fun parseDate(dateStr: String): Date? {
        return try {
            SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    private fun getLabelFromDate(date: Date, range: TimeRange): String {
        val cal = Calendar.getInstance()
        cal.time = date
        return when (range) {
            TimeRange.DAILY -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(date)
            TimeRange.WEEKLY -> "Wk ${cal.get(Calendar.WEEK_OF_YEAR)}"
            TimeRange.MONTHLY -> SimpleDateFormat("MMM", Locale.getDefault()).format(date)
            TimeRange.QUARTERLY -> "Q${(cal.get(Calendar.MONTH) / 3) + 1}"
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}