package com.example.pertemuan9

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var myAdapter: AdapterList
    private lateinit var itemList: MutableList<ItemList>
    private lateinit var db: FirebaseFirestore
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Inisialisasi Firebase
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

        // Inisialisasi RecyclerView & floatingButton
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatAddNews)
        progressDialog = ProgressDialog(this@MainActivity).apply {
            setTitle("Loading...")
        }

        //Setup RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        itemList = ArrayList()
        myAdapter = AdapterList(itemList)
        recyclerView.adapter = myAdapter

        floatingActionButton.setOnClickListener {
            val toAddPage = Intent(this@MainActivity, NewsAdd::class.java)
            startActivity(toAddPage)
        }

        myAdapter.setOnItemClickListener(object : AdapterList.OnItemClickListener {
            override fun onItemClick(item: ItemList) {
                val intent = Intent(this@MainActivity, NewsDetail::class.java).apply {
                    putExtra("id", item.id)
                    putExtra("title", item.judul)
                    putExtra("desc", item.subJudul)
                    putExtra("imageUrl", item.imageUrl)
                }
                startActivity(intent)
            }
        })

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getData() {
        progressDialog.show()
        db.collection("news")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    itemList.clear()
                    for (document in task.result) {
                        val item = ItemList(
                            document.id,
                            document.getString("title") ?: "",
                            document.getString("desc") ?: "",
                            document.getString("imageUrl") ?: ""
                        )
                        itemList.add(item)
                        Log.d("Data", "${document.id} => ${document.data}")
                    }
                    myAdapter.notifyDataSetChanged()
                } else {
                    Log.w("Data", "Error getting documents.", task.exception)
                }
                progressDialog.dismiss()
            }
    }

    override fun onStart() {
        super.onStart()
        //Fetch data from Firestore
        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_logout) {
            mAuth.signOut()
            Toast.makeText(this@MainActivity, "Logged Out Successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this@MainActivity, DefaultActivity::class.java)
            startActivity(intent)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}