package com.practicum.playlistmaker.library.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isEmpty
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.LibraryPlaylistCreateFragmentBinding
import com.practicum.playlistmaker.library.domain.models.Playlist
import com.practicum.playlistmaker.main.ui.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date

class PlaylistCreationFragment: Fragment() {
    private var _binding: LibraryPlaylistCreateFragmentBinding? = null
    private val binding: LibraryPlaylistCreateFragmentBinding get() = requireNotNull(_binding) {"Fragment playlist creation binding must not be null"}

    private val viewModel: PlaylistCreationFragmentViewModel by viewModel()

    private var imageInput: InputStream? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LibraryPlaylistCreateFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback(
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (
                        binding.playlistNameTe.text.toString().isEmpty()
                        && binding.playlistDescriptionTe.text.toString().isEmpty()
                        && binding.playlistCoverImage.drawable == null
                    ) {
                        requireActivity().supportFragmentManager.popBackStack()
                        return
                    }
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.finish_playlist_creation_question)
                        .setMessage(R.string.all_unsaved_data_will_be_lost)
                        .setNeutralButton(R.string.cancel) {_, _ -> }
                            .setPositiveButton(R.string.finish) {_, _ -> requireActivity().supportFragmentManager.popBackStack()}
                                .show()
                }
            }
        )

        binding.playlistCreationToolbar.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.playlistNameTe.doOnTextChanged { text, _, _, _ ->
            binding.createPlaylistBt.isEnabled = text.isNullOrEmpty().not()
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {uri ->
            if (uri != null) {
                Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.track_placeholder)
                    .centerCrop()
                    .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.big_album_cover_dp_rounded)))
                    .into(binding.playlistCoverImage)
                binding.playlistCoverImage.setImageURI(uri)
                imageInput = requireContext().contentResolver.openInputStream(uri)

            }
        }

        binding.playlistCoverImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.createPlaylistBt.setOnClickListener {
            if (imageInput != null) {
                val filePath = File(
                    requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    IMAGE_SUBDIRECTORY_NAME
                )
                if (filePath.exists().not()) {
                    filePath.mkdirs()
                }
                val fileName =
                    File(filePath, SimpleDateFormat(IMAGE_NAME_FORMAT).format(Date()))
                val outputStream = FileOutputStream(fileName)
                BitmapFactory.decodeStream(imageInput)
                    .compress(Bitmap.CompressFormat.PNG, 30, outputStream)

                viewModel.createPlaylist(
                    Playlist(
                        0,
                        binding.playlistNameTe.text.toString(),
                        binding.playlistDescriptionTe.text.toString(),
                        fileName.toString(),
                        mutableListOf()
                    )
                )
            } else {
                viewModel.createPlaylist(
                    Playlist(
                        0,
                        binding.playlistNameTe.text.toString(),
                        binding.playlistDescriptionTe.text.toString(),
                        "",
                        mutableListOf()
                    )
                )
            }
            Toast.makeText(requireContext(), "Playlist ${binding.playlistNameTe.text.toString()} created", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val panel = requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view)
        if (panel !== null) {
            panel.isVisible = false
            return
        }
        val playerLayout = requireActivity().findViewById<ConstraintLayout>(R.id.player_layout)
        if (playerLayout !== null) {
            playerLayout.isVisible = false
        }
    }

    override fun onDetach() {
        super.onDetach()
        val panel = requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view)
        if (panel !== null) {
            panel.isVisible = true
            return
        }
        val playerLayout = requireActivity().findViewById<ConstraintLayout>(R.id.player_layout)
        if (playerLayout !== null) {
            playerLayout.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val IMAGE_SUBDIRECTORY_NAME = "PlaylistImages"
        const val IMAGE_NAME_FORMAT = "dd.MM.yyyy hh:mm:ss"
        fun newInstance() = PlaylistCreationFragment()
    }
}