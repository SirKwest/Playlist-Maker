package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings);

        val backButton = findViewById<ImageView>(R.id.arrow_back);
        backButton.setOnClickListener { super.finish() }

        val shareButton = findViewById<FrameLayout>(R.id.share_button);
        shareButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_url))
                type = "text/plain"
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }

        val supportButton = findViewById<FrameLayout>(R.id.support_button);
        supportButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.developer_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.email_title_placeholder))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.email_text_placeholder))
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }

        val userAgreementButton = findViewById<FrameLayout>(R.id.user_agreement_button);
        userAgreementButton.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.user_agreement_link))
            }
            startActivity(Intent.createChooser(sendIntent, null))
        }
    }
}