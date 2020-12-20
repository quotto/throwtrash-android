package net.mythrowaway.app.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.push_alexa_app_dialog.view.*
import net.mythrowaway.app.R

class PushAlexaAppDialog: DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = activity!!.layoutInflater.inflate(R.layout.push_alexa_app_dialog,null)
        dialogView.button.setOnClickListener {
            this.dismiss()
        }
        dialogView.imageButton3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.amazon.dee.app")
            }
            if(GooglePlayUtil.isInstalledGooglePlay(activity!!)) {
                intent.setPackage("com.android.vending")
            }
            startActivity(intent)
        }
        return AlertDialog.Builder(activity!!).setView(dialogView).create()
    }
}

/**
 * GooglePlayアプリがインストールされていて、アプリ間アカウントリンクをサポートしているかを確認するためのユーティリティ。
 */
object GooglePlayUtil {

    private const val PACKAGE_NAME = "com.amazon.dee.app"

    /**
     * GooglePlayアプリがインストールされていて、アプリリンクをサポートしている場合
     *
     * @param contextアプリケーションコンテキスト。
     */
    @JvmStatic
    fun isInstalledGooglePlay(context: Context): Boolean {
        return try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(PACKAGE_NAME, 0)

            packageInfo != null

        } catch (e: PackageManager.NameNotFoundException) {
            // GooglePlayアプリがインストールされていない場合
            false
        }
    }
}