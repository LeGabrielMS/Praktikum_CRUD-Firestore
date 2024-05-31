package com.example.pertemuan9

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.setHasFixedSize(true)

        val itemList = listOf(
            ItemList(
                "Indonesia Merdeka",
                "Indonesia Merdeka pada tanggal 17 Agustus 1945",
                "https://maukuliah.ap-south-1.linodeobjects.com/gallery/043059/Gedung%201%20STTB-thumbnail.jpg"
            ),
            ItemList(
                "Universitas Teknologi Bandung",
                "Universitas Teknologi Bandung merupakan Universitas yang berada di Bandung, Jawa Barat",
                "https://maukuliah.ap-south-1.linodeobjects.com/gallery/043059/Gedung%201%20STTB-thumbnail.jpg"
            ),
            ItemList(
                "Natal 2023",
                "Natal 2023 merupakan hari besar nasional Indonesia",
                "https://maukuliah.ap-south-1.linodeobjects.com/gallery/043059/Gedung%201%20STTB-thumbnail.jpg"
            )
        )

        val adapter = AdapterList(itemList)
        recyclerView.adapter = adapter
    }
}