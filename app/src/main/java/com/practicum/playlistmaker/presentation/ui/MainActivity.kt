package com.practicum.playlistmaker.presentation.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.search.SearchActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settingsButton = findViewById<Button>(R.id.settings);
        settingsButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SettingsActivity::class.java
                )
            )
        }

        val libraryButtonListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, LibraryActivity::class.java))
            }
        }
        val libraryButton = findViewById<Button>(R.id.library);
        libraryButton.setOnClickListener(libraryButtonListener);

        val searchButton = findViewById<Button>(R.id.search);
        searchButton.setOnClickListener { startActivity(Intent(this@MainActivity, SearchActivity::class.java)) }
    }
}
