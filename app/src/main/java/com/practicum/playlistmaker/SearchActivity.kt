package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.api.ItunesApiClient
import com.practicum.playlistmaker.api.ItunesApiService
import com.practicum.playlistmaker.api.ItunesResponse
import com.practicum.playlistmaker.track.Track
import com.practicum.playlistmaker.track.TrackListAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class SearchActivity : AppCompatActivity() {
    private var searchValue = ""

    private lateinit var searchField : EditText
    private lateinit var recyclerView: RecyclerView
    private val apiClient: ItunesApiClient = ItunesApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val backButton = findViewById<Toolbar>(R.id.search_toolbar);
        backButton.setOnClickListener { super.finish() }

        searchField = findViewById(R.id.search_field)
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
            searchField.setText("");
            /* Hide keyboard after clearing input */
            val view = this.currentFocus
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(view?.windowToken,0)
            /* End */
            searchField.clearFocus(); // Hide cursor from input
        }

        recyclerView = findViewById(R.id.search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        searchField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val tracks = apiClient.getTrackList(searchField.text.toString())
                tracks.enqueue(object : Callback<ItunesResponse> {
                    override fun onResponse(
                        call: Call<ItunesResponse>,
                        response: Response<ItunesResponse>
                    ) {
                        if (response.isSuccessful) {
                            recyclerView.adapter = TrackListAdapter(response.body()!!.results)
                        } else {
                            val errorJson = response.errorBody()?.string()
                        }
                    }

                    override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                        t.printStackTrace()
                    }

                })
            }
            false
        }
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

    companion object {
        const val SEARCH_FIELD_DATA_TAG = "SEARCH_TEXT"
        const val SEARCH_FIELD_DEFAULT_VALUE = ""
    }
}