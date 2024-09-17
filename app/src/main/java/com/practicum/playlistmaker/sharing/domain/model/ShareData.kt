package com.practicum.playlistmaker.sharing.domain.model

data class ShareData(val url: String) {
    override fun toString(): String {
        return url
    }
}
