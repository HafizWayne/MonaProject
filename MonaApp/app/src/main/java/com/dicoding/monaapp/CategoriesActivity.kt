package com.dicoding.monaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.monaapp.data.Category
import com.dicoding.monaapp.ui.CategoryAdapter

class CategoriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        val categories = listOf(
            Category("Food", R.drawable.ic_food),
            Category("Transport", R.drawable.ic_transport),
            // Tambahkan kategori lain sesuai kebutuhan
        )

        val categoriesRecyclerView: RecyclerView = findViewById(R.id.categories_recycler_view)
        categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        categoriesRecyclerView.adapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, DetailCategoriesActivity::class.java)
            intent.putExtra("category_name", category.name)
            startActivity(intent)
        }
    }
}