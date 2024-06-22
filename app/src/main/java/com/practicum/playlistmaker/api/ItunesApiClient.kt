package com.practicum.playlistmaker.api

import retrofit2.Call
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
    }
}
