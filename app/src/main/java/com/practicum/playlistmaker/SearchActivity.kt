package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.api.ItunesApiClient
import com.practicum.playlistmaker.api.ItunesResponse
import com.practicum.playlistmaker.track.TrackListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var searchValue = ""

    private lateinit var searchField: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerImage: ImageView
    private lateinit var recyclerMessage: TextView
    private lateinit var refreshButton: Button

    private lateinit var searchHistoryTitle: TextView
    private lateinit var clearHistoryButton: Button

    private val apiClient: ItunesApiClient = ItunesApiClient()
    private lateinit var searchHistory: SearchHistory

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
        searchHistory = SearchHistory((applicationContext as App).getSearchPreferences())

        val backButton = findViewById<Toolbar>(R.id.search_toolbar);
        backButton.setOnClickListener { super.finish() }

        searchField = findViewById(R.id.search_field)
        searchField.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && searchField.text.isEmpty() && searchHistory.get().isNotEmpty()) {
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
            searchHistory.clear()
            settingVisualElements(ScreenStates.DEFAULT)
        }

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
            }
            false
        }

        refreshButton.setOnClickListener { search() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_FIELD_DATA_TAG, searchValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchValue =
            savedInstanceState.getString(SEARCH_FIELD_DATA_TAG, SEARCH_FIELD_DEFAULT_VALUE)
        if (searchValue.isEmpty())
            return

        searchField.setText(searchValue)
        searchField.setSelection(searchValue.length)
        /* Show keyboard after restoring instance */
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        /* End */
    }

    private fun search() {
        val tracks = apiClient.getTrackList(searchField.text.toString())
        tracks.enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(
                call: Call<ItunesResponse>,
                response: Response<ItunesResponse>
            ) {
                if (!response.isSuccessful) {
                    settingVisualElements(ScreenStates.CONNECTION_ISSUES)
                    return
                }
                if (response.body() == null || response.body()!!.results.isNullOrEmpty() == true) {
                    settingVisualElements(ScreenStates.EMPTY_RESULTS)
                    return
                }
                val tracksAdapter = TrackListAdapter(response.body()!!.results)
                recyclerView.adapter = tracksAdapter
                tracksAdapter.setOnItemClickListener(object : TrackListAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val item = tracksAdapter.getTrackByPosition(position)
                        searchHistory.add(item)
                        Toast.makeText(
                            this@SearchActivity,
                            "Track: " + item.artistName + " - " + item.trackName,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                settingVisualElements(ScreenStates.DEFAULT)
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                settingVisualElements(ScreenStates.CONNECTION_ISSUES)
            }

        })
    }

    private fun settingVisualElements(state: ScreenStates?) {
        val isDarkTheme = (applicationContext as App).isDarkTheme()
        when (state) {
            ScreenStates.CONNECTION_ISSUES -> {
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
                recyclerImage.setImageResource(0)
                recyclerMessage.text = null
                refreshButton.visibility = View.GONE
                clearHistoryButton.visibility = View.VISIBLE
                searchHistoryTitle.visibility = View.VISIBLE
                recyclerView.adapter = TrackListAdapter(searchHistory.get())
            }
            else -> {
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
        enum class ScreenStates {
            CONNECTION_ISSUES,
            EMPTY_RESULTS,
            SHOW_HISTORY,
            DEFAULT
        }
    }
}
