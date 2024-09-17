package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

class PlayerViewModel(private val playerInteractor: PlayerInteractor): ViewModel() {
    private var currentState: MutableLiveData<PlayerInteractor.Companion.PlayerState> =
        MutableLiveData(PlayerInteractor.Companion.PlayerState.PREPARED)
    private var positionState: MutableLiveData<Int> = MutableLiveData(0)
    private var threadHandler: Handler = Handler(Looper.getMainLooper())

    init {
        preparing()
    }
    fun observePlayingState(): LiveData<PlayerInteractor.Companion.PlayerState> = currentState
    fun observePositionState(): LiveData<Int> = positionState
    fun changeState() {
        when (currentState.value) {
            PlayerInteractor.Companion.PlayerState.STARTED -> pausing()
            else -> starting()
        }
    }

    fun postActualState() {
        currentState.postValue(playerInteractor.getState())
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        playerInteractor.release()
        currentState.postValue(playerInteractor.getState())
    }


    private fun timerProgress() {
        if (currentState.value !== PlayerInteractor.Companion.PlayerState.STARTED) {
            return
        }
        positionState.postValue(playerInteractor.getCurrentPosition())
        threadHandler.postDelayed(timerRunnable, TIMER_UPDATE_DELAY)
    }
    private val timerRunnable = { timerProgress() }

    private fun startTimer() {
        threadHandler.post(timerRunnable)
    }

    private fun stopTimer() {
        threadHandler.removeCallbacks(timerRunnable)
    }

    private fun pausing() {
        playerInteractor.pause()
        currentState.postValue(playerInteractor.getState())
    }

    private fun preparing() {
        playerInteractor.prepare()
        currentState.postValue(playerInteractor.getState())
        positionState.postValue(0)
    }

    private fun starting() {
        playerInteractor.start()
        currentState.postValue(playerInteractor.getState())
        startTimer()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY = 300L
    }
}
