package net.mythrowaway.app.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_account_link.*
import kotlinx.coroutines.*

class AccountLinkActivity : AppCompatActivity(),CoroutineScope  by MainScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_link)

        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.acceptThirdPartyCookies(accountLinkView)
        val url = intent.getStringExtra(EXTRACT_URL)
        val session = intent.getStringExtra(EXTRACT_SESSION)

        cookieManager.setCookie(Uri.parse(url).scheme+"://"+Uri.parse(url).host,session)

        accountLinkView.webViewClient = AccountLinkViewClient()
        accountLinkView.clearCache(true)
        accountLinkView.settings.javaScriptEnabled = true

        Log.i(javaClass.simpleName,"account link url->${url}")
        accountLinkView.loadUrl(url)
   }

    inner class AccountLinkViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            statusTextView.visibility = View.INVISIBLE
        }
    }

    companion object {
        const val EXTRACT_URL = "EXTRACT_URL"
        const val EXTRACT_SESSION = "EXTRACT_SESSION"
    }
}
