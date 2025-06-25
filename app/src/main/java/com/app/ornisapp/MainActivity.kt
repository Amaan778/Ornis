package com.app.ornisapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.add.AddData
import com.app.ornisapp.add.Sale
import com.app.ornisapp.notes.NotesActivity
import com.app.ornisapp.purchase.Purchase
import com.app.ornisapp.purchase.PurchaseData
import com.app.ornisapp.recycler.PurchaseRecycler
import com.app.ornisapp.recycler.SalesRecycler
import com.app.ornisapp.recycler.WastageRecycler
import com.app.ornisapp.salesgrap.ProfitGraph
import com.app.ornisapp.salesgrap.SalesData
import com.app.ornisapp.wastage.WastageData
import com.app.ornisapp.wastage.Waste
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var btn: LinearLayout
    private lateinit var total: TextView
    private lateinit var wastage: TextView
    private lateinit var profitvalue: TextView
    private lateinit var waste: LinearLayout
    private lateinit var purchasebtn: LinearLayout
    private lateinit var purchase: TextView
    private lateinit var pieChart: PieChart
    private lateinit var saleslinear:LinearLayout
    private lateinit var wastedata:LinearLayout
    private lateinit var profitlinear:LinearLayout
    private lateinit var purchaselinear:LinearLayout
    private lateinit var notes:LinearLayout
    private lateinit var linear:LinearLayout
    private lateinit var tool:Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn = findViewById(R.id.btn)
        total = findViewById(R.id.total)
        wastage = findViewById(R.id.wstage)
        profitvalue = findViewById(R.id.profit)
        waste = findViewById(R.id.wastage)
        purchasebtn = findViewById(R.id.purchasebtn)
        purchase = findViewById(R.id.purchase)
        pieChart = findViewById(R.id.pieChart)
        saleslinear=findViewById(R.id.saleslinear)
        wastedata=findViewById(R.id.wastedata)
        profitlinear=findViewById(R.id.profitlinear)
        purchaselinear=findViewById(R.id.purchaselinear)
        notes=findViewById(R.id.notes)
        linear=findViewById(R.id.linear)
        tool=findViewById(R.id.toolbar)

        btn.setOnClickListener {
            startActivity(Intent(this, AddData::class.java))
        }

        waste.setOnClickListener {
            startActivity(Intent(this, WastageData::class.java))
        }

        purchasebtn.setOnClickListener {
            startActivity(Intent(this, PurchaseData::class.java))
        }

        saleslinear.setOnClickListener {
            startActivity(Intent(this,SalesRecycler::class.java))
        }

        wastedata.setOnClickListener {
            startActivity(Intent(this,WastageRecycler::class.java))
        }

        profitlinear.setOnClickListener {
            startActivity(Intent(this,ProfitGraph::class.java))
        }

        purchaselinear.setOnClickListener {
            startActivity(Intent(this,PurchaseRecycler::class.java))
        }

        notes.setOnClickListener {
            startActivity(Intent(this,NotesActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        fetchAndCalculateProfit()
    }

    private fun fetchAndCalculateProfit() {
        val database = FirebaseDatabase.getInstance().reference
        var totalSales = 0
        var totalPurchase = 0
        var totalWastage = 0

        database.child("sales").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (monthSnap in snapshot.children) {
                    for (dateSnap in monthSnap.children) {
                        for (saleSnap in dateSnap.children) {
                            val sale = saleSnap.getValue(Sale::class.java)
                            if (sale != null) {
                                totalSales += sale.totalAmount
                            }
                        }
                    }
                }
                total.text = "₹$totalSales"

                database.child("purchase").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (monthSnap in snapshot.children) {
                            for (dateSnap in monthSnap.children) {
                                for (purchaseSnap in dateSnap.children) {
                                    val purchaseItem = purchaseSnap.getValue(Purchase::class.java)
                                    if (purchaseItem != null) {
                                        totalPurchase += purchaseItem.totalAmount
                                    }
                                }
                            }
                        }
                        purchase.text = "₹$totalPurchase"

                        database.child("wastage").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (monthSnap in snapshot.children) {
                                    for (dateSnap in monthSnap.children) {
                                        for (wasteSnap in dateSnap.children) {
                                            val wasteItem = wasteSnap.getValue(Waste::class.java)
                                            if (wasteItem != null) {
                                                totalWastage += wasteItem.totalLoss
                                            }
                                        }
                                    }
                                }
                                wastage.text = "₹$totalWastage"
                                val profit = totalSales - totalPurchase - totalWastage
                                profitvalue.text = "₹$profit"
                                updatePieChart(totalSales, totalPurchase, totalWastage, profit)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(this@MainActivity, "Wastage error: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@MainActivity, "Purchase error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Sales error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updatePieChart(sales: Int, purchase: Int, wastage: Int, profit: Int) {
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(sales.toFloat(), "Sales"))
        entries.add(PieEntry(purchase.toFloat(), "Purchase"))
        entries.add(PieEntry(wastage.toFloat(), "Wastage"))
        entries.add(PieEntry(profit.toFloat(), "Profit"))

        val dataSet = PieDataSet(entries, "Business Overview")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Profit Analysis"
        pieChart.setCenterTextSize(16f)
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
