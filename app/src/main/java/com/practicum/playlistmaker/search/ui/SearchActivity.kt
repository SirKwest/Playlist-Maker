package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private val binding: ActivitySearchBinding by lazy { ActivitySearchBinding.inflate(layoutInflater) }
    private lateinit var viewModel: SearchActivityViewModel
    private var searchValue = ""

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel = ViewModelProvider(
            this,
            SearchActivityViewModel.getViewModelFactory()
        )[SearchActivityViewModel::class.java]

        viewModel.observeScreenState().observe(this) { state ->
            settingVisualElements(state)
        }

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclerView.requestLayout()

        binding.searchToolbar.setOnClickListener { super.finish() }

        binding.searchField.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && binding.searchField.text.isEmpty() && viewModel.getHistory().isNotEmpty()) {
                settingVisualElements(ScreenStates.ShowList(viewModel.getHistory(), true))
            } else {
                settingVisualElements(ScreenStates.Default)
            }
        }

        binding.searchField.requestFocus()

        val searchFieldTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.cancelButton.isVisible = !p0.isNullOrEmpty();
                searchValue = p0.toString();
                viewModel.searchDebounce(searchValue)
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        }
        binding.searchField.addTextChangedListener(searchFieldTextWatcher);

        binding.cancelButton.setOnClickListener {
            binding.searchField.setText("")
            binding.searchRecyclerView.adapter = TrackListAdapter(emptyList())
            binding.emptySearchImage.setImageResource(0)
            binding.emptySearchText.text = null
            binding.refreshButton.isVisible = false
            /* Hide keyboard after clearing input */
            val view = this.currentFocus
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
            /* End */
            binding.searchField.clearFocus(); // Hide cursor from input
        }

        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            settingVisualElements(ScreenStates.Default)
        }

        binding.searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchDebounce(searchValue)
            }
            false
        }

        binding.refreshButton.setOnClickListener { viewModel.searchDebounce(searchValue) }
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

        binding.searchField.setText(searchValue)
        binding.searchField.setSelection(searchValue.length)
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

    private fun settingVisualElements(state: ScreenStates?) {
        binding.progressBar.isVisible = state is ScreenStates.RequestInProgress
        when (state) {
            ScreenStates.RequestInProgress -> {
                binding.emptySearchText.isVisible = false
                binding.refreshButton.isVisible = false
                binding.emptySearchImage.setImageResource(0)
                binding.clearHistoryButton.isVisible = false
                binding.searchHistoryTitle.isVisible = false
                binding.searchRecyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            ScreenStates.ConnectionIssues -> {
                binding.emptySearchText.isVisible = true
                binding.emptySearchText.text = getString(R.string.connection_failed)



                val typedValue = TypedValue()
                theme.resolveAttribute(R.attr.connectionFailedDrawable, typedValue, true)
                binding.emptySearchImage.setImageResource(typedValue.resourceId)

                binding.clearHistoryButton.isVisible = false
                binding.refreshButton.isVisible = true
                binding.searchRecyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            ScreenStates.EmptyResults -> {
                binding.emptySearchText.isVisible = true
                binding.emptySearchText.text = getString(R.string.empty_search)
                binding.refreshButton.isVisible = false
                binding.emptySearchImage.isVisible = true

                val typedValue = TypedValue()
                theme.resolveAttribute(R.attr.emptySearchDrawable, typedValue, true)
                binding.emptySearchImage.setImageResource(typedValue.resourceId)

                binding.clearHistoryButton.isVisible = false
                binding.searchRecyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            is ScreenStates.ShowList -> {
                binding.emptySearchImage.setImageResource(0)
                binding.emptySearchText.text = null
                binding.refreshButton.isVisible = false
                binding.clearHistoryButton.isVisible = true
                binding.searchHistoryTitle.isVisible = state.isHistory
                val historyAdapter = TrackListAdapter(state.tracks)
                historyAdapter.setOnItemClickListener(object :
                    TrackListAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        if (!clickDebounce()) {
                            return
                        }
                        val item = historyAdapter.getTrackByPosition(position)
                        viewModel.addToHistory(item)
                        historyAdapter.notifyDataSetChanged()
                        val playerActivityIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                        playerActivityIntent.putExtra(PlayerActivity.SELECTED_TRACK, Gson().toJson(item))
                        this@SearchActivity.startActivity(playerActivityIntent)
                    }
                })
                binding.searchRecyclerView.adapter = historyAdapter
            }
            else -> {
                binding.emptySearchImage.setImageResource(0)
                binding.emptySearchText.text = null
                binding.refreshButton.isVisible = false
                binding.clearHistoryButton.isVisible = false
                binding.searchHistoryTitle.isVisible = false
            }
        }
    }

    companion object {
        const val SEARCH_FIELD_DATA_TAG = "SEARCH_TEXT"
        const val SEARCH_FIELD_DEFAULT_VALUE = ""
        const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}
