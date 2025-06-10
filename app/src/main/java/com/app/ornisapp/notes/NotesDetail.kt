package com.app.ornisapp.notes

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.app.ornisapp.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class NotesDetail : AppCompatActivity() {
    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var save: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_detail)

        val get=intent.getStringExtra("id")
        Log.d("value", "onCreate: "+get)

        title=findViewById(R.id.title)
        description=findViewById(R.id.datadescription)
        save=findViewById(R.id.save)

        db=FirebaseFirestore.getInstance()

        db.collection("Notes").document(get.toString()).get()
            .addOnSuccessListener {
                if (it != null) {
                    val nottitle=it.data?.get("title").toString()
                    val notedescrip=it.data?.get("description").toString()

                    title.setText(nottitle)
                    description.setText(notedescrip)

                }
            }

        save.setOnClickListener {

            val titles=title.text.toString()
            val desc=description.text.toString()
            val id= get.toString()

            db=FirebaseFirestore.getInstance()

            val data=NotesData(titles,desc,id)

            db.collection("Notes").document(id).set(data)
                .addOnSuccessListener {
                    Toast.makeText(this,"Notes saved", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Notes not saved", Toast.LENGTH_SHORT).show()
                }

        }

    }
}