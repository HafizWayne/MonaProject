package com.dicoding.monaapp.data.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("total_balance")
	val totalBalance: Int? = null,

	@field:SerializedName("total_expense")
	val totalExpense: Int? = null,

	@field:SerializedName("total_makan")
	val totalMakan: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("credentials")
	val credentials: String? = null,

	@field:SerializedName("total_emergency")
	val totalEmergency: Int? = null,

	@field:SerializedName("dana_maksimal")
	val danaMaksimal: Int? = null
)
