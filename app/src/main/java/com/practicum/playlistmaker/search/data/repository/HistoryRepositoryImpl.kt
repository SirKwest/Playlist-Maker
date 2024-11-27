package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.library.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.models.HistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryRepositoryImpl(private val sharedPreferences: SharedPreferences, private val appDatabase: AppDatabase): HistoryRepository {
    private var data = ArrayList<Track>()
    override fun get(): Flow<ArrayList<Track>> = flow {
        val jsonString = sharedPreferences.getString(HistoryRepository.HISTORY_KEY, "")
        data = Gson().fromJson<ArrayList<Track>>(
            jsonString,
            object : TypeToken<ArrayList<Track>>() {}.type
        ) ?: arrayListOf()
        data.map{
            val track = appDatabase.tracksDao().getTrack(it.trackId)
            it.isFavorite = track?.isFavorite == 1 }
        emit(data)
    }

    override fun add(track: Track) {
        if (data.contains(track)) {
            data.remove(track)
        }
        data.add(0, track)
        if (data.size > 10) {
            data.removeAt(10)
        }
        save()
    }

    override fun clear() {
        data.clear()
        sharedPreferences.edit { remove(HistoryRepository.HISTORY_KEY) }
    }

    private fun save() {
        sharedPreferences.edit { putString(HistoryRepository.HISTORY_KEY, Gson().toJson(data)) }
    }
}