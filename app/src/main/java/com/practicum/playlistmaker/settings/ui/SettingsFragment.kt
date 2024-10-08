package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding get() = requireNotNull(_binding) { "Fragment binding must not be null" }

    private val viewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.switchTheme(isChecked)
            viewModel.updateTheme(isChecked)
        }
        binding.themeSwitcher.setChecked(viewModel.observeThemeState().value!!)

        binding.shareButton.setOnClickListener {
            viewModel.observeShare().observe(viewLifecycleOwner) {
                startActivity(viewModel.onShareClick(it))
            }
        }
        binding.supportButton.setOnClickListener {
            viewModel.observeMail().observe(viewLifecycleOwner) {
                startActivity(viewModel.onSupportClick(it))
            }
        }
        binding.userAgreementButton.setOnClickListener {
            viewModel.observeTerms().observe(viewLifecycleOwner) {
                startActivity(viewModel.onAgreementClick(it))
            }
        }
    }

}