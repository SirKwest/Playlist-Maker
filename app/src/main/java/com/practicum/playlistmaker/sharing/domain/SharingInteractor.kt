package com.practicum.playlistmaker.sharing.domain

import com.practicum.playlistmaker.sharing.domain.model.MailData
import com.practicum.playlistmaker.sharing.domain.model.ShareData
import com.practicum.playlistmaker.sharing.domain.model.TermsData

interface SharingInteractor {

    fun getShareData(): ShareData
    fun getTermsData(): TermsData
    fun getMailData(): MailData
}