package com.practicum.playlistmaker.library.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.library.domain.FileSystemRepository
import com.practicum.playlistmaker.library.domain.db.FileSystemInteractor

class FileSystemInteractorImpl(private val repository: FileSystemRepository) : FileSystemInteractor {
    override fun saveImageToFile(uri: Uri): String {
        return repository.saveImageToFile(uri)
    }
}
