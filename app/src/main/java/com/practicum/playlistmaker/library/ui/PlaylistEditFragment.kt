package com.practicum.playlistmaker.library.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.library.domain.models.Playlist
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment : PlaylistCreationFragment() {

    private val childViewModel: PlaylistEditFragmentViewModel by viewModel()

    private var playlistInfo: Playlist? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistCreationToolbar.title = requireActivity().resources.getString(R.string.edit)
        binding.createPlaylistBt.text = requireActivity().resources.getString(R.string.save)

        binding.playlistCreationToolbar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        childViewModel.getPlaylistById(requireArguments().getInt(PlaylistsFragment.BUNDLE_PLAYLIST_ID_KEY))

        childViewModel.observePlaylistState().observe(viewLifecycleOwner) { playlist ->
            playlistInfo = playlist
            Glide.with(this)
                .load(playlist.imagePath)
                .centerCrop()
                .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
                .into(binding.playlistCoverImage)
            binding.playlistCoverImage.setImageDrawable(null)
            binding.playlistNameTe.setText(playlist.name)
            binding.playlistDescriptionTe.setText(playlist.description)
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
                    .into(binding.playlistCoverImage)
                binding.playlistCoverImage.setImageURI(uri)
                childViewModel.setCoverImageUri(uri)
            }
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistBt.setOnClickListener {
            if (playlistInfo !== null) {
                childViewModel.updatePlaylist(
                    playlistInfo!!.id,
                    binding.playlistNameTe.text.toString(),
                    binding.playlistDescriptionTe.text.toString(),
                    binding.playlistCoverImage.drawable.toBitmap()
                )
            }
            requireActivity().supportFragmentManager.popBackStack()
        }

    }
    companion object {
        fun newInstance(id: Int) = PlaylistEditFragment().apply {
            arguments = bundleOf(PlaylistsFragment.BUNDLE_PLAYLIST_ID_KEY to id)
        }
    }
}