package com.app.ornisapp.recycler

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ornisapp.R
import com.app.ornisapp.purchase.Purchase
import com.app.ornisapp.salesgrap.PurchaseGraphData
import com.app.ornisapp.salesgrap.Wastegraphdata
import com.app.ornisapp.wastage.Waste
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PurchaseRecycler : AppCompatActivity() {
    private lateinit var monthSpinner: Spinner
    private lateinit var salesRecyclerView: RecyclerView
    private lateinit var noDataText: TextView
    private lateinit var adapter: PurchaseAdapter
    private val salesList = mutableListOf<Pair<String, Purchase>>()
    private lateinit var graph: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase_recycler)

        monthSpinner = findViewById(R.id.monthSpinner)
        salesRecyclerView = findViewById(R.id.salesRecyclerView)
        noDataText = findViewById(R.id.noDataText)
        graph=findViewById(R.id.save)

        graph.setOnClickListener {
            startActivity(Intent(this, PurchaseGraphData::class.java))
        }

        adapter = PurchaseAdapter(salesList)
        salesRecyclerView.layoutManager = LinearLayoutManager(this)
        salesRecyclerView.adapter = adapter

        val months = listOf("Select Month", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = spinnerAdapter

        monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMonth = parent.getItemAtPosition(position).toString()
                if (selectedMonth != "Select Month") {
                    loadSalesForMonth(selectedMonth)
                } else {
                    salesList.clear()
                    adapter.notifyDataSetChanged()
                    noDataText.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun loadSalesForMonth(month: String) {
        val ref = FirebaseDatabase.getInstance().getReference("purchase").child(month)
        salesList.clear()
        adapter.notifyDataSetChanged()
        noDataText.visibility = View.GONE

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var hasData = false
                for (dateSnap in snapshot.children) {
                    val date = dateSnap.key ?: continue
                    for (saleSnap in dateSnap.children) {
                        val sale = saleSnap.getValue(Purchase::class.java)
                        sale?.let {
                            salesList.add(Pair(date, it))
                            hasData = true
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                noDataText.visibility = if (hasData) View.GONE else View.VISIBLE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PurchaseRecycler, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}