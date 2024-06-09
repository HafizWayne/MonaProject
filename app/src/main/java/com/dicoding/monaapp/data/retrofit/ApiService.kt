package com.dicoding.monaapp.data.retrofit

import com.dicoding.monaapp.data.models.SavingRequest
import com.dicoding.monaapp.data.response.SavingResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("saving")
    fun sendSavings(
        @Body savingRequest: SavingRequest
    ): Call<SavingResponse>

    @GET("savings")
    fun getSavings(): Call<List<SavingResponse>>
}