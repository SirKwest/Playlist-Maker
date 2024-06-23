package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.practicum.playlistmaker.track.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val trackJsonExtra = intent.getStringExtra(SELECTED_TRACK)
        val trackInfo = Gson().fromJson(trackJsonExtra, Track::class.java)

        val albumImage: ImageView = findViewById(R.id.big_album_cover)
        val trackName: TextView = findViewById(R.id.track_name)
        val artistName: TextView = findViewById(R.id.artist_name)
        val trackTime: TextView = findViewById(R.id.track_time_value)
        val trackAlbum: TextView = findViewById(R.id.album_value)
        val trackReleaseYear: TextView = findViewById(R.id.release_year_value)
        val trackGenre: TextView = findViewById(R.id.genre_value)
        val trackCountry: TextView = findViewById(R.id.country_value)

        val trackTimeGroup: Group = findViewById(R.id.track_time_description)
        val albumGroup: Group = findViewById(R.id.album_description)
        val releaseYearGroup: Group = findViewById(R.id.release_year_description)
        val genreGroup: Group = findViewById(R.id.genre_description)
        val countryGroup: Group = findViewById(R.id.country_description)

        val backButton = findViewById<Toolbar>(R.id.player_toolbar);
        backButton.setOnClickListener { super.finish() }

        Glide.with(this)
            .load(trackInfo.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(8))
            .into(albumImage)

        trackName.text = trackInfo.trackName
        artistName.text = trackInfo.artistName

        if (trackInfo.trackTimeMillis.isNullOrEmpty()) {
            trackTimeGroup.visibility = View.GONE
        } else {
            val time = trackInfo.trackTimeMillis.toLong()
            trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
        }

        if (trackInfo.collectionName.isNullOrEmpty()) {
            albumGroup.visibility = View.GONE
        } else {
            trackAlbum.text = trackInfo.collectionName
        }

        if (trackInfo.releaseDate.isNullOrEmpty()) {
            releaseYearGroup.visibility = View.GONE
        } else {
            trackReleaseYear.text = trackInfo.releaseDate.substring(0..3)
        }

        if (trackInfo.primaryGenreName.isNullOrEmpty()) {
            genreGroup.visibility = View.GONE
        } else {
            trackGenre.text = trackInfo.primaryGenreName
        }

        if (trackInfo.country.isNullOrEmpty()) {
            countryGroup.visibility = View.GONE
        } else {
            trackCountry.text = trackInfo.country
        }

    }

    companion object {
        const val SELECTED_TRACK = "selected_track"
    }
}