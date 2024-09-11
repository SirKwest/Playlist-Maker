package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksConsumer
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksConsumer) {
        executor.execute {
            try {
                consumer.consume(repository.searchTracks(expression))
            } catch (error: Exception) {
                consumer.handleError(error)
            }
        }
    }
}
