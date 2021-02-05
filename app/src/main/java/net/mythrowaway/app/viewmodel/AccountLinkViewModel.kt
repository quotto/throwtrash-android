package net.mythrowaway.app.viewmodel

import androidx.lifecycle.ViewModel

class AccountLinkViewModel: ViewModel() {
    var sessionId = ""
    var sessionValue = ""
    var url = ""
    // urlから切り出したstate
    var state = ""
}