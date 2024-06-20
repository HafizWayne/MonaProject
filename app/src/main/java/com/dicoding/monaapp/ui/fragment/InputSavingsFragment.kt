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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.Category
import com.dicoding.monaapp.data.models.SavingRequest
import com.dicoding.monaapp.data.models.TransactionRequest
import com.dicoding.monaapp.data.response.SavingResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.databinding.FragmentInputDataBinding
import com.dicoding.monaapp.databinding.FragmentInputSavingsBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

private const val TAG = "InputIncomeFragment"

class InputSavingsFragment : Fragment() {
    private var _binding: FragmentInputSavingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val dateEditText = binding.dateInput
        val amountEditText = binding.amountInput
        val titleEditText = binding.titleInput

        // Add DatePicker to dateEditText
        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.buttonSave.setOnClickListener {
            animateButton(it) {
                val date = dateEditText.text.toString()
                val credentials = firebaseAuth.currentUser?.uid ?: "No User ID"
                val amount = amountEditText.text.toString().toIntOrNull() ?: 0
                val title = titleEditText.text.toString()

                sendUserData(date, credentials, amount, title)
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

    private fun sendUserData(date: String, credentials: String, amount: Int, title: String) {
        val savingRequest = SavingRequest(date, credentials, amount, title)
        val service = ApiConfig.getApiService().sendSavings(savingRequest)
        service.enqueue(object : Callback<SavingResponse> {
            override fun onResponse(call: Call<SavingResponse>, response: Response<SavingResponse>) {
                if (response.isSuccessful) {
                    showToast("Data sent successfully!")
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SavingResponse>, t: Throwable) {
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
