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

class NotesAdapter(private val context: Context,private val userlist:List<NotesData>) : RecyclerView.Adapter<NotesAdapter.Viewholder>() {
    class Viewholder(itemview:View) : RecyclerView.ViewHolder(itemview) {
        val title:TextView = itemview.findViewById(R.id.title)
        val idss:TextView=itemview.findViewById(R.id.docid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Viewholder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.notes_adapter,parent,false)
        return Viewholder(view)
    }

    override fun getItemCount(): Int {
        return userlist.size
    }

    override fun onBindViewHolder(holder: Viewholder, position: Int) {
        val surrentlis=userlist[position]
        holder.title.text=surrentlis.title
        holder.idss.text=surrentlis.id

        holder.itemView.setOnClickListener {
            val intent=Intent(context,NotesDetail::class.java)
            intent.putExtra("id",userlist[position].id)
            context.startActivity(intent)
            Toast.makeText(context,"Clciking",Toast.LENGTH_LONG).show()
        }

    }
}