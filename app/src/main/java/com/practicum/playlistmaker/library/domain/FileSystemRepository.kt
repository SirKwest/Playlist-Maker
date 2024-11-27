package com.practicum.playlistmaker.library.domain

import android.net.Uri

interface FileSystemRepository {
    fun saveImageToFile(uri: Uri): String
}