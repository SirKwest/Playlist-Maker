package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.core.content.edit
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings);
        val swDarkTheme = findViewById<SwitchMaterial>(R.id.theme_switcher);

        val currentNightMode = baseContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK);
        swDarkTheme.setChecked(currentNightMode == Configuration.UI_MODE_NIGHT_YES);

        swDarkTheme.setOnCheckedChangeListener { _, isChecked -> (applicationContext as App).switchTheme(isChecked) }

        val backButton = findViewById<Toolbar>(R.id.settings_toolbar);
        backButton.setOnClickListener { super.finish() }

        val shareButton = findViewById<FrameLayout>(R.id.share_button);
        shareButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_url))
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }

        }

        val supportButton = findViewById<FrameLayout>(R.id.support_button);
        supportButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_title_placeholder))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text_placeholder))
                startActivity(Intent.createChooser(this, null))
            }
        }

        val userAgreementButton = findViewById<FrameLayout>(R.id.user_agreement_button);
        userAgreementButton.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.user_agreement_link))
                startActivity(Intent.createChooser(this, null))
            }
        }
    }
}