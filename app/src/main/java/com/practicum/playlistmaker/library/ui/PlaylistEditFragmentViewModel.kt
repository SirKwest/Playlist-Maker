package com.practicum.playlistmaker.library.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.library.domain.models.Playlist
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class PlaylistEditFragmentViewModel(private val playlistInteractor: PlaylistInteractor, private val application: Application) : ViewModel() {
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
        val filePath = File(
            application.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PlaylistCreationFragment.IMAGE_SUBDIRECTORY_NAME
        )

        val imageFile = File(
            filePath, SimpleDateFormat(PlaylistCreationFragment.IMAGE_NAME_FORMAT).format(
                Date()
            )
        )

        if (coverImageUri == null) {
            viewModelScope.launch {
                playlistInteractor.updatePlaylist(
                    Playlist(
                        id,
                        name,
                        description,
                        imageFile.toString(),
                        mutableListOf()
                    )
                )
            }
        } else {
            if (filePath.exists().not()) {
                filePath.mkdirs()
            }
            val outputStream = FileOutputStream(imageFile)

            viewModelScope.launch {
                var fileName = ""
                if (coverImage.compress(Bitmap.CompressFormat.PNG, 30, outputStream)) {
                    fileName = imageFile.toString()
                }
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
}