package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel

enum class AccountLinkType {
    WEB,
    APP
}

class AccountLinkViewModel: ViewModel() {
    var token = ""
    var url = ""
    // urlから切り出したstate
    var state = ""
    lateinit var type: AccountLinkType
}