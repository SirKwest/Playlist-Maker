package com.practicum.playlistmaker

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.track.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    private var data = ArrayList<Track>()

    fun get() : ArrayList<Track> {
        val jsonString = sharedPreferences.getString(HISTORY_KEY, "")
        data = Gson().fromJson<ArrayList<Track>>(jsonString, object : TypeToken<ArrayList<Track>>() {}.type) ?: arrayListOf()
        return data
    }

    fun add(track: Track) {
        if (data.contains(track)) {
            data.remove(track)
        }
        data.add(0, track)
        if (data.size > 10) {
            data.removeAt(10)
        }
        save()
    }

    fun clear() {
        data.clear()
        sharedPreferences.edit { remove(HISTORY_KEY)}
    }

    private fun save() {
        sharedPreferences.edit { putString(HISTORY_KEY, Gson().toJson(data)) }
    }

    companion object {
        const val PREFERENCES_NAME = "search_history"
        const val HISTORY_KEY = "searched_tracks"
    }
}