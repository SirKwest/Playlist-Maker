package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.SingleLiveEvent
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.domain.ThemeInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.MailData
import com.practicum.playlistmaker.sharing.domain.model.ShareData
import com.practicum.playlistmaker.sharing.domain.model.TermsData

class SettingsViewModel(private val sharingInteractor: SharingInteractor, private val themeInteractor: ThemeInteractor): ViewModel() {
    private val share = SingleLiveEvent<ShareData>()
    private val mail = SingleLiveEvent<MailData>()
    private val terms = SingleLiveEvent<TermsData>()

    init {
        share.postValue(sharingInteractor.getShareData())
        mail.postValue(sharingInteractor.getMailData())
        terms.postValue(sharingInteractor.getTermsData())
    }
    fun observeShare(): LiveData<ShareData> = share
    fun observeMail(): LiveData<MailData> = mail
    fun observeTerms(): LiveData<TermsData> = terms
    fun observeThemeState(): LiveData<Boolean> = MutableLiveData(themeInteractor.isDarkThemeSelected())

    fun updateTheme(isDarkTheme: Boolean) {
        themeInteractor.saveTheme(isDarkTheme)
    }


    fun onShareClick(shareData: ShareData): Intent {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareData.toString())
        return Intent.createChooser(shareIntent, null)
    }

    fun onSupportClick(mailData: MailData): Intent {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse("mailto:")
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mailData.mail))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, mailData.subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, mailData.text)
        return Intent.createChooser(supportIntent, null)
    }

    fun onAgreementClick(termsData: TermsData): Intent {
        val agreementIntent = Intent(Intent.ACTION_VIEW)
        agreementIntent.data = Uri.parse(termsData.toString())
        return Intent.createChooser(agreementIntent, null)
    }
    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(Creator.provideShareInteractor(), Creator.provideThemeInteractor())
            }
        }
    }
}