package com.dicoding.monaapp.data.response

import com.google.gson.annotations.SerializedName


data class TransactionResponse(

	@field:SerializedName("date")
	val date: String? = null,

	@field:SerializedName("amount")
	val amount: Int? = null,

	@field:SerializedName("credentials")
	val credentials: String? = null,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("category")
	val category: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)
