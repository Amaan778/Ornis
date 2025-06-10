package com.app.ornisapp.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.ornisapp.R

class NotesAdapter(
    private val context: Context,
    private val notesList: List<NotesData>,
    private val onLongPress: (NotesData) -> Unit
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
        val idText: TextView = itemView.findViewById(R.id.docid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.notes_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = notesList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notesList[position]
        holder.title.text = note.title
        holder.idText.text = note.id

        // Short click opens detail
        holder.itemView.setOnClickListener {
            val intent = Intent(context, NotesDetail::class.java)
            intent.putExtra("id", note.id)
            context.startActivity(intent)
        }

        // Long click shows delete dialog
        holder.itemView.setOnLongClickListener {
            onLongPress(note)
            true
        }
    }
}
