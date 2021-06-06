package net.mythrowaway.app.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_inquiry.*
import net.mythrowaway.app.R

class InquiryActivity : AppCompatActivity() {
    val form_url = "https://docs.google.com/forms/d/e/1FAIpQLScQiZNzcYKgto1mQYAmxmo49RTuAnvtmkk3BQ02MsVlE4OmHg/viewform"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inquiry)
        inquiryWebView.clearCache(true)
        inquiryWebView.settings.javaScriptEnabled = true
        inquiryWebView.loadUrl(form_url)

        closeButton.setOnClickListener {
            this.finish()
        }

        // https://developer.android.com/training/appbar/actions?hl=ja
        // ではsetSupportActionBarとonOptionsItemSelectedを使うが、
        // setSupportActionBarを実行するとメニューが表示されないためWidgetを直接指定する。
        inquiry_toolbar.setOnMenuItemClickListener {item ->
            Log.d(this.javaClass.simpleName,"$item.itemId")
            when(item.itemId) {
                R.id.action_open_chrome -> {
                    // GoogleChromeで現在のページを開く
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(form_url))
                    intent.setPackage("com.android.chrome")
                    startActivity(intent)
                } else -> {
                    Log.w(this.javaClass.simpleName, "no match item")
                }

            }
            true
        }
    }
}