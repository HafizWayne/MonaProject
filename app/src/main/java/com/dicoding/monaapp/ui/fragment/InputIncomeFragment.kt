package com.dicoding.monaapp.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.data.models.TransactionRequest
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.databinding.FragmentInputIncomeBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

private const val TAG = "InputIncomeFragment"

class InputIncomeFragment : Fragment() {
    private var _binding: FragmentInputIncomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputIncomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val dateEditText = binding.dateInput
        val categorySpinner = binding.categoryInput
        val amountEditText = binding.amountInput
        val titleEditText = binding.titleInput
        val foodCategorySpinner = binding.foodCategoryInput  // Added foodCategorySpinner

        // Populate the main category Spinner
        val categories = arrayOf("Salary", "Additional") // Updated categories
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Set initial visibility of foodCategorySpinner
        foodCategorySpinner.visibility = View.GONE

        // Add DatePicker to dateEditText
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        // Listener for categorySpinner to show/hide foodCategorySpinner
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                if (selectedCategory == "Food") {
                    foodCategorySpinner.visibility = View.VISIBLE
                } else {
                    foodCategorySpinner.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        binding.buttonSave.setOnClickListener {
            val date = dateEditText.text.toString()
            val credentials = firebaseAuth.currentUser?.uid ?: "No User ID"
            val amount = amountEditText.text.toString().toIntOrNull() ?: 0
            val action = "income"
            val category = categorySpinner.selectedItem.toString()
            val title = titleEditText.text.toString()

            // Determine foodCategory value based on category selection
            val foodCategory = if (category == "Food") {
                foodCategorySpinner.selectedItem.toString()
            } else {
                "" // Send empty string if category is not Food
            }

            sendUserData(date, credentials, amount, action, category,foodCategory, title)
        }

        addTextWatchers()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedMonth = String.format("%02d", selectedMonth + 1)
                val formattedDay = String.format("%02d", selectedDay)
                val selectedDate = "$selectedYear-$formattedMonth-$formattedDay"
                binding.dateInput.setText(selectedDate)
            }, year, month, day)
        datePickerDialog.show()
    }

    private fun sendUserData(date: String, credentials: String, amount: Int, action: String, category: String, title: String, foodCategory: String) {
        val transactionRequest = TransactionRequest(date, credentials, amount, action, category, title, foodCategory)
        val service = ApiConfig.getApiService().sendTransaction(transactionRequest)
        service.enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful) {
                    showToast("Data sent successfully!")
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun addTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.dateInput.addTextChangedListener(textWatcher)
        binding.amountInput.addTextChangedListener(textWatcher)
        binding.titleInput.addTextChangedListener(textWatcher)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = InputIncomeFragment()
    }
}
