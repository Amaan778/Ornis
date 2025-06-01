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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn=findViewById(R.id.btn)
        total=findViewById(R.id.total)

        btn.setOnClickListener {
            startActivity(Intent(this,AddData::class.java))
        }

        val database = FirebaseDatabase.getInstance().reference
        val month = "June"  // Or dynamically get it from your UI

// Reference to the sales for the given month
        val salesMonthRef = database.child("sales").child(month)

        salesMonthRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalAmountSum = 0

                // Loop through all dates under this month
                for (dateSnapshot in snapshot.children) {
                    // Loop through all sales under the date
                    for (saleSnapshot in dateSnapshot.children) {
                        // Get Sale object (make sure your Sale class matches structure)
                        val sale = saleSnapshot.getValue(Sale::class.java)
                        if (sale != null) {
                            totalAmountSum += sale.totalAmount
                        }
                    }
                }

                // Now totalAmountSum contains sum of all totalAmount in the month
                Toast.makeText(this@MainActivity, "Total sales in $month: $totalAmountSum", Toast.LENGTH_LONG).show()

                // Or update a TextView
                total.text = "Total Sales: $totalAmountSum"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load sales: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })


    }
}