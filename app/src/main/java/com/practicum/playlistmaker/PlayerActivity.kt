package com.practicum.playlistmaker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.practicum.playlistmaker.track.Track

class PlayerActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val trackJsonExtra = intent.getStringExtra(SELECTED_TRACK)
        val trackInfo = Gson().fromJson(trackJsonExtra, Track::class.java)
    }

    companion object {
        const val SELECTED_TRACK = "selected_track"
    }
}