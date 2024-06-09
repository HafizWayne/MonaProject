package com.dicoding.monaapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.R
import com.dicoding.monaapp.ui.fragment.CategoriesFragment
import com.dicoding.monaapp.ui.fragment.HomeFragment
import com.dicoding.monaapp.ui.fragment.InputDataFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_item1 -> {
                replaceFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_item2 -> {
                replaceFragment(CategoriesFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_item3 -> {
                replaceFragment(InputDataFragment())
                return@OnNavigationItemSelectedListener true
            }
            else -> false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_nav_home)
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Set the default fragment
        if (savedInstanceState == null) {
            replaceFragment(CategoriesFragment())
        }
    }
}