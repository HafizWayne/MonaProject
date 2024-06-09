package com.dicoding.monaapp.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.data.models.SavingRequest
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.data.response.SavingResponse
import com.dicoding.monaapp.databinding.FragmentInputDataBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "InputDataFragment"

class InputDataFragment : Fragment() {
    private var _binding: FragmentInputDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInputDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dateEditText = binding.dateInput
        val credentialsEditText = binding.credentialsInput
        val amountEditText = binding.amountInput
        val titleEditText = binding.titleInput

        binding.buttonSave.setOnClickListener {
            val date = dateEditText.text.toString()
            val credentials = credentialsEditText.text.toString()
            val amount = amountEditText.text.toString().toIntOrNull() ?: 0
            val title = titleEditText.text.toString()
            sendUserData(date, credentials, amount, title)
        }

        addTextWatchers()
    }

    private fun sendUserData(date: String, credentials: String, amount: Int, title: String) {
        val savingRequest = SavingRequest(date, credentials, amount, title)
        val service = ApiConfig.getApiService().sendSavings(savingRequest)
        service.enqueue(object : Callback<SavingResponse> {
            override fun onResponse(
                call: Call<SavingResponse>,
                response: Response<SavingResponse>
            ) {
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
            override fun afterTextChanged(s: Editable?) {

            }
        }
        binding.credentialsInput.addTextChangedListener(textWatcher)
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
        fun newInstance() = InputDataFragment()
    }
}
