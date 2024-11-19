package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequestSuspend(dto: Any): Response
}
