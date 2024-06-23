package com.example.pertemuan9

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class NewsAdd : AppCompatActivity() {
    var id: String? = ""
    var judul: String? = null
    var deskripsi: String? = null
    var image: String? = null

    private val PICK_IMAGE_REQUEST = 1

    private lateinit var title: EditText
    private lateinit var desc: EditText
    private lateinit var imageView: ImageView
    private lateinit var saveNews: Button
    private lateinit var chooseImage: Button
    var imageUri: Uri? = null

    private lateinit var dbNews: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_add)

        //inisialisasi Firebase
        dbNews = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        //inisialisasi UI Components
        title = findViewById(R.id.title)
        desc = findViewById(R.id.desc)
        imageView = findViewById(R.id.imageView)
        saveNews = findViewById(R.id.btnAdd)
        chooseImage = findViewById(R.id.btnChooseImage)

        progressDialog = ProgressDialog(this@NewsAdd).apply {
            setTitle("Loading...")
        }

        chooseImage.setOnClickListener {
            openFileChooser()
        }

        saveNews.setOnClickListener {
            val newsTitle = title.text.toString().trim()
            val newsDesc = desc.text.toString().trim()

            if (newsTitle.isEmpty() || newsDesc.isEmpty()) {
                Toast.makeText(
                    this@NewsAdd,
                    "Please enter news title and description",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            progressDialog.show()

            if (imageUri != null) {
                uploadImageToStorage(newsTitle, newsDesc)
            } else {
                saveData(newsTitle, newsDesc, image ?: "")
                finish()
            }
        }

        val updateOption = intent
        if (updateOption != null) {
            id = updateOption.getStringExtra("id")
            judul = updateOption.getStringExtra("title")
            deskripsi = updateOption.getStringExtra("desc")
            image = updateOption.getStringExtra("imageUrl")

            title.setText(judul)
            desc.setText(deskripsi)
            Glide.with(this@NewsAdd).load(image).into(imageView)
        }

    }

    private fun openFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            imageView.setImageURI(imageUri)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun saveData(newsTitle: String, newsDesc: String, imageUrl: String) {
        val news = HashMap<String, Any>()
        news["title"] = newsTitle
        news["desc"] = newsDesc
        news["imageUrl"] = imageUrl

        if (id != null) {
            // Update News
            dbNews.collection("news")
                .document(id ?: "")
                .update(news)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News updated successfully", Toast.LENGTH_LONG)
                        .show()
                    finish()
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@NewsAdd,
                        "Failed to update news: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.w("NewsAdd", "Error updating document", e)
                }
        } else {
            // Add News
            dbNews.collection("news")
                .add(news)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    Toast.makeText(this@NewsAdd, "News added successfully", Toast.LENGTH_LONG)
                        .show()
                    title.setText("")
                    desc.setText("")
                    imageView.setImageResource(0) //Clear Image View
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@NewsAdd,
                        "Failed to add news: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        }


    }

    private fun uploadImageToStorage(newsTitle: String, newsDesc: String) {
        imageUri?.let { uri ->
            val storageRef =
                storage.reference.child("news_images/" + System.currentTimeMillis() + ".jpeg")
            storageRef.putFile(uri)
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        val imageUrl = downloadUri.toString()
                        saveData(newsTitle, newsDesc, imageUrl)
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@NewsAdd,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

        }
    }

}