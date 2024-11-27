package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FileSystemInteractor
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistCreationFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val fileSystemInteractor: FileSystemInteractor,
) : ViewModel() {
    private var coverImageUri : Uri? = null

    fun createPlaylist(name: String, description: String, coverImage: Bitmap?) {
        var fileName = ""
        if (coverImageUri == null || coverImage == null) {
            viewModelScope.launch {
                playlistInteractor.addPlaylist(Playlist(0, name, description, fileName, mutableListOf()))
            }
            return
        }

        fileName = fileSystemInteractor.saveImageToFile(coverImageUri!!)

        viewModelScope.launch {
            playlistInteractor.addPlaylist(Playlist(0, name, description, fileName, mutableListOf()))
        }

    }

    fun setCoverImageUri(uri: Uri) {
        coverImageUri = uri
    }
}
