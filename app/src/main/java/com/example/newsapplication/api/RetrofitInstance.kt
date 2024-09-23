package com.example.newsapplication.api

import android.provider.SyncStateContract.Constants
import com.example.newsapplication.util.Costants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object{
        // Lazily initialize Retrofit instance
            private val retrofit by lazy{
            // Create a logging interceptor to log request and response bodies
                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            // Create an OkHttpClient with the logging interceptor
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()

            // Build Retrofit instance
                Retrofit.Builder()
                    .baseUrl(BASE_URL)// Set the base URL
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
        val api by lazy{
            // Create implementation of NewsAPI interface
            retrofit.create(NewsAPI::class.java)
        }
    }
}