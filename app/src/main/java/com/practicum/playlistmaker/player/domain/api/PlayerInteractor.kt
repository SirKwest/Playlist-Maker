package com.practicum.playlistmaker.player.domain.api

interface PlayerInteractor {
    fun getState(): PlayerState
    fun getCurrentPosition(): Int
    fun prepare()
    fun start()
    fun pause()
    fun release()

    companion object {
        enum class PlayerState {
            PREPARED,
            STARTED,
            PAUSED,
            COMPLETED
        }
    }
}