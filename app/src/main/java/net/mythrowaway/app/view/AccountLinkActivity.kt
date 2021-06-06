package net.mythrowaway.app.view

import android.app.Activity
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import net.mythrowaway.app.R
import kotlinx.android.synthetic.main.activity_account_link.*
import kotlinx.coroutines.*
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.usecase.IConfigRepository

class AccountLinkActivity : AppCompatActivity(),CoroutineScope  by MainScope() {
    val complete_url_suffix = "accountlink-complete.html"
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

        Log.d(javaClass.simpleName,"account link url->${url}")
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
            Log.d(javaClass.simpleName,"onPageFinish@$url")
            if(url != null && Regex(".+/$complete_url_suffix$").find(url)?.value != null) {
                Log.d(javaClass.simpleName, "set result ok")
                setResult(Activity.RESULT_OK,null)
            }
        }
    }
    private val preference: IConfigRepository = DIContainer.resolve(IConfigRepository::class.java)!!

    companion object {
        const val EXTRACT_URL = "EXTRACT_URL"
        const val EXTRACT_SESSION = "EXTRACT_SESSION"
    }
}
