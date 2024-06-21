package com.dicoding.monaapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.monaapp.data.models.UserRequest
import com.dicoding.monaapp.data.response.UserResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.databinding.ActivityRegisterBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        val fabBack: FloatingActionButton = findViewById(R.id.fab_back)
        fabBack.setOnClickListener {
            onBackPressed()
        }

        binding.signupButton.setOnClickListener {
            animateButton(binding.signupButton) {
                signUp()
            }
        }
    }

    private fun animateButton(view: View, onAnimationEnd: () -> Unit) {
        val animation = AnimationUtils.loadAnimation(this, R.anim.button_press_anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                view.isEnabled = false
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

    private fun signUp() {
        val email = binding.email.text.toString()
        val pass = binding.password.text.toString()
        val confirmPass = binding.confirmPassword.text.toString()
        val nama = binding.fullName.text.toString() // Get full name

        if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty() && nama.isNotEmpty()) {
            if (pass == confirmPass) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = firebaseAuth.currentUser?.uid
                        if (userId != null) {
                            sendUserData(userId, nama, 0, 0, 0, 0, 0) // Initial values set to 0
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Empty Fields Are Not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendUserData(credentials: String, nama: String, totalBalance: Int, totalExpense: Int, totalEmergency: Int, danaMaksimal: Int, totalMakan: Int) {
        val userRequest = UserRequest(credentials, nama, totalBalance, totalExpense, totalEmergency, danaMaksimal, totalMakan)
        val service =  ApiConfig.getApiService().sendUsers(userRequest)
        service.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "User data sent successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@RegisterActivity, "Failed to send user data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onRegisterActivityClick(view: View) {
        val intent = Intent(this, Result::class.java)
        startActivity(intent)
    }
}
