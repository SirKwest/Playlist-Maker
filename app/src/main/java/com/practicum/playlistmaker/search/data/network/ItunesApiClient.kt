package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ItunesApiClient: NetworkClient {

    private val baseUrl = "https://itunes.apple.com";

    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(
        GsonConverterFactory.create()).build()

    private val itunesService = retrofit.create<ItunesApiService>()

    override fun doRequest(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            val resp = itunesService.search(dto.expression).execute()
            val body = resp.body() ?: Response()
            body.apply { resultCode = resp.code() }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
