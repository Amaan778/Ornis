package com.app.ornisapp.notes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ornisapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class NotesActivity : AppCompatActivity() {
    private lateinit var fab:FloatingActionButton
    private lateinit var firestore:FirebaseFirestore
    private lateinit var adapters:NotesAdapter
    private lateinit var userlist : MutableList<NotesData>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        fab=findViewById(R.id.fab)
        recyclerView=findViewById(R.id.recycler)

        firestore=FirebaseFirestore.getInstance()

        fab.setOnClickListener {
            startActivity(Intent(this,Addnotes::class.java))
        }

        recyclerView.layoutManager=LinearLayoutManager(this)
        userlist = mutableListOf()

        firestore.collection("Notes").get()
            .addOnSuccessListener {
                for (document in it){
                    val userss=document.toObject(NotesData::class.java)
                    userlist.add(userss)
                }
                adapters =NotesAdapter(this,userlist)
                recyclerView.adapter=adapters
            }
            .addOnFailureListener {
                Toast.makeText(this,"Failed to load data",Toast.LENGTH_SHORT).show()
            }

    }
}