package com.practicum.playlistmaker.player.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor

class PlayerViewModel(private val playerInteractor: PlayerInteractor): ViewModel() {
    private var currentState: MutableLiveData<PlayerInteractor.Companion.PlayerState> =
        MutableLiveData(com.practicum.playlistmaker.player.domain.api.PlayerInteractor.Companion.PlayerState.PREPARED)
    private var positionState: MutableLiveData<Int> = MutableLiveData(0)

    init {
        preparing()
    }
    fun observePlayingState(): LiveData<PlayerInteractor.Companion.PlayerState> = currentState
    fun observePositionState(): LiveData<Int> = positionState

    private var threadHandler: Handler = Handler(Looper.getMainLooper())
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

    companion object {
        const val TIMER_UPDATE_DELAY = 300L
        fun getViewModelFactory(trackUrl: String) : ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(Creator.providePlayerInteractor(trackUrl))
                }
            }
    }
}