package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import java.io.IOException

class PlayerImpl(private var mediaPlayer: MediaPlayer, private var url: String): PlayerInteractor {
    private val TAG = "Player_Implementation"

    private var state: PlayerInteractor.Companion.PlayerState = PlayerInteractor.Companion.PlayerState.PREPARED
    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun getState(): PlayerInteractor.Companion.PlayerState {
        return state
    }

    override fun prepare() {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } catch (e: IOException) {
            Log.e(TAG, e.printStackTrace().toString())
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
        state = PlayerInteractor.Companion.PlayerState.PREPARED
        mediaPlayer.setOnCompletionListener {
            state = PlayerInteractor.Companion.PlayerState.COMPLETED
        }
    }

    override fun start() {
        try {
            mediaPlayer.start()
            state = PlayerInteractor.Companion.PlayerState.STARTED
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }

    override fun pause() {
        try {
            mediaPlayer.pause()
            state = PlayerInteractor.Companion.PlayerState.PAUSED
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }

    override fun release() {
        try {
            mediaPlayer.release()
            state = PlayerInteractor.Companion.PlayerState.COMPLETED
        } catch (e: IllegalStateException) {
            Log.e(TAG, e.printStackTrace().toString())
        }
    }
}
