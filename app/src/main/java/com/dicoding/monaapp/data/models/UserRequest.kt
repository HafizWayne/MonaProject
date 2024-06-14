package com.dicoding.monaapp.data.models

data class UserRequest (
    val id: String,
    val nama: String,
    val total_balance: Int,
    val total_expense: Int,
    val total_emergency: String
)