package com.app.ornisapp.notes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.ornisapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class NotesActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: NotesAdapter
    private lateinit var notesList: MutableList<NotesData>
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        fab = findViewById(R.id.fab)
        recyclerView = findViewById(R.id.recycler)

        firestore = FirebaseFirestore.getInstance()
        notesList = mutableListOf()

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = NotesAdapter(this, notesList) { selectedNote ->
            showDeleteConfirmation(selectedNote)
        }
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            startActivity(Intent(this, Addnotes::class.java))
        }

        loadNotes()
    }

    private fun loadNotes() {
        firestore.collection("Notes").get()
            .addOnSuccessListener { result ->
                notesList.clear()
                for (document in result) {
                    val note = document.toObject<NotesData>()
                    note.id = document.id // Ensure the document ID is assigned
                    notesList.add(note)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load notes", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showDeleteConfirmation(note: NotesData) {
        AlertDialog.Builder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                deleteNoteFromFirestore(note)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteNoteFromFirestore(note: NotesData) {
        firestore.collection("Notes").document(note.id ?: return)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show()
                loadNotes()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
