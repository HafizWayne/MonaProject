package com.dicoding.monaapp.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.data.response.UserResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.databinding.FragmentCategoriesBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "CategoriesFragment"

class CategoriesFragment : Fragment() {
    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        getUserData()
        getUserBalance()
    }

    private fun getUserData() {
        val userId = firebaseAuth.currentUser?.uid ?: return showToast("No user logged in")
        Log.d(TAG, "User ID: $userId")

        val service = ApiConfig.getApiService().getTransactions()
        service.enqueue(object : Callback<List<TransactionResponse>> {
            override fun onResponse(
                call: Call<List<TransactionResponse>>,
                response: Response<List<TransactionResponse>>
            ) {
                if (response.isSuccessful) {
                    val transactionList = response.body()
                    transactionList?.let {
                        Log.d(TAG, "Received ${it.size} transactions")
                        // Filter transactions by the logged-in user on the client-side
                        val userTransactions = it.filter { transaction ->
                            Log.d(TAG, "Transaction credentials: ${transaction.credentials}")
                            transaction.credentials == userId
                        }
                        Log.d(TAG, "Filtered transactions count: ${userTransactions.size}")

                        if (userTransactions.isNotEmpty()) {
                            val transaction = userTransactions[0]
                            binding.categories2.text = transaction.title
                            binding.dateMonth2.text = transaction.date
                            binding.minusPrice2.text = transaction.amount.toString()
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
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        Log.d(TAG, "User ID: ${it.id}")
                        Log.d(TAG, "Total Balance: ${it.totalBalance}")
                        Log.d(TAG, "Total Expense: ${it.totalExpense}")
                        binding.totalExpense.text = it.totalExpense.toString()
                        binding.totalBalance.text = it.totalBalance.toString()
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
            CategoriesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
