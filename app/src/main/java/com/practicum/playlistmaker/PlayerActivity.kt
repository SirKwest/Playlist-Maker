package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity: AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private lateinit var playbackTimer: TextView
    private lateinit var playButton: ImageView

    private lateinit var threadHandler: Handler

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private fun timerProgress() {
        if (!mediaPlayer.isPlaying) {
            return
        }
        playbackTimer.text = dateFormat.format(mediaPlayer.currentPosition)
        threadHandler.postDelayed(timerRunnable, 300)
    }

    private val timerRunnable = { timerProgress() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        val trackJsonExtra = intent.getStringExtra(SELECTED_TRACK)
        val trackInfo = Gson().fromJson(trackJsonExtra, Track::class.java)

        playbackTimer = findViewById(R.id.playback_timer)

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

        val backButton = findViewById<Toolbar>(R.id.player_toolbar)
        backButton.setOnClickListener { super.finish() }

        playButton = findViewById(R.id.play_button)

        threadHandler = Handler(Looper.getMainLooper())

        Glide.with(this)
            .load(trackInfo.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
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

        mediaPlayer.setDataSource(trackInfo.previewUrl)

        mediaPlayer.prepareAsync()

        playButton.setOnClickListener {
            playButton.setImageResource(R.drawable.ic_play)
            threadHandler.post(timerRunnable)
            try {
                if (mediaPlayer.isPlaying) {
                    playButton.setImageResource(R.drawable.ic_play)
                    mediaPlayer.pause()
                } else {
                    playButton.setImageResource(R.drawable.ic_pause)
                    mediaPlayer.start()
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }

        mediaPlayer.setOnCompletionListener {
            threadHandler.removeCallbacks(timerRunnable)
            playButton.setImageResource(R.drawable.ic_play)
            try {
                mediaPlayer.seekTo(0)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
            playbackTimer.text = dateFormat.format(mediaPlayer.currentPosition)
        }
    }

    override fun onPause() {
        super.onPause()
        try {
            mediaPlayer.pause()
            playButton.setImageResource(R.drawable.ic_play)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        threadHandler.removeCallbacks(timerRunnable)
    }

    companion object {
        const val SELECTED_TRACK = "selected_track"
    }
}
