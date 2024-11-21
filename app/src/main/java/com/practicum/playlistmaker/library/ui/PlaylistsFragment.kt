package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.LibraryPlaylistsFragmentBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private var _binding: LibraryPlaylistsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlaylistFragmentViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryPlaylistsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistBt.setOnClickListener {
            findNavController().navigate(R.id.playlistCreationFragment)
        }

        binding.playlistRv.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = PlaylistAdapter(mutableListOf())
        binding.playlistRv.adapter = adapter

        viewModel.observePlaylistState.observe(viewLifecycleOwner) { state ->
            settingVisualElements(state)
            if (state is PlaylistScreenState.ShowPlaylists) {
                val adapter = PlaylistAdapter(state.playlists)
                binding.playlistRv.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.getPlaylists()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun settingVisualElements(state: PlaylistScreenState) {
        when (state) {
            PlaylistScreenState.EmptyScreen -> {
                binding.playlistRv.isVisible = false
                binding.emptyPlaylistIv.isVisible = true
                binding.emptyPlaylistTv.isVisible = true
            }
            is PlaylistScreenState.ShowPlaylists -> {
                binding.playlistRv.isVisible = true
                binding.emptyPlaylistIv.isVisible = false
                binding.emptyPlaylistTv.isVisible = false
            }
        }
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}
