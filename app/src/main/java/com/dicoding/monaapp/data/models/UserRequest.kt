package com.dicoding.monaapp.data.models

data class UserRequest (
    val credentials: String,
    val nama: String,
    val total_balance: Int,
    val total_expense: Int,
    val total_emergency: Int,
    val dana_maksimal: Int,
    val total_makan: Int
)