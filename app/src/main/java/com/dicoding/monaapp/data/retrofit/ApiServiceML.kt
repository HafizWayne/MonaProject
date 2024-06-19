package com.dicoding.monaapp.data.retrofit

import RecommendationsResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServiceML {
                @GET("recommend")
                fun getRecommendations(@Query("credentials") credentials: String): Call<RecommendationsResponse>
        }
