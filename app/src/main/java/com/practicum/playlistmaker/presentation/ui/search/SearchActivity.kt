package com.practicum.playlistmaker.presentation.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.api.TracksConsumer
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private var searchValue = ""

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search() }

    private lateinit var searchField: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerImage: ImageView
    private lateinit var recyclerMessage: TextView
    private lateinit var refreshButton: Button

    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: Button

    private lateinit var progressBar: ProgressBar

    private val tracksInteractor: TracksInteractor = Creator.provideTracksInteractor()
    private val historyInteractor: SearchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        refreshButton = findViewById(R.id.refresh_button)
        recyclerImage = findViewById(R.id.empty_search_image)
        recyclerMessage = findViewById(R.id.empty_search_text)
        recyclerView = findViewById(R.id.search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.requestLayout()

        clearHistoryButton = findViewById(R.id.clear_history_button)
        searchHistoryTitle = findViewById(R.id.search_history_title)
        progressBar = findViewById(R.id.progress_bar)

        val backButton = findViewById<Toolbar>(R.id.search_toolbar);
        backButton.setOnClickListener { super.finish() }

        searchField = findViewById(R.id.search_field)
        searchField.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && searchField.text.isEmpty() && historyInteractor.get().isNotEmpty()) {
                settingVisualElements(ScreenStates.SHOW_HISTORY)
            } else {
                settingVisualElements(ScreenStates.DEFAULT)
            }
        }

        searchField.requestFocus();

        val clearButton = findViewById<ImageView>(R.id.cancel_button)

        val searchFieldTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.isVisible = !p0.isNullOrEmpty();
                searchValue = p0.toString();
                searchDebounce()
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
        searchField.addTextChangedListener(searchFieldTextWatcher);

        clearButton.setOnClickListener {
            searchField.setText("")
            recyclerView.adapter = TrackListAdapter(emptyList())
            recyclerImage.setImageResource(0)
            recyclerMessage.text = null
            refreshButton.visibility = View.GONE
            /* Hide keyboard after clearing input */
            val view = this.currentFocus
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            /* End */
            searchField.clearFocus(); // Hide cursor from input
        }

        clearHistoryButton.setOnClickListener {
            historyInteractor.clear()
            settingVisualElements(ScreenStates.DEFAULT)
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchDebounce()
            }
            false
        }

        refreshButton.setOnClickListener { searchDebounce() }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_DATA_TAG, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue = savedInstanceState.getString(SEARCH_FIELD_DATA_TAG, SEARCH_FIELD_DEFAULT_VALUE)
        if (searchValue.isEmpty())
            return

        searchField.setText(searchValue)
        searchField.setSelection(searchValue.length)
        /* Show keyboard after restoring instance */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        /* End */
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun search() {
        if (searchField.text.isEmpty()) {
            return
        }
        settingVisualElements(ScreenStates.REQUEST_IN_PROGRESS)
        tracksInteractor.searchTracks(
            searchField.text.toString(),
            object : TracksConsumer {
                override fun consume(foundedTracks: List<Track>) {
                    runOnUiThread {handleResponse(foundedTracks)}
                }

                override fun handleError(error: Exception) {
                    runOnUiThread { settingVisualElements(ScreenStates.CONNECTION_ISSUES) }
                }
            })
    }
    private fun handleResponse(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            settingVisualElements(ScreenStates.EMPTY_RESULTS)
            return
        }
        val tracksAdapter = TrackListAdapter(tracks)
        recyclerView.adapter = tracksAdapter
        tracksAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (!clickDebounce()) {
                    return
                }
                val item = tracksAdapter.getTrackByPosition(position)
                historyInteractor.add(item)
                val playerActivityIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                playerActivityIntent.putExtra(PlayerActivity.SELECTED_TRACK, Gson().toJson(item))
                this@SearchActivity.startActivity(playerActivityIntent)
            }
        })
        settingVisualElements(ScreenStates.DEFAULT)
    }

    private fun settingVisualElements(state: ScreenStates?) {
        val isDarkTheme = (applicationContext as App).isDarkTheme()
        when (state) {
            ScreenStates.REQUEST_IN_PROGRESS -> {
                progressBar.visibility = View.VISIBLE
                recyclerMessage.visibility = View.GONE
                refreshButton.visibility = View.GONE
                recyclerImage.setImageResource(0)
                clearHistoryButton.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
                recyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            ScreenStates.CONNECTION_ISSUES -> {
                progressBar.visibility = View.GONE
                recyclerMessage.visibility = View.VISIBLE
                recyclerMessage.text = getString(R.string.connection_failed)
                refreshButton.visibility = View.VISIBLE
                recyclerImage.setImageResource(
                    if (isDarkTheme) {
                        R.drawable.connection_failed_dark
                    } else {
                        R.drawable.connection_failed_light
                    }
                )
                clearHistoryButton.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
                recyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            ScreenStates.EMPTY_RESULTS -> {
                progressBar.visibility = View.GONE
                recyclerMessage.visibility = View.VISIBLE
                recyclerMessage.text = getString(R.string.empty_search)
                refreshButton.visibility = View.GONE
                recyclerImage.setImageResource(
                    if (isDarkTheme) {
                        R.drawable.empty_search_dark
                    } else {
                        R.drawable.empty_search_light
                    }
                )
                clearHistoryButton.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
                recyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            ScreenStates.SHOW_HISTORY -> {
                progressBar.visibility = View.GONE
                recyclerImage.setImageResource(0)
                recyclerMessage.text = null
                refreshButton.visibility = View.GONE
                clearHistoryButton.visibility = View.VISIBLE
                searchHistoryTitle.visibility = View.VISIBLE
                val historyAdapter = TrackListAdapter(historyInteractor.get())
                historyAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        if (!clickDebounce()) {
                            return
                        }
                        val item = historyAdapter.getTrackByPosition(position)
                        historyInteractor.add(item)
                        Toast.makeText(
                            this@SearchActivity,
                            "Track: " + item.artistName + " - " + item.trackName,
                            Toast.LENGTH_SHORT
                        ).show()
                        historyAdapter.notifyDataSetChanged()
                        val playerActivityIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                        playerActivityIntent.putExtra(PlayerActivity.SELECTED_TRACK, Gson().toJson(item))
                        this@SearchActivity.startActivity(playerActivityIntent)
                    }
                })
                recyclerView.adapter = historyAdapter
            }
            else -> {
                progressBar.visibility = View.GONE
                recyclerImage.setImageResource(0)
                recyclerMessage.text = null
                refreshButton.visibility = View.GONE
                clearHistoryButton.visibility = View.GONE
                searchHistoryTitle.visibility = View.GONE
            }
        }
    }

    companion object {
        const val SEARCH_FIELD_DATA_TAG = "SEARCH_TEXT"
        const val SEARCH_FIELD_DEFAULT_VALUE = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
        const val SEARCH_DEBOUNCE_DELAY = 2000L

        enum class ScreenStates {
            CONNECTION_ISSUES,
            EMPTY_RESULTS,
            SHOW_HISTORY,
            REQUEST_IN_PROGRESS,
            DEFAULT
        }
    }
}
