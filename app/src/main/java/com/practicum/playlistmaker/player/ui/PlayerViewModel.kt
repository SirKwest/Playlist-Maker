package com.practicum.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistInteractor
): ViewModel() {
    private var currentState: MutableLiveData<PlayerInteractor.Companion.PlayerState> =
        MutableLiveData(PlayerInteractor.Companion.PlayerState.PREPARED)
    private var positionState: MutableLiveData<Int> = MutableLiveData(0)
    private var favoriteState: MutableLiveData<Boolean> = MutableLiveData()
    private val bottomSheetPlaylistsState = MutableLiveData<BottomSheetPlaylistsState>()

    private var playerTimerJob: Job? = null

    init {
        preparingPlayer()
    }
    fun observePlayingState(): LiveData<PlayerInteractor.Companion.PlayerState> = currentState
    fun observePositionState(): LiveData<Int> = positionState
    fun observeFavoriteState(): LiveData<Boolean> = favoriteState
    fun observeBottomSheetState(): LiveData<BottomSheetPlaylistsState> = bottomSheetPlaylistsState

    fun getPlaylistsForBottomSheet() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect{playlists ->
                if (playlists.isEmpty()) {
                    postPlaylistsState(BottomSheetPlaylistsState.Empty)
                } else {
                    postPlaylistsState(BottomSheetPlaylistsState.ShowPlaylists(playlists))
                }
            }
        }
    }

    fun isTrackAlreadyInPlaylist(track: Track, playlist: Playlist) : Boolean {
        return track.trackId in playlist.addedTrackIds
    }

    fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addTrackToPlaylist(track, playlist)
        }
    }

    private fun postPlaylistsState(state: BottomSheetPlaylistsState) {
        bottomSheetPlaylistsState.postValue(state)
    }

    fun changePlayerState() {
        when (currentState.value) {
            PlayerInteractor.Companion.PlayerState.STARTED -> pausing()
            else -> startingPlayer()
        }
    }

    fun postActualPlayerState() {
        currentState.postValue(playerInteractor.getState())
    }

    fun changeFavoriteStatus(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if (track.isFavorite) {
                track.isFavorite = false
                favoritesInteractor.removeFromFavorites(track.trackId)
            } else {
                track.isFavorite = true
                favoritesInteractor.addToFavorites(track)
            }
            favoriteState.postValue(track.isFavorite)
        }
    }


    private fun updateActualPlayerState() {
        currentState.value = playerInteractor.getState()
        postActualPlayerState()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        playerInteractor.release()
        updateActualPlayerState()
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
        updateActualPlayerState()
    }

    private fun preparingPlayer() {
        playerInteractor.prepare()
        updateActualPlayerState()
        positionState.postValue(0)
    }

    private fun startingPlayer() {
        playerInteractor.start()
        updateActualPlayerState()
        startTimer()
    }

    companion object {
        private const val TIMER_UPDATE_DELAY_MILLS = 300L
    }
}
