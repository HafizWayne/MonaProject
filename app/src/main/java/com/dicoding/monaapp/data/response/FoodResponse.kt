import com.google.gson.annotations.SerializedName

data class FoodResponse(
	@SerializedName("alamat")
	val alamat: String? = null,

	@SerializedName("category")
	val category: String? = null,

	@SerializedName("lat")
	val lat: Double? = null,

	@SerializedName("link")
	val link: String? = null,

	@SerializedName("lng")
	val lng: Double? = null,

	@SerializedName("merchant_name")
	val merchantName: String? = null,

	@SerializedName("price_category")
	val priceCategory: String? = null,

	@SerializedName("price_max")
	val priceMax: Int? = null,

	@SerializedName("price_min")
	val priceMin: Int? = null,

	@SerializedName("target_price")
	val targetPrice: Int? = null
)

data class RecommendationsResponse(
	@SerializedName("recommendations")
	val recommendations: List<FoodResponse>
)
