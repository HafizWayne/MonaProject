package com.dicoding.monaapp.data.models

data class SavingRequest(
    val date: String,
    val credentials: String,
    val amount: Int,
    val title: String
)
