package com.practicum.playlistmaker.library.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.library.domain.db.FavoritesInteractor
import kotlinx.coroutines.launch

class FavoritesFragmentViewModel(private val favoritesInteractor: FavoritesInteractor) : ViewModel() {
    private val favoriteStateLiveData = MutableLiveData<FavoriteScreenState>()

    fun observeState(): LiveData<FavoriteScreenState> = favoriteStateLiveData

    fun getDataForScreen() {
        viewModelScope.launch {
            favoritesInteractor.getAllFavorites().collect { tracks ->
                if (tracks.isEmpty()) {
                    settingVisualElements(FavoriteScreenState.EmptyScreen)
                } else {
                    settingVisualElements(FavoriteScreenState.ShowFavorites(tracks))
                }
            }
        }
    }

    private fun settingVisualElements(state: FavoriteScreenState) {
        favoriteStateLiveData.postValue(state)
    }
}
