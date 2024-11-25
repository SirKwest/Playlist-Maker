package com.practicum.playlistmaker.library.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistDetailsFragmentBinding
import com.practicum.playlistmaker.library.ui.PlaylistsFragment.Companion.BUNDLE_PLAYLIST_ID_KEY

class PlaylistDetailsFragment : Fragment() {
    private var _binding: PlaylistDetailsFragmentBinding? = null
    private val binding: PlaylistDetailsFragmentBinding get() = requireNotNull(_binding) {"Fragment playlist creation binding must not be null"}

    private val viewModel: PlaylistDetailsFragmentViewModel by viewModel()

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
            Glide.with(this)
                .load(playlist.imagePath)
                .placeholder(R.drawable.track_placeholder)
                .centerCrop()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
                .into(binding.playlistDetailsCoverIv)

            binding.playlistDetailsDescriptionTv.text = playlist.description
            binding.playlistDetailsNameTv.text = playlist.name
            binding.playlistDetailsTotalTracksCountTv.text = resources.getQuantityString(R.plurals.tracks, playlist.addedTrackIds.size, playlist.addedTrackIds.size)
            binding.playlistDetailsTotalDurationTv.text = binding.playlistDetailsTotalTracksCountTv.text
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

    companion object {
        fun newInstance(id: Int) = PlaylistDetailsFragment().apply {
            arguments = bundleOf(BUNDLE_PLAYLIST_ID_KEY to id)
        }
    }
}