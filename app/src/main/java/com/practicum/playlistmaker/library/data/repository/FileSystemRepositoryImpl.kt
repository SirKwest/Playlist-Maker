package com.practicum.playlistmaker.library.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.FileSystemRepository
import com.practicum.playlistmaker.library.ui.PlaylistCreationFragment
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

class FileSystemRepositoryImpl(private val context: Context): FileSystemRepository {
    override fun saveImageToFile(uri: Uri): String {
        val filePath = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PlaylistCreationFragment.IMAGE_SUBDIRECTORY_NAME
        )
        if (filePath.exists().not()) {
            filePath.mkdirs()
        }
        val imageFile = File(filePath, SimpleDateFormat(PlaylistCreationFragment.IMAGE_NAME_FORMAT).format(
            Date()
        ))
        val outputStream = FileOutputStream(imageFile)

        if (BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri)).compress(Bitmap.CompressFormat.PNG, 30, outputStream)) {
            return imageFile.toString()
        } else {
            return ""
        }
    }
}
