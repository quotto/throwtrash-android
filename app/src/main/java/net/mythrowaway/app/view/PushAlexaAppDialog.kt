package net.mythrowaway.app.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import net.mythrowaway.app.databinding.PushAlexaAppDialogBinding

class PushAlexaAppDialog: DialogFragment() {
    private lateinit var pushAlexaAppDialogBinding: PushAlexaAppDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        pushAlexaAppDialogBinding = PushAlexaAppDialogBinding.inflate(requireActivity().layoutInflater)
        pushAlexaAppDialogBinding.button.setOnClickListener {
            this.dismiss()
        }
        pushAlexaAppDialogBinding.imageButton3.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.amazon.dee.app")
            }
            if(GooglePlayUtil.isInstalledGooglePlay(requireActivity())) {
                intent.setPackage("com.android.vending")
            }
            startActivity(intent)
        }
        return AlertDialog.Builder(requireActivity()).setView(pushAlexaAppDialogBinding.root).create()
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
     * @param context アプリケーションコンテキスト。
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