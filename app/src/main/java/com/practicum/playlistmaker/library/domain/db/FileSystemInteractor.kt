package com.practicum.playlistmaker.library.domain.db

import android.net.Uri

interface FileSystemInteractor {
    fun saveImageToFile(uri: Uri): String
}
