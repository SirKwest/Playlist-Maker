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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDetailsFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val favoritesInteractor: FavoritesInteractor,
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
                    favoritesInteractor.getAllFavorites().collect {tracks ->
                        tracksLiveData.postValue(tracks)
                    }
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
        viewModelScope.launch {
            playlistInteractor.deleteTrackFromPlaylist(track, playlist)
        }
    }

    fun calculateCombinedTracksDuration(tracks: List<Track>, format: String) : String {
        val totalDurationMills = tracks.sumOf { it.trackTimeMillis?.toInt()!! }
        return SimpleDateFormat(format, Locale.getDefault()).format(totalDurationMills)
    }


    fun sharePlaylist(playlist: Playlist?): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        var message = application.getString(R.string.playlists) + " \"${playlist!!.name}\"\n" + "${playlist.description ?: ""}\n" + "${
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
