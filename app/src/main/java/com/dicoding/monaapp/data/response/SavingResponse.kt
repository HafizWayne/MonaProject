package com.dicoding.monaapp.data.response

import com.google.gson.annotations.SerializedName

data class SavingResponse(

    @field:SerializedName("date")
    val date: String? = null,

    @field:SerializedName("amount")
    val amount: Int? = null,

    @field:SerializedName("credentials")
    val credentials: String? = null,

    @field:SerializedName("title")
    val title: String? = null
)
