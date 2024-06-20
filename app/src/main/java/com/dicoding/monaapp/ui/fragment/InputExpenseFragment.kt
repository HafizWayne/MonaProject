package com.dicoding.monaapp.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.models.TransactionRequest
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.databinding.FragmentInputDataBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

private const val TAG = "InputExpenseFragment"

class InputExpenseFragment : Fragment() {
    private var _binding: FragmentInputDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val dateEditText = binding.dateInput
        val categorySpinner = binding.categoryInput
        val foodCategorySpinner = binding.foodCategoryInput
        val amountEditText = binding.amountInput
        val titleEditText = binding.titleInput

        // Populate the main category Spinner
        val categories = arrayOf("Food", "Transport", "Groceries", "Bills", "Medicine")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        // Populate the food category Spinner
        val foodCategories = arrayOf(
            "Aneka nasi",
            "Ayam & bebek",
            "Bakmie",
            "Bakso & soto",
            "Barat",
            "Cepat saji",
            "Chinese",
            "India",
            "Jajanan",
            "Jepang",
            "Kopi",
            "Korea",
            "Martabak",
            "Minuman",
            "Pizza & pasta",
            "Roti",
            "Sate",
            "Seafood",
            "Sweets",
            "Timur Tengah"
        )
        val foodCategoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, foodCategories)
        foodCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        foodCategorySpinner.adapter = foodCategoryAdapter

        // Set listener for main category spinner
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

        // Add DatePicker to dateEditText
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSave.setOnClickListener {
            animateButton(it) {
                val date = dateEditText.text.toString()
                val credentials = firebaseAuth.currentUser?.uid ?: "No User ID"
                val amount = amountEditText.text.toString().toIntOrNull() ?: 0
                val action = "expense"
                val category = categorySpinner.selectedItem.toString()
                val title = titleEditText.text.toString()
                val foodCategory = if (category == "Food") foodCategorySpinner.selectedItem.toString() else ""

                sendUserData(date, credentials, amount, action, category, foodCategory, title)
            }
        }

        addTextWatchers()
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
        fun newInstance() = InputExpenseFragment()
    }
}
