package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel

enum class ACCOUNT_LINK_TYPE {
    WEB,
    APP
}

class AccountLinkViewModel: ViewModel() {
    var sessionId = ""
    var sessionValue = ""
    var url = ""
    // urlから切り出したstate
    var state = ""
    lateinit var type: ACCOUNT_LINK_TYPE
}