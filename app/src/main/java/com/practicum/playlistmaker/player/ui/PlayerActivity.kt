package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.getViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.library.ui.PlaylistCreationFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity: AppCompatActivity() {
    private lateinit var viewModel: PlayerViewModel

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private val binding: ActivityPlayerBinding by lazy { ActivityPlayerBinding.inflate(layoutInflater) }
    private var playlistAdapter = BottomSheetPlaylistsAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val trackJsonExtra = intent.getStringExtra(SELECTED_TRACK)
        val trackInfo = Gson().fromJson(trackJsonExtra, Track::class.java)


        viewModel = getViewModel {
            parametersOf(trackInfo.previewUrl)
        }

        viewModel.getPlaylistsForBottomSheet()

        viewModel.observeBottomSheetState().observe(this) {state ->
            if (state is BottomSheetPlaylistsState.ShowPlaylists) {
                playlistAdapter = BottomSheetPlaylistsAdapter(state.playlists)
                playlistAdapter.setOnItemClickListener(object : BottomSheetPlaylistsAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val item = playlistAdapter.getItemByPosition(position)
                        Toast.makeText(baseContext, "Добавлено в плейлист ${item.name}", Toast.LENGTH_SHORT).show()
                        playlistAdapter.notifyDataSetChanged()
                    }
                })
                binding.playlistBottomSheetRv.adapter = playlistAdapter
                playlistAdapter.notifyDataSetChanged()
            }
        }

        viewModel.observePlayingState().observe(this) { state ->
            viewModel.postActualPlayerState()
            when (state) {
                PlayerInteractor.Companion.PlayerState.STARTED -> binding.playButton.setImageResource(R.drawable.ic_pause)
                PlayerInteractor.Companion.PlayerState.COMPLETED -> {
                    binding.playbackTimer.text = dateFormat.format(0)
                    binding.playButton.setImageResource(R.drawable.ic_play)
                }
                else -> binding.playButton.setImageResource(R.drawable.ic_play)
            }
        }

        viewModel.observePositionState().observe(this) {
            binding.playbackTimer.text = dateFormat.format(it)
        }

        viewModel.observeFavoriteState().observe(this) {
            if (it) {
                binding.favoriteButton.setImageResource(R.drawable.favorite)
                trackInfo.isFavorite = true
            } else {
                binding.favoriteButton.setImageResource(R.drawable.not_favorite)
                trackInfo.isFavorite = false
            }
        }

        binding.favoriteButton.setOnClickListener {
            viewModel.changeFavoriteStatus(trackInfo)
        }
        BottomSheetBehavior.from(binding.playerBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
        binding.addToButton.setOnClickListener {
            viewModel.getPlaylistsForBottomSheet()
            playlistAdapter.notifyDataSetChanged()
            binding.playerBottomSheet.isVisible = true
            BottomSheetBehavior.from(binding.playerBottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
        }


        if (trackInfo.isFavorite) {
            binding.favoriteButton.setImageResource(R.drawable.favorite)
        } else {
            binding.favoriteButton.setImageResource(R.drawable.not_favorite)
        }

        binding.playerToolbar.setOnClickListener { super.finish() }

        Glide.with(this)
            .load(trackInfo.getBigCover())
            .placeholder(R.drawable.track_placeholder)
            .centerCrop()
            .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
            .into(binding.bigAlbumCover)

        binding.trackName.text = trackInfo.trackName
        binding.artistName.text = trackInfo.artistName

        if (trackInfo.trackTimeMillis.isNullOrEmpty()) {
            binding.trackTimeDescription.visibility = View.GONE
        } else {
            binding.trackTimeValue.text = trackInfo.getTrackTimeFormatted("mm:ss")
        }

        if (trackInfo.collectionName.isNullOrEmpty()) {
            binding.albumDescription.visibility = View.GONE
        } else {
            binding.albumValue.text = trackInfo.collectionName
        }

        if (trackInfo.releaseDate.isNullOrEmpty()) {
            binding.releaseYearDescription.visibility = View.GONE
        } else {
            binding.releaseYearValue.text = trackInfo.getReleaseYear()
        }

        if (trackInfo.primaryGenreName.isNullOrEmpty()) {
            binding.genreDescription.visibility = View.GONE
        } else {
            binding.genreValue.text = trackInfo.primaryGenreName
        }

        if (trackInfo.country.isNullOrEmpty()) {
            binding.countryDescription.visibility = View.GONE
        } else {
            binding.countryValue.text = trackInfo.country
        }

        binding.playButton.setOnClickListener {
            viewModel.changePlayerState()
        }

        binding.createPlaylistBt.setOnClickListener {
            supportFragmentManager.beginTransaction().replace(R.id.container_fragment, PlaylistCreationFragment()).addToBackStack(null).commit()
            BottomSheetBehavior.from(binding.playerBottomSheet).state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylistsForBottomSheet()
        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        const val SELECTED_TRACK = "selected_track"
    }
}
