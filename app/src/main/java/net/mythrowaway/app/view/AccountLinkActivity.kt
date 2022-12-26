package net.mythrowaway.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.core.view.isInvisible
import net.mythrowaway.app.R
import kotlinx.coroutines.*
import net.mythrowaway.app.databinding.ActivityAccountLinkBinding

class AccountLinkActivity : AppCompatActivity(),CoroutineScope  by MainScope() {
    private val mCompleteUrlSuffix = "accountlink-complete.html"
    private lateinit var accountLinkBinding: ActivityAccountLinkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        accountLinkBinding = ActivityAccountLinkBinding.inflate(layoutInflater)
        setContentView(accountLinkBinding.root)

        val cookieManager = CookieManager.getInstance()
        val url = intent.getStringExtra(EXTRACT_URL) ?: resources.getString(R.string.url_error)
//        val session = intent.getStringExtra(EXTRACT_TOKEN)
//        Log.d(javaClass.simpleName, "use session -> $session")

        cookieManager.setAcceptCookie(true)
        cookieManager.acceptThirdPartyCookies(accountLinkBinding.accountLinkView)
//        cookieManager.setCookie(Uri.parse(getString(R.string.url_backend)).scheme+"://"+
//                Uri.parse(getString(R.string.url_backend)).host,session
//        )

        accountLinkBinding.accountLinkView.webViewClient = AccountLinkViewClient()
        accountLinkBinding.accountLinkView.clearCache(true)
        accountLinkBinding.accountLinkView.settings.javaScriptEnabled = true

        Log.d(javaClass.simpleName,"account link url->${url}")
        accountLinkBinding.accountLinkView.loadUrl(url)
   }

    inner class AccountLinkViewClient: WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            Log.d(javaClass.simpleName, "load url -> $url")
            view?.loadUrl(url ?: "")
            return true
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            accountLinkBinding.statusTextView.isInvisible = true
            Log.d(javaClass.simpleName,"onPageFinish@$url")
            if(url != null && Regex(".+/$mCompleteUrlSuffix$").find(url)?.value != null) {
                Log.d(javaClass.simpleName, "set result ok")
                setResult(RESULT_OK,null)
            }
        }
    }

    companion object {
        const val EXTRACT_URL = "EXTRACT_URL"
        const val EXTRACT_TOKEN = "EXTRACT_TOKEN"
    }
}
