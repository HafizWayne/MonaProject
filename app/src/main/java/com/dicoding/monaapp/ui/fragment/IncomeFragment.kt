package com.dicoding.monaapp.ui.fragment

import TransactionAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.data.response.UserResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.databinding.FragmentIncomeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.Locale

private const val TAG = "IncomeFragment"

class IncomeFragment : Fragment() {
    private var _binding: FragmentIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var transactionAdapter: TransactionAdapter
    private val transactionList = mutableListOf<TransactionResponse>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()



        setupRecyclerView()
        getUserData()
        getUserBalance()

        binding.buttonSubmit.setOnClickListener {
            animateButton(it) {
                navigateToInputIncomeFragment()
            }
        }
    }

    private fun animateButton(view: View, onAnimationEnd: () -> Unit) {
        view.isEnabled = false

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.button_press_anim)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                view.isEnabled = true
                onAnimationEnd()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        view.startAnimation(animation)
    }

    private fun navigateToInputIncomeFragment() {
        val fragment = InputIncomeFragment()
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter(transactionList)
        binding.rvTransactions.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
                    val transactions = response.body()
                    transactions?.let {
                        Log.d(TAG, "Received ${it.size} transactions")
                        val userTransactions = it.filter { transaction ->
                            transaction.credentials == userId && transaction.action == "income"
                        }
                        Log.d(TAG, "Filtered transactions count: ${userTransactions.size}")

                        if (userTransactions.isNotEmpty()) {
                            transactionList.clear()
                            transactionList.addAll(userTransactions)
                            transactionAdapter.notifyDataSetChanged()
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
            ExpenseFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}

