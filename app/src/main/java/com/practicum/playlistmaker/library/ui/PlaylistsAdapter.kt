package com.practicum.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.databinding.PlaylistItemLayoutBinding

class PlaylistsAdapter(private val playlist: List<Playlist>) : RecyclerView.Adapter<PlaylistsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistsViewHolder {
        return PlaylistsViewHolder(PlaylistItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistsViewHolder, position: Int) {
        holder.bind(playlist[position])
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(position) }
    }
    fun getItemByPosition(position: Int) : Playlist {
        return playlist[position]
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null
}