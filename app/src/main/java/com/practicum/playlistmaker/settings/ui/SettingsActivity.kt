package com.practicum.playlistmaker.settings.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.settingsToolbar.setOnClickListener { super.finish() }

        binding.themeSwitcher.setChecked(viewModel.observeThemeState().value!!)
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            (applicationContext as App).switchTheme(isChecked)
            viewModel.updateTheme(isChecked)
        }

        binding.shareButton.setOnClickListener {
            viewModel.observeShare().observe(this) {
                startActivity(viewModel.onShareClick(it))
            }
        }
        binding.supportButton.setOnClickListener {
            viewModel.observeMail().observe(this) {
                startActivity(viewModel.onSupportClick(it))
            }
        }
        binding.userAgreementButton.setOnClickListener {
            viewModel.observeTerms().observe(this) {
                startActivity(viewModel.onAgreementClick(it))
            }
        }
    }
}
