package net.mythrowaway.app.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.*
import net.mythrowaway.app.R
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.DIContainer
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.adapter.IConnectView
import net.mythrowaway.app.adapter.controller.AccountLinkControllerImpl
import net.mythrowaway.app.adapter.controller.ConnectControllerImpl
import net.mythrowaway.app.adapter.presenter.ConnectViewModel
import net.mythrowaway.app.databinding.ActivityConnectBinding
import net.mythrowaway.app.usecase.IConfigRepository

class ConnectActivity : AppCompatActivity(), IConnectView, IAccountLinkView, CoroutineScope by MainScope() {
    private val controller =
        ConnectControllerImpl(this)
    private val accountLinkController = AccountLinkControllerImpl(this)

    private var viewModel = ConnectViewModel()

    private val config =  DIContainer.resolve(IConfigRepository::class.java)!!

    private lateinit var activityConnectBinding: ActivityConnectBinding

    override fun setEnabledStatus(viewModel: ConnectViewModel) {
        activityConnectBinding.shareButton.isEnabled = viewModel.enabledShare
        activityConnectBinding.activationButton.isEnabled = viewModel.enabledActivate
        activityConnectBinding.alexaButton.isEnabled = viewModel.enabledAlexa
        this.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityConnectBinding = ActivityConnectBinding.inflate(layoutInflater)
        setContentView(activityConnectBinding.root)

        if(intent.action == Intent.ACTION_VIEW) {
            val uri = Uri.parse(intent.data.toString())
            val error = uri.getQueryParameter("error")
            if(error!=null) {
                finish()
            } else {
                val code = uri.getQueryParameter("code")
                val state = uri.getQueryParameter("state")
                val session = config.getAccountLinkSession()
                Log.d(javaClass.simpleName, "uri->${uri},session->${session}")

                config.getUserId()?.let { id ->
                    val accountLinkActivity = Intent(this, AccountLinkActivity::class.java)
                    accountLinkActivity.putExtra(
                        AccountLinkActivity.EXTRACT_URL,
                        "${getString(R.string.url_backend)}/enable_skill?code=${code}&state=${state}&id=$id&redirect_uri=${getString(
                            R.string.app_link_uri
                        )}/accountlink"
                    )
                    accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_SESSION, session)
                    val startActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        when(result.resultCode) {
                            Activity.RESULT_OK -> {
                                // レビューダイアログの表示
                                val manager = ReviewManagerFactory.create(applicationContext)
                                val request = manager.requestReviewFlow()
                                request.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val reviewInfo = task.result
                                        val flow = manager.launchReviewFlow(this, reviewInfo)
                                        flow.addOnCompleteListener {
                                            Log.d(this.javaClass.simpleName, "review complete")
                                        }
                                    } else {
                                        Log.e(this.javaClass.simpleName, "Review flow failed")
                                    }
                                }
                            }
                        }
                    }
                    startActivity.launch(accountLinkActivity)
                }
            }
        }

        activityConnectBinding.shareButton.setOnClickListener {
            val intent = Intent(this,PublishCodeActivity::class.java)
            startActivity(intent)
        }

        activityConnectBinding.activationButton.setOnClickListener {
            val intent = Intent(this, ActivateActivity::class.java)
            val startActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                when(result.resultCode) {
                    Activity.RESULT_OK -> {
                        setResult(Activity.RESULT_OK)
                    }
                }
            }
            startActivity.launch(intent)
        }

        activityConnectBinding.alexaButton.setOnClickListener {
            if(AlexaAppUtil.isAlexaAppSupportAppToApp(this)) {
                launch {
                    accountLinkController.accountLink()
                }
            } else {
                // ダイアログで表示
                PushAlexaAppDialog().show(supportFragmentManager,"pushDialog")
            }
        }
        
        controller.changeEnabledStatus()
    }


    override suspend fun startAccountLink(receiveViewModel: AccountLinkViewModel) {
        // Alexaアプリからは新しいActivityが呼ばれるためセッション情報は永続化する
        config.saveAccountLinkSession(receiveViewModel.sessionId,receiveViewModel.sessionValue)
        Log.i(javaClass.simpleName, "start account link -> ${receiveViewModel.url}")
        coroutineScope {
            launch(Dispatchers.Main) {
                val i = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "${receiveViewModel.url}&" +
                                "redirect_uri=${getString(R.string.app_link_uri)}/accountlink"
                    )
                )
                startActivity(i)
            }.join()
        }

        // Alexaスキルで同じアクティビティがスタックされるため終了する
        finish()
    }

    override suspend fun showError() {
        Log.e(javaClass.simpleName, "Cause AccountLink Error on start link")
    }
}

/**
 * Alexaアプリがインストールされていて、アプリ間アカウントリンクをサポートしているかを確認するためのユーティリティ。
 */
object AlexaAppUtil {

    private const val ALEXA_PACKAGE_NAME = "com.amazon.dee.app"

    private const val REQUIRED_MINIMUM_VERSION_CODE = 866607211

    /**
     * Alexaアプリがインストールされていて、アプリリンクをサポートしている場合
     *
     * @param context アプリケーションコンテキスト。
     */
    @JvmStatic
    fun isAlexaAppSupportAppToApp(context: Context): Boolean {
        return try {
            val packageManager: PackageManager = context.packageManager
            val packageInfo = packageManager.getPackageInfo(ALEXA_PACKAGE_NAME, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode > REQUIRED_MINIMUM_VERSION_CODE
            } else {
                packageInfo != null
            }

        } catch (e: PackageManager.NameNotFoundException) {
            // Alexaアプリがインストールされていない場合
            false
        }
    }
}
