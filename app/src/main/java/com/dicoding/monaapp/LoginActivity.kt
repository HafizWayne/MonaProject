package com.dicoding.monaapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.monaapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    companion object {
        private const val TAG = "LoginActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            animateButton(it) {
                val email = binding.usernameEmail.text.toString()
                val pass = binding.password.text.toString()

                Log.d(TAG, "Email: $email, Password: $pass")

                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    performLogin(email, pass)
                } else {
                    Log.d(TAG, "Empty fields detected")
                    Toast.makeText(this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun animateButton(view: View, onAnimationEnd: () -> Unit) {
        view.isEnabled = false

        val animation = AnimationUtils.loadAnimation(this, R.anim.button_press_anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                view.isEnabled = true
                view.clearAnimation()
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        view.startAnimation(animation)
    }

    private fun performLogin(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.d(TAG, "Login successful")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            } else {
                Log.w(TAG, "Login failed", it.exception)
                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

}