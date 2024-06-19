import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.monaapp.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FoodAdapter(private val foodList: List<FoodResponse>, private val userLatLng: Pair<Double, Double>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem, userLatLng)
    }

    override fun getItemCount(): Int = foodList.size

    inner class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvMerchantName: TextView = itemView.findViewById(R.id.tvMerchantName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvPriceCategory: TextView = itemView.findViewById(R.id.tvPriceCategory)
        private val tvPriceRange: TextView = itemView.findViewById(R.id.tvPriceRange)
        private val tvTargetPrice: TextView = itemView.findViewById(R.id.tvTargetPrice)
        private val tvDistances: TextView = itemView.findViewById(R.id.tvDistances)

        fun bind(food: FoodResponse, userLatLng: Pair<Double, Double>) {
            tvMerchantName.text = food.merchantName
            tvCategory.text = food.category
            tvPriceCategory.text = when (food.priceCategory) {
                "Murah" -> "$"
                "Menengah" -> "$$"
                "Mahal" -> "$$$"
                else -> ""
            }
            tvPriceRange.text = "Harga: Rp. ${food.priceMin} - Rp. ${food.priceMax}"
            tvTargetPrice.text = " ${food.targetPrice}"

            // Calculate the distance
            val distance = calculateDistance(userLatLng.first, userLatLng.second, food.lat!!, food.lng!!)
            tvDistances.text = "${"%.1f".format(distance)} km"
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
}
