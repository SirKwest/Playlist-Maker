package com.practicum.playlistmaker.library.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.LibraryPlaylistCreateFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistCreationFragment: Fragment() {
    private var _binding: LibraryPlaylistCreateFragmentBinding? = null
    private val binding: LibraryPlaylistCreateFragmentBinding get() = requireNotNull(_binding) {"Fragment playlist creation binding must not be null"}

    private val viewModel: PlaylistCreationFragmentViewModel by viewModel()

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
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view).visibility = View.GONE
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().findViewById<BottomNavigationView>(R.id.main_navigation_view).visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlaylistCreationFragment()
    }
}