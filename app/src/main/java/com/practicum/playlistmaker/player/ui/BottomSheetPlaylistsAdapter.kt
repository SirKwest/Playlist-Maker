package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlayerBottomSheetPlaylistItemLayoutBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

class BottomSheetPlaylistsAdapter(private val playlists: List<Playlist>) : RecyclerView.Adapter<BottomSheetPlaylistsViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BottomSheetPlaylistsViewHolder {
        return BottomSheetPlaylistsViewHolder(PlayerBottomSheetPlaylistItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(position) }
    }
    fun getItemByPosition(position: Int) : Playlist {
        return playlists[position]
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null
}
