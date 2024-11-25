package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track

class TrackListAdapter(private val tracks: List<Track>) : RecyclerView.Adapter<TrackViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item_layout, parent, false)
        return TrackViewHolder(view);
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener { itemClickListener?.onItemClick(position) }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.onLongClick(position)
            true}
    }

    fun getTrackByPosition(position: Int) : Track {
        return tracks[position]
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
    interface OnLongClickListener {
        fun onLongClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
    private var itemClickListener: OnItemClickListener? = null

    fun setOnLongClickListener(listener: OnLongClickListener) {
        itemLongClickListener = listener
    }
    private var itemLongClickListener: OnLongClickListener? = null
}