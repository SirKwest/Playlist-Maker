package com.practicum.playlistmaker.library.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class PlaylistCreationFragmentViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val application: Application
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

        val filePath = File(
            application.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PlaylistCreationFragment.IMAGE_SUBDIRECTORY_NAME
        )
        if (filePath.exists().not()) {
            filePath.mkdirs()
        }
        val imageFile = File(filePath, SimpleDateFormat(PlaylistCreationFragment.IMAGE_NAME_FORMAT).format(Date()))
        val outputStream = FileOutputStream(imageFile)

        viewModelScope.launch {
            if (coverImage.compress(Bitmap.CompressFormat.PNG, 30, outputStream)) {
                fileName = imageFile.toString()
            }
            playlistInteractor.addPlaylist(Playlist(0, name, description, fileName, mutableListOf()))
        }

    }

    fun setCoverImageUri(uri: Uri) {
        coverImageUri = uri
    }
}