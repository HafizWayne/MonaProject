package com.dicoding.monaapp.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dicoding.monaapp.LoginActivity
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.response.SavingResponse
import com.dicoding.monaapp.data.retrofit.ApiConfig
import com.dicoding.monaapp.databinding.FragmentCategoriesBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "CategoriesFragment"

class CategoriesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var _binding: FragmentCategoriesBinding? = null
    private lateinit var binding: FragmentCategoriesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            getUserData()
        }
        getUserData()
    }


    private fun getUserData() {
        val service = ApiConfig.getApiService().getSavings()
        service.enqueue(object : Callback<List<SavingResponse>> {
            override fun onResponse(
                call: Call<List<SavingResponse>>,
                response: Response<List<SavingResponse>>
            ) {
                if (response.isSuccessful) {
                    val savingsList = response.body()
                    savingsList?.let {
                        // Assuming you want to show the first item in the list for simplicity
                        if (it.isNotEmpty()) {
                            val saving = it[1]
                            binding.categories2.text = saving.title
                            binding.dateMonth2.text = saving.date
                            binding.minusPrice2.text = saving.amount.toString()
                        } else {
                            showToast("No data available")
                        }
                    }
                } else {
                    Log.e(TAG, "onFailure: ${response.message()}")
                    showToast("Failed to retrieve data")
                }
            }

            override fun onFailure(call: Call<List<SavingResponse>>, t: Throwable) {
                Log.e(TAG, "onFailure: ${t.message}")
                showToast("Failed to retrieve data")
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
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
