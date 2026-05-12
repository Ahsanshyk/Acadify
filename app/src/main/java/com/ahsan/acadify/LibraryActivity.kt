package com.ahsan.acadify

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.ahsan.acadify.Adapters.AdapterBooks
import com.ahsan.acadify.Models.ModelBooks
import com.ahsan.acadify.databinding.ActivityLibraryBinding
import java.util.ArrayList

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var booksArrayList: java.util.ArrayList<ModelBooks>
    private lateinit var adapterBooks: AdapterBooks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadAllBooks()

        binding.backBtn.setOnClickListener { onBackPressed() }

        binding.addBookBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@LibraryActivity,
                    AddBooksActivity::class.java
                )
            )
        }
        binding.applyIssueViewBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@LibraryActivity,
                    LibraryManagementActivity::class.java
                )
            )
        }
        binding.issuedBookBtn.setOnClickListener {
            startActivity(
                Intent(
                    this@LibraryActivity,
                    MyIssuedBookActivity::class.java
                )
            )
        }
    }

    private fun loadAllBooks() {
        booksArrayList = ArrayList()
        FirebaseFirestore.getInstance().collection("Books")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val modelBooks: ModelBooks = document.toObject(ModelBooks::class.java)
                        booksArrayList.add(modelBooks)
                    }
                    binding.booksRv.layoutManager =
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    adapterBooks = AdapterBooks(this@LibraryActivity, booksArrayList)
                    binding.booksRv.adapter = adapterBooks
                }
            }
    }
}