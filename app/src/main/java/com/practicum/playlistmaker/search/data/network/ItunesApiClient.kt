package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response
import com.practicum.playlistmaker.search.data.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class ItunesApiClient: NetworkClient {

    private val baseUrl = "https://itunes.apple.com";

    private val retrofit = Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(
        GsonConverterFactory.create()).build()

    private val itunesService = retrofit.create<ItunesApiService>()

    override suspend fun doRequestSuspend(dto: Any): Response {
        return if (dto is TrackSearchRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val response = itunesService.search(dto.expression)
                    response.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    Response().apply { resultCode = 500 }
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}
