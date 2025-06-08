package com.app.ornisapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.add.AddData
import com.app.ornisapp.add.Sale
import com.app.ornisapp.purchase.Purchase
import com.app.ornisapp.purchase.PurchaseData
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
    private lateinit var btn:Button
    private lateinit var total:TextView
    private lateinit var wastage:TextView
    private lateinit var profitvalue:TextView
    private lateinit var waste:Button
    private lateinit var purchasebtn:Button
    private lateinit var purchase:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn=findViewById(R.id.btn)
        total=findViewById(R.id.total)
        wastage=findViewById(R.id.wstage)
        profitvalue=findViewById(R.id.profit)
        waste=findViewById(R.id.wastage)
        purchasebtn=findViewById(R.id.purchasebtn)
        purchase=findViewById(R.id.purchase)

        btn.setOnClickListener {
            startActivity(Intent(this,AddData::class.java))
        }

        waste.setOnClickListener {
            startActivity(Intent(this,WastageData::class.java))
        }

        purchasebtn.setOnClickListener {
            startActivity(Intent(this,PurchaseData::class.java))
        }

        fetchTotalSales()
        fetchwastage()
        fetchpurchase()
        fetchAndCalculateProfit()

        val pieChart = findViewById<PieChart>(R.id.pieChart)

        val totalText = total.text.toString().replace("₹", "").trim()
        val purchaseText = purchase.text.toString().replace("₹", "").trim()
        val wastageText = wastage.text.toString().replace("₹", "").trim()
        val profitText = profitvalue.text.toString().replace("₹", "").trim()

        val totalFloat = totalText.toFloatOrNull() ?: 0f
        val purchaseFloat = purchaseText.toFloatOrNull() ?: 0f
        val wastageFloat = wastageText.toFloatOrNull() ?: 0f
        val profitFloat = profitText.toFloatOrNull() ?: 0f


// Pie Entries
        val entries = ArrayList<PieEntry>()
        entries.add(PieEntry(totalFloat, "Sales"))
        entries.add(PieEntry(purchaseFloat, "Purchase"))
        entries.add(PieEntry(wastageFloat, "Wastage"))
        entries.add(PieEntry(profitFloat, "Profit"))


// Pie DataSet
        val dataSet = PieDataSet(entries, "Business Overview")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTextSize = 14f

// Pie Data
        val pieData = PieData(dataSet)

        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = "Profit Analysis"
        pieChart.setCenterTextSize(16f)
        pieChart.animateY(1000)
        pieChart.invalidate()


    }

    private fun fetchpurchase() {
        val database = FirebaseDatabase.getInstance().reference
        val salesRef = database.child("purchase")

        salesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmountSum = 0

                for (monthSnapshot in snapshot.children) {
                    for (dateSnapshot in monthSnapshot.children) {
                        for (saleSnapshot in dateSnapshot.children) {
                            val purchase = saleSnapshot.getValue(Purchase::class.java)
                            if (purchase != null) {
                                totalAmountSum += purchase.totalAmount
                            }
                        }
                    }
                }

                purchase.text = "$totalAmountSum"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchwastage() {
        val database = FirebaseDatabase.getInstance().reference
        val salesRef = database.child("wastage")

        salesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmountSum = 0

                for (monthSnapshot in snapshot.children) {
                    for (dateSnapshot in monthSnapshot.children) {
                        for (saleSnapshot in dateSnapshot.children) {
                            val waste = saleSnapshot.getValue(Waste::class.java)
                            if (waste != null) {
                                totalAmountSum += waste.totalLoss
                            }
                        }
                    }
                }

                wastage.text = "$totalAmountSum"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTotalSales() {
        val database = FirebaseDatabase.getInstance().reference
        val salesRef = database.child("sales")

        salesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmountSum = 0

                for (monthSnapshot in snapshot.children) {
                    for (dateSnapshot in monthSnapshot.children) {
                        for (saleSnapshot in dateSnapshot.children) {
                            val sale = saleSnapshot.getValue(Sale::class.java)
                            if (sale != null) {
                                totalAmountSum += sale.totalAmount
                            }
                        }
                    }
                }

                total.text = "$totalAmountSum"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchTotalSalesForMonth(month: String) {
        val database = FirebaseDatabase.getInstance().reference
        val salesMonthRef = database.child("sales").child(month)

        salesMonthRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmountSum = 0

                for (dateSnapshot in snapshot.children) {
                    for (saleSnapshot in dateSnapshot.children) {
                        val sale = saleSnapshot.getValue(Sale::class.java)
                        if (sale != null) {
                            totalAmountSum += sale.totalAmount
                        }
                    }
                }

                total.text = "Total Sales: ₹$totalAmountSum"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchTotalSales()
        fetchwastage()
        fetchpurchase()
        fetchAndCalculateProfit()
//        fetchTotalSalesForMonth("June") // Or pass dynamic month
    }

    override fun onStart() {
        super.onStart()
        fetchTotalSales()
        fetchwastage()
        fetchpurchase()
        fetchAndCalculateProfit()
//        fetchTotalSalesForMonth("June") // Or pass dynamic month if needed
    }

    private fun fetchAndCalculateProfit() {
        val database = FirebaseDatabase.getInstance().reference

        var totalSales = 0
        var totalPurchase = 0
        var totalWastage = 0

        // Sales
        database.child("sales").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (monthSnap in snapshot.children) {
                    for (dateSnap in monthSnap.children) {
                        for (saleSnap in dateSnap.children) {
                            val totalAmount = saleSnap.child("totalAmount").getValue(Int::class.java) ?: 0
                            totalSales += totalAmount
                        }
                    }
                }
                total.text = "₹$totalSales"

                // Purchase
                database.child("purchase").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (monthSnap in snapshot.children) {
                            for (dateSnap in monthSnap.children) {
                                for (purchaseSnap in dateSnap.children) {
                                    val totalAmount = purchaseSnap.child("totalAmount").getValue(Int::class.java) ?: 0
                                    totalPurchase += totalAmount
                                }
                            }
                        }
                        purchase.text = "₹$totalPurchase"

                        database.child("wastage").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (monthSnap in snapshot.children) {
                                    for (dateSnap in monthSnap.children) {
                                        for (wasteSnap in dateSnap.children) {
                                            val waste = wasteSnap.getValue(Waste::class.java)
                                            if (waste != null) {
                                                totalWastage += waste.totalLoss
                                            }
                                        }
                                    }
                                }
                                wastage.text = "₹$totalWastage"

                                // Now calculate profit
                                val profit = totalSales - totalPurchase - totalWastage
                                profitvalue.text = "₹$profit"
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

}