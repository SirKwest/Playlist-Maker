package com.practicum.playlistmaker.sharing.data

import android.content.Context
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.MailData
import com.practicum.playlistmaker.sharing.domain.model.ShareData
import com.practicum.playlistmaker.sharing.domain.model.TermsData

class SharingImpl(private val context: Context) : SharingInteractor {
    override fun getShareData(): ShareData {
        return ShareData(context.getString(R.string.practicum_url))
    }

    override fun getTermsData(): TermsData {
        return TermsData(context.getString(R.string.user_agreement_link))
    }

    override fun getMailData(): MailData {
        return MailData(
            context.getString(R.string.developer_email),
            context.getString(R.string.email_title_placeholder),
            context.getString(R.string.email_text_placeholder)
        )
    }

}