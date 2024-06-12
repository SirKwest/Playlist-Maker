package com.practicum.playlistmaker.api

import com.practicum.playlistmaker.track.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ItunesApiClient {

    private val baseUrl = "https://itunes.apple.com";

    fun getTrackList(query: String): Call<ItunesResponse> {
        val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(
            GsonConverterFactory.create()).build()
        val itunesService = retrofit.create<ItunesApiService>()
        return itunesService.search(query)
        /*
        itunesService.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                if (response.isSuccessful) {
                    return response.body().response
                } else {
                    val errorJson = response.errorBody()?.string()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                t.printStackTrace()
            }

        })
        return tracks

         */
    }
}