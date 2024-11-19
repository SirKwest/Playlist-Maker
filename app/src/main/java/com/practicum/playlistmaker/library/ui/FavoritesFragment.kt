package com.practicum.playlistmaker.library.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.practicum.playlistmaker.databinding.LibraryFavoritesFragmentBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.ui.TrackListAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {
    private var _binding: LibraryFavoritesFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoritesFragmentViewModel by viewModel()
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryFavoritesFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            settingVisualElements(state)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDataForScreen()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun settingVisualElements(state: FavoriteScreenState) {
        when (state) {
            FavoriteScreenState.EmptyScreen -> {
                binding.favoritePlaceholderImage.isVisible = true
                binding.favoritePlaceholderText.isVisible = true
                binding.favoriteRecyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            is FavoriteScreenState.ShowFavorites -> {
                binding.favoritePlaceholderImage.isVisible = false
                binding.favoritePlaceholderText.isVisible = false
                binding.favoriteRecyclerView.isVisible = true
                val historyAdapter = TrackListAdapter(state.favorites)
                historyAdapter.setOnItemClickListener(object :
                    TrackListAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        if (!clickDebounce()) {
                            return
                        }
                        val item = historyAdapter.getTrackByPosition(position)
                        val playerActivityIntent = Intent(requireContext(), PlayerActivity::class.java)
                        playerActivityIntent.putExtra(PlayerActivity.SELECTED_TRACK, Gson().toJson(item))
                        startActivity(playerActivityIntent)
                    }
                })
                binding.favoriteRecyclerView.adapter = historyAdapter
            }
        }
    }
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DELAY_MILLS)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        fun newInstance() = FavoritesFragment()

        const val CLICK_DELAY_MILLS = 1000L
    }
}
