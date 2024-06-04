package com.dicoding.monaapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailCategoriesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_categories)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_item1 -> {
                    Toast.makeText(this, "Item 1 Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_item2 -> {
                    Toast.makeText(this, "Item 2 Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_item3 -> {
                    Toast.makeText(this, "Item 3 Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_item4 -> {
                    Toast.makeText(this, "Item 4 Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_item5 -> {
                    Toast.makeText(this, "Item 5 Selected", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}

