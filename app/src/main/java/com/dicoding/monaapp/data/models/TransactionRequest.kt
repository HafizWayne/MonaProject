package com.dicoding.monaapp.data.models

data class TransactionRequest (
    val date: String,
    val credentials: String,
    val amount: Int,
    val action: String,
    val category: String,
    val food_category: String,
    val title: String,
)