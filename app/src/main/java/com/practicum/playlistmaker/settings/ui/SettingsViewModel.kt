package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.SingleLiveEvent
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.MailData
import com.practicum.playlistmaker.sharing.domain.model.ShareData
import com.practicum.playlistmaker.sharing.domain.model.TermsData

class SettingsViewModel(private val sharingInteractor: SharingInteractor): ViewModel() {
    private val share = SingleLiveEvent<ShareData>()

    init {
        share.postValue(sharingInteractor.getShareData())
    }
    fun onShareClick(): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharingInteractor.getShareData().toString())
        return Intent.createChooser(shareIntent, null)
    }

    fun onSupportClick(): Intent {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(sharingInteractor.getMailData().mail))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, sharingInteractor.getMailData().subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, sharingInteractor.getMailData().text)
        return Intent.createChooser(supportIntent, null)
    }

    fun onAgreementClick(): Intent {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(sharingInteractor.getTermsData().toString())
        return Intent.createChooser(agreementIntent, null)
    }
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(Creator.provideShareInteractor())
            }
        }
    }
}