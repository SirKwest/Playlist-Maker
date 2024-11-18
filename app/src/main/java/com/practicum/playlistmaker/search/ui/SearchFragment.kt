package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding get() = requireNotNull(_binding) {"Fragment binding must not be null"}

    private val viewModel: SearchActivityViewModel by viewModel()
    private var searchValue = ""

    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeScreenState().observe(viewLifecycleOwner) { state ->
            settingVisualElements(state)
        }

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.searchRecyclerView.requestLayout()


        binding.searchField.setOnFocusChangeListener { _, isFocused ->
            if (isFocused && binding.searchField.text.isEmpty()) {
                viewModel.getHistory()
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
            settingVisualElements(ScreenStates.ShowList(emptyList(), false))
            /* Hide keyboard after clearing input */
            val inputMethodManager =
                activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
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

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(CLICK_DELAY_MILLS)
                isClickAllowed = true
            }
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
                context?.theme?.resolveAttribute(R.attr.connectionFailedDrawable, typedValue, true)
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
                context?.theme?.resolveAttribute(R.attr.emptySearchDrawable, typedValue, true)
                binding.emptySearchImage.setImageResource(typedValue.resourceId)

                binding.clearHistoryButton.isVisible = false
                binding.searchRecyclerView.adapter = TrackListAdapter(arrayListOf())
            }
            is ScreenStates.ShowList -> {
                binding.emptySearchImage.setImageResource(0)
                binding.emptySearchText.text = null
                binding.refreshButton.isVisible = false
                binding.clearHistoryButton.isVisible = state.isHistory && state.tracks.isNotEmpty()
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
                        val playerActivityIntent = Intent(requireContext(), PlayerActivity::class.java)
                        playerActivityIntent.putExtra(PlayerActivity.SELECTED_TRACK, Gson().toJson(item))
                        startActivity(playerActivityIntent)
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
        const val CLICK_DELAY_MILLS = 1000L
    }
}
