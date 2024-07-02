package com.example.pertemuan9

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class NewsDetail : AppCompatActivity() {
    private lateinit var newsTitle: TextView
    private lateinit var newsSubtitle: TextView
    private lateinit var newsImage: ImageView

    private lateinit var edit: Button
    private lateinit var hapus: Button
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        //inisialisasi UI Components
        newsTitle = findViewById(R.id.newsTitle)
        newsSubtitle = findViewById(R.id.newsSubtitle)
        newsImage = findViewById(R.id.newsImage)
        edit = findViewById(R.id.editButton)
        hapus = findViewById(R.id.deleteButton)

        //inisialisasi Firebase
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        //get data from intent
        val intent = this.intent
        val id = intent.getStringExtra("id")
        val title = intent.getStringExtra("title")
        val subtitle = intent.getStringExtra("desc")
        val imageUrl = intent.getStringExtra("imageUrl")

        //set data to UI Components
        newsTitle.text = title
        newsSubtitle.text = subtitle
        Glide.with(this).load(imageUrl).into(newsImage)

        //edit news
        edit.setOnClickListener {
            val editIntent = Intent(this@NewsDetail, NewsAdd::class.java).apply {
                putExtra("id", id)
                putExtra("title", title)
                putExtra("desc", subtitle)
                putExtra("imageUrl", imageUrl)
            }
            startActivity(editIntent)
            finish()
        }

        //delete news
        hapus.setOnClickListener {
            id?.let { documentId ->
                db.collection("news")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@NewsDetail,
                            "News Deleted Succesfully",
                            Toast.LENGTH_LONG
                        ).show()

                        // Redirect to MainActivity
                        val mainIntent = Intent(this, MainActivity::class.java).apply {
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        startActivity(mainIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this@NewsDetail,
                            "Failed to delete news: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        Log.w("News Detail, Error Deleting Document", e)
                    }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_logout) {
            mAuth.signOut()
            Toast.makeText(this@NewsDetail, "Logged Out Successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@NewsDetail, DefaultActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}