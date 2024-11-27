package com.practicum.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.LibraryPlaylistsFragmentBinding
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

        binding.playlistRv.layoutManager = GridLayoutManager(requireContext(), GRID_LAYOUT_COLUMNS_AMOUNT)
        viewModel.getPlaylists()

        viewModel.observePlaylistState.observe(viewLifecycleOwner) { state ->
            settingVisualElements(state)
            if (state is PlaylistsScreenState.ShowPlaylists) {
                val adapter = PlaylistsAdapter(state.playlists)
                adapter.setOnItemClickListener(object : PlaylistsAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val item = adapter.getItemByPosition(position)
                        findNavController().navigate(R.id.playlistDetailsFragment, Bundle().apply { putInt(
                            BUNDLE_PLAYLIST_ID_KEY, item.id) })
                    }
                })
                binding.playlistRv.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun settingVisualElements(state: PlaylistsScreenState) {
        when (state) {
            PlaylistsScreenState.EmptyScreen -> {
                binding.playlistRv.isVisible = false
                binding.emptyPlaylistIv.isVisible = true
                binding.emptyPlaylistTv.isVisible = true
            }
            is PlaylistsScreenState.ShowPlaylists -> {
                binding.playlistRv.isVisible = true
                binding.emptyPlaylistIv.isVisible = false
                binding.emptyPlaylistTv.isVisible = false
            }
        }
    }

    companion object {
        const val GRID_LAYOUT_COLUMNS_AMOUNT = 2
        const val BUNDLE_PLAYLIST_ID_KEY = "PLAYLIST_ID_KEY"
        fun newInstance() = PlaylistsFragment()
    }
}
