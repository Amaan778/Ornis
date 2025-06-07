package com.app.ornisapp

import android.content.Intent
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var btn:Button
    private lateinit var total:TextView
    private lateinit var profit:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn=findViewById(R.id.btn)
        total=findViewById(R.id.total)
        profit=findViewById(R.id.profit)

        btn.setOnClickListener {
            startActivity(Intent(this,AddData::class.java))
        }

        fetchTotalSales()

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
//        fetchTotalSalesForMonth("June") // Or pass dynamic month
    }

    override fun onStart() {
        super.onStart()
        fetchTotalSales()
//        fetchTotalSalesForMonth("June") // Or pass dynamic month if needed
    }

}