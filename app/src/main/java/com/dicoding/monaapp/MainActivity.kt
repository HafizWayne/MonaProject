package com.dicoding.monaapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.ui.fragment.CategoriesFragment
import com.dicoding.monaapp.ui.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {



    fun onRegisterClick(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    fun onLoginClick(view: View) {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }
}

