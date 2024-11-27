package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FileSystemInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistEditFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val fileSystemInteractor: FileSystemInteractor,
) : ViewModel() {
    private var coverImageUri : Uri? = null
    private val playlistLiveData = MutableLiveData<Playlist>()
    fun observePlaylistState(): LiveData<Playlist> = playlistLiveData
    fun setCoverImageUri(uri: Uri) {
        coverImageUri = uri
    }
    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            playlistInteractor.getPlaylistById(id).collect { playlist ->
                playlistLiveData.postValue(playlist)
            }
        }
    }

    fun updatePlaylist(id: Int, name: String, description: String, coverImage: Bitmap?) {
        if (coverImageUri == null || coverImage == null) {
            viewModelScope.launch {
                playlistInteractor.updatePlaylist(
                    Playlist(
                        id,
                        name,
                        description,
                        "",
                        mutableListOf()
                    )
                )
            }
            return
        }

        val fileName = fileSystemInteractor.saveImageToFile(coverImageUri!!)
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(
                Playlist(
                    id,
                    name,
                    description,
                    fileName,
                    mutableListOf()
                )
            )
        }
    }
}
