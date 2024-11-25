package com.practicum.playlistmaker.library.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistDetailsFragmentBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.library.ui.PlaylistsFragment.Companion.BUNDLE_PLAYLIST_ID_KEY
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.TrackListAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistDetailsFragment : Fragment() {
    private var _binding: PlaylistDetailsFragmentBinding? = null
    private val binding: PlaylistDetailsFragmentBinding get() = requireNotNull(_binding) {"Fragment playlist creation binding must not be null"}

    private val viewModel: PlaylistDetailsFragmentViewModel by viewModel()

    private var isClickAllowed = true

    private var playlistInfo: Playlist? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistDetailsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistDetailsToolbar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        viewModel.getPlaylistById(requireArguments().getInt(BUNDLE_PLAYLIST_ID_KEY))

        viewModel.observePlaylistState().observe(viewLifecycleOwner) {playlist ->
            playlistInfo = playlist
            Glide.with(this)
                .load(playlist.imagePath)
                .placeholder(R.drawable.track_placeholder)
                .centerCrop()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
                .into(binding.playlistDetailsCoverIv)

            binding.playlistDetailsDescriptionTv.text = playlist.description
            binding.playlistDetailsNameTv.text = playlist.name
            binding.playlistDetailsTotalTracksCountTv.text = resources.getQuantityString(R.plurals.tracks, playlist.addedTrackIds.size, playlist.addedTrackIds.size)
        }

        viewModel.observeTracksState().observe(viewLifecycleOwner) { tracks ->
            val historyAdapter = TrackListAdapter(tracks)
            historyAdapter.setOnItemClickListener(object :
                TrackListAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    if (!clickDebounce()) {
                        return
                    }
                    val item = historyAdapter.getTrackByPosition(position)
                    val playerActivityIntent = Intent(requireContext(), PlayerActivity::class.java)
                    playerActivityIntent.putExtra(
                        PlayerActivity.SELECTED_TRACK,
                        Gson().toJson(item)
                    )
                    startActivity(playerActivityIntent)
                }
            })
            historyAdapter.setOnLongClickListener(object : TrackListAdapter.OnLongClickListener {
                override fun onLongClick(position: Int) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.delete_track)
                        .setMessage(R.string.do_you_want_delete_track)
                        .setNegativeButton(R.string.no) { _, _ -> }
                        .setPositiveButton(R.string.yes) { _, _ ->
                            viewModel.deleteTrackFromPlaylist(historyAdapter.getTrackByPosition(position), playlistInfo)
                        }.show()
                    historyAdapter.notifyDataSetChanged()
                }
            })
            binding.playlistDetailsBottomSheetRv.adapter = historyAdapter
            binding.playlistDetailsTotalDurationTv.text = resources.getQuantityString(
                R.plurals.minutes,
                viewModel.calculateCombinedTracksDuration(tracks, "mm").toInt(),
                viewModel.calculateCombinedTracksDuration(tracks, "mm").toInt()
            )
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val panel = requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view)
        if (panel !== null) {
            panel.isVisible = false
        }
    }

    override fun onDetach() {
        super.onDetach()
        val panel = requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view)
        if (panel !== null) {
            panel.isVisible = true
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(SearchFragment.CLICK_DELAY_MILLS)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        fun newInstance(id: Int) = PlaylistDetailsFragment().apply {
            arguments = bundleOf(BUNDLE_PLAYLIST_ID_KEY to id)
        }
    }
}