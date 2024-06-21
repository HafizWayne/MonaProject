package com.dicoding.monaapp.data.retrofit

import com.dicoding.monaapp.data.models.SavingRequest
import com.dicoding.monaapp.data.models.TransactionRequest
import com.dicoding.monaapp.data.models.UserRequest
import com.dicoding.monaapp.data.response.SavingResponse
import com.dicoding.monaapp.data.response.TransactionResponse
import com.dicoding.monaapp.data.response.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("saving")
    fun sendSavings(
        @Body savingRequest: SavingRequest
    ): Call<SavingResponse>
    @POST("user")
    fun sendUsers(
        @Body userRequest: UserRequest
    ): Call<UserResponse>

    @GET("savings")
    fun getSavings(): Call<List<SavingResponse>>


    @POST("transaction")
    fun sendTransaction(
        @Body transactionRequest: TransactionRequest
    ): Call<TransactionResponse>
    @GET("transactions")
    fun getTransactions(): Call<List<TransactionResponse>>

    @GET("users/{id}")
    fun  getUsers(
        @Path("id") userId: String
    ): Call<UserResponse>
}