package com.example.mythrowtrash.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.mythrowtrash.R
import com.example.mythrowtrash.adapter.APIAdapter
import com.example.mythrowtrash.adapter.DIContainer
import com.example.mythrowtrash.usecase.IConfigRepository
import kotlinx.android.synthetic.main.activity_account_link.*
import kotlinx.coroutines.*

class AccountLinkActivity : AppCompatActivity(),CoroutineScope by MainScope() {
    private val apiAdapter = APIAdapter()
    private val preference:IConfigRepository = DIContainer.resolve(IConfigRepository::class.java)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_link)

        accountLinkView.webViewClient = AccountLinkViewClient()
        accountLinkView.clearCache(true)
        preference.getUserId()?.let { id ->
            accountLinkView.loadUrl("https://backend.mythrowaway.net/dev/start_link?platform=android&id=$id")
        }
    }

    inner class AccountLinkViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }
    }
}   
