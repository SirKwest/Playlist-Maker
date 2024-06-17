package com.practicum.playlistmaker.track

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {
    private val trackName: TextView = trackView.findViewById(R.id.track_name)
    private val artistName: TextView = trackView.findViewById(R.id.artist_name)
    private val trackTime: TextView = trackView.findViewById(R.id.track_time)
    private val albumImage: ImageView = trackView.findViewById(R.id.album_image)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        val time = model.trackTimeMillis.toLong()
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
        Glide
            .with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(4))
            .into(albumImage)
        artistName.requestLayout()
    }
}