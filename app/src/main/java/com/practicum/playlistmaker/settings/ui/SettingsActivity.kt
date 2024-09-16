package com.practicum.playlistmaker.settings.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private val binding: ActivitySettingsBinding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val currentNightMode = baseContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        binding.themeSwitcher.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES)
        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked -> (applicationContext as App).switchTheme(isChecked) }
        binding.settingsToolbar.setOnClickListener { super.finish() }

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]

        binding.shareButton.setOnClickListener {startActivity(viewModel.onShareClick()) }
        binding.supportButton.setOnClickListener {startActivity(viewModel.onSupportClick()) }
        binding.userAgreementButton.setOnClickListener {startActivity(viewModel.onAgreementClick()) }
    }
}