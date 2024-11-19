package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor): ViewModel() {
    private var currentState: MutableLiveData<PlayerInteractor.Companion.PlayerState> =
        MutableLiveData(PlayerInteractor.Companion.PlayerState.PREPARED)
    private var positionState: MutableLiveData<Int> = MutableLiveData(0)

    private var playerTimerJob: Job? = null

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

    private fun updateActualState() {
        currentState.value = playerInteractor.getState()
        postActualState()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        playerInteractor.release()
        updateActualState()
    }

    private fun startTimer() {
        playerTimerJob?.cancel()
        playerTimerJob = viewModelScope.launch {
            while (currentState.value === PlayerInteractor.Companion.PlayerState.STARTED) {
                delay(TIMER_UPDATE_DELAY_MILLS)
                positionState.postValue(playerInteractor.getCurrentPosition())
            }
        }
    }

    private fun stopTimer() {
        playerTimerJob?.cancel()
    }

    private fun pausing() {
        playerInteractor.pause()
        updateActualState()
    }

    private fun preparing() {
        playerInteractor.prepare()
        updateActualState()
        positionState.postValue(0)
    }

    private fun starting() {
        playerInteractor.start()
        updateActualState()
        startTimer()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY_MILLS = 300L
    }
}
