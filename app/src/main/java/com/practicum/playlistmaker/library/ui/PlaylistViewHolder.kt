package com.practicum.playlistmaker.library.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemLayoutBinding
import com.practicum.playlistmaker.library.domain.models.Playlist


class PlaylistViewHolder(val binding: PlaylistItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(playlist: Playlist) {
        Glide.with(itemView)
            .load(playlist.imagePath)
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
            .into(binding.playlistIv)

        binding.playlistNameTv.text = playlist.name
        binding.playlistTrackCountTv.text = itemView.resources.getQuantityString(R.plurals.tracks, playlist.tracks, playlist.tracks)
    }
}