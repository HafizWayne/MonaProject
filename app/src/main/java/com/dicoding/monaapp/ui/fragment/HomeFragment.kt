package com.dicoding.monaapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.monaapp.R
import com.dicoding.monaapp.adapter.HomeAdapter
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.data.response.UserResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var homeAdapter: HomeAdapter
    private val transactionList = mutableListOf<TransactionResponse>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        setupRecyclerView()
        getUserData()
        getUserBalance()
        setGreetingMessage()
    }

    private fun setupRecyclerView() {
        homeAdapter = HomeAdapter(transactionList)
        binding.rvTransactions.apply {
            adapter = homeAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getUserData() {
        val userId = firebaseAuth.currentUser?.uid ?: return showToast("No user logged in")
        Log.d(TAG, "User ID: $userId")

        val service = ApiConfig.getApiService().getUsers(userId)
        service.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        Log.d(TAG, "User ID: ${it.id}")
                        binding.userName.text = "Hi, ${it.nama}"
                        getTransactions(userId)
                    } ?: run {
                        showToast("User data not found")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    showToast("Failed to retrieve user data")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                showToast("Failed to retrieve user data")
            }
        })
    }

    private fun getTransactions(userId: String) {
        val service = ApiConfig.getApiService().getTransactions()
        service.enqueue(object : Callback<List<TransactionResponse>> {
            override fun onResponse(
                call: Call<List<TransactionResponse>>,
                response: Response<List<TransactionResponse>>
            ) {
                if (response.isSuccessful) {
                    val transactions = response.body()
                    transactions?.let {
                        Log.d(TAG, "Received ${it.size} transactions")
                        val userTransactions = it.filter { transaction ->
                            transaction.credentials == userId
                        }
                        Log.d(TAG, "Filtered transactions count: ${userTransactions.size}")

                        if (userTransactions.isNotEmpty()) {
                            transactionList.clear()
                            transactionList.addAll(userTransactions)
                            homeAdapter.notifyDataSetChanged()
                        } else {
                            showToast("No data available")
                        }
                    } ?: run {
                        showToast("No data available")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    showToast("Failed to retrieve data")
                }
            }

            override fun onFailure(call: Call<List<TransactionResponse>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                showToast("Failed to retrieve data")
            }
        })
    }

    private fun getUserBalance() {
        val userId = firebaseAuth.currentUser?.uid ?: return showToast("No user logged in")
        Log.d(TAG, "User ID: $userId")

        val service = ApiConfig.getApiService().getUsers(userId)
        service.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        Log.d(TAG, "User ID: ${it.id}")
                        Log.d(TAG, "Total Balance: ${it.totalBalance}")
                        Log.d(TAG, "Total Expense: ${it.totalExpense}")
                        val localeID = Locale("in", "ID")
                        val formattedAmountExpense = NumberFormat.getNumberInstance(localeID).format(it.totalExpense)
                        val formattedAmountIncome = NumberFormat.getNumberInstance(localeID).format(it.totalBalance)

                        binding.totalExpense.text = "-Rp. $formattedAmountExpense"
                        binding.totalBalance.text = "Rp. $formattedAmountIncome"
                    } ?: run {
                        showToast("User data not found")
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    showToast("Failed to retrieve user data")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                showToast("Failed to retrieve user data")
            }
        })
    }

    private fun setGreetingMessage() {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greetingMessage = when {
            currentHour in 5..11 -> "Good Morning"
            currentHour in 12..17 -> "Good Afternoon"
            else -> "Good Evening"
        }
        binding.timeGreeting.text = greetingMessage
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}
