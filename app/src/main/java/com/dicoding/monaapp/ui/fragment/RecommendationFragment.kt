import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.monaapp.R
import com.dicoding.monaapp.data.retrofit.ApiConfigML
import com.dicoding.monaapp.databinding.FragmentCategoriesBinding
import com.dicoding.monaapp.databinding.FragmentRecommendationBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val TAG = "RecommendationFragment"

class RecommendationFragment : Fragment() {
    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var foodAdapter: FoodAdapter
    private val foodList = mutableListOf<FoodResponse>()

    private var userLatLng: Pair<Double, Double>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        setupAutocompleteFragment()
        setupRecyclerView()
        getFoodRecommendations()
    }

    private fun setupAutocompleteFragment() {
        Places.initialize(requireContext(), getString(R.string.google_maps_key))

        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as? AutocompleteSupportFragment
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(place: Place) {
                    val latLng = place.latLng
                    if (latLng != null) {
                        userLatLng = Pair(latLng.latitude, latLng.longitude)
                        Log.d(TAG, "Place: ${place.name}, ${latLng.latitude}, ${latLng.longitude}")
                        setupRecyclerView() // Reinitialize the adapter with the new location
                        getFoodRecommendations()
                    }
                }

                override fun onError(status: com.google.android.gms.common.api.Status) {
                    Log.e(TAG, "An error occurred: $status")
                }
            })
            // Mengubah warna teks setelah view sudah siap
            autocompleteFragment.view?.post {
                val autocompleteTextView = autocompleteFragment.view?.findViewById<EditText>(
                    com.google.android.libraries.places.R.id.places_autocomplete_search_input
                )
                autocompleteTextView?.setTextColor(Color.WHITE) // Ganti dengan warna yang diinginkan
                autocompleteTextView?.setHintTextColor(Color.WHITE) // Ganti dengan warna yang diinginkan

    }
        } else {
            Log.e(TAG, "AutocompleteSupportFragment is null")
        }
    }


    private fun setupRecyclerView() {
        foodAdapter = FoodAdapter(emptyList(), userLatLng ?: Pair(0.0, 0.0))
        binding.rvTransactions.apply {
            adapter = foodAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }


    private fun getFoodRecommendations() {
        val userId = firebaseAuth.currentUser?.uid ?: return showToast("No user logged in")

        if (userLatLng == null) {
            showToast("Please select a location")
            return
        }

        Log.d(TAG, "User ID: $userId")
        Log.d(TAG, "User LatLng: ${userLatLng?.first}, ${userLatLng?.second}")

        val service = ApiConfigML.getApiServiceML().getRecommendations(userId)
        service.enqueue(object : Callback<RecommendationsResponse> {
            override fun onResponse(call: Call<RecommendationsResponse>, response: Response<RecommendationsResponse>) {
                if (response.isSuccessful) {
                    val recommendationsResponse = response.body()
                    val foods = recommendationsResponse?.recommendations
                    foods?.let {
                        val filteredFoods = it.filter { food ->
                            food.lat != null && food.lng != null

                        }
                        foodList.clear()
                        foodList.addAll(filteredFoods)
                        foodAdapter = FoodAdapter(foodList, userLatLng!!)
                        binding.rvTransactions.adapter = foodAdapter
                        foodAdapter.notifyDataSetChanged()
                    } ?: run {
                        showToast("No data available")
                    }
                } else {
                    showToast("Failed to retrieve data")
                }
            }

            override fun onFailure(call: Call<RecommendationsResponse>, t: Throwable) {
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

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}

