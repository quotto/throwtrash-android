package net.mythrowaway.app.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import net.mythrowaway.app.R
import net.mythrowaway.app.databinding.ActivityInquiryBinding

class InquiryActivity : AppCompatActivity() {
    private lateinit var activityInquiryBinding: ActivityInquiryBinding

    val mFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLScQiZNzcYKgto1mQYAmxmo49RTuAnvtmkk3BQ02MsVlE4OmHg/viewform"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityInquiryBinding = ActivityInquiryBinding.inflate(layoutInflater)
        setContentView(activityInquiryBinding.root)

        activityInquiryBinding.inquiryWebView.clearCache(true)
        activityInquiryBinding.inquiryWebView.settings.javaScriptEnabled = true
        activityInquiryBinding.inquiryWebView.loadUrl(mFormUrl)

        activityInquiryBinding.closeButton.setOnClickListener {
            this.finish()
        }

        // https://developer.android.com/training/appbar/actions?hl=ja
        // ではsetSupportActionBarとonOptionsItemSelectedを使うが、
        // setSupportActionBarを実行するとメニューが表示されないためWidgetを直接指定する。
        activityInquiryBinding.inquiryToolbar.setOnMenuItemClickListener {item ->
            Log.d(this.javaClass.simpleName,"$item.itemId")
            when(item.itemId) {
                R.id.action_open_chrome -> {
                    // GoogleChromeで現在のページを開く
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mFormUrl))
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