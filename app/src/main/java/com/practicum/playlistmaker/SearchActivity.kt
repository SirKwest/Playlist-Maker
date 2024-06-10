package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible

class SearchActivity : AppCompatActivity() {
    private var searchValue = ""

    private lateinit var searchField : EditText

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