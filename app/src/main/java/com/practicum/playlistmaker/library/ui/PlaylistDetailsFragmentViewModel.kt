package com.practicum.playlistmaker.library.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val tracksInteractor: FavoritesInteractor,
    private val application: Application
) : ViewModel() {
    private val playlistLiveData = MutableLiveData<Playlist>()
    private val tracksLiveData = MutableLiveData<List<Track>>()

    fun observePlaylistState(): LiveData<Playlist> = playlistLiveData
    fun observeTracksState(): LiveData<List<Track>> = tracksLiveData

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                playlistLiveData.postValue(playlist)
                if (playlist.addedTrackIds.isEmpty().not()) {
                    playlistInteractor.getTracksByIds(playlist.addedTrackIds).collect {tracks ->
                        val sortedTracks: MutableList<Track> = mutableListOf()
                        playlist.addedTrackIds.forEach {trackId -> tracks.find { track -> track.trackId == trackId }
                            ?.let { sortedTracks.add(it) } }
                        tracksLiveData.postValue(sortedTracks)
                    }
                } else {
                    tracksLiveData.postValue(listOf())
                }
            }
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylistById(playlist.id)
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlist: Playlist?) {
        if (playlist == null) {
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
            if (!track.isFavorite) {
                playlistInteractor.getPlaylistIdsByTrackId(track.trackId).collect {ids ->
                    if (ids.isEmpty()) {
                        tracksInteractor.removeTrackRecord(track.trackId)
                    }
                }
            }
            getPlaylistById(playlist.id)
        }
    }

    fun calculateCombinedTracksDuration(tracks: List<Track>, format: String) : String {
        val totalDurationMills = tracks.sumOf { it.trackTimeMillis?.toInt()!! }
        return SimpleDateFormat(format, Locale.getDefault()).format(totalDurationMills)
    }


    fun sharePlaylist(playlist: Playlist): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        var message = application.getString(R.string.playlist) + " \"${playlist.name}\"\n" + "${playlist.description ?: ""}\n" + "${
            application.resources.getQuantityString(
                R.plurals.tracks,
                playlist.addedTrackIds.size,
                playlist.addedTrackIds.size
            )
        }:\n"
        var i = 1
        tracksLiveData.value?.map { track ->
            message += "$i." + "${track.artistName} - " + "${track.trackName} " + "(${
                track.getTrackTimeFormatted("mm:ss")
            })\n"
            i++
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)

        return Intent.createChooser(
            shareIntent,
            application.getString(R.string.choose_app_for_share)
        )
    }
}
