package com.practicum.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {
    private val trackName: TextView = trackView.findViewById(R.id.track_name)
    private val artistName: TextView = trackView.findViewById(R.id.artist_name)
    private val trackTime: TextView = trackView.findViewById(R.id.track_time)
    private val albumImage: ImageView = trackView.findViewById(R.id.album_image)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide
            .with(itemView)
            .load(model.artworkUrl100)
            .fitCenter()
            .placeholder(R.drawable.track_placeholder)
            .transform(RoundedCorners(80)) // Figma's layout of screen does not have any rounded corners at album image, so value is totally random
            .into(albumImage)
    }
}