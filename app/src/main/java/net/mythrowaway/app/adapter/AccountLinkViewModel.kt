package net.mythrowaway.app.adapter

import androidx.lifecycle.ViewModel

class AccountLinkViewModel: ViewModel() {
    var sessionId = ""
    var sessionValue = ""
    var url = ""
    // urlから切り出したstate
    var state = ""
}