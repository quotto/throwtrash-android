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
import androidx.activity.viewModels
import kotlinx.coroutines.*
import net.mythrowaway.app.R
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.IAccountLinkView
import net.mythrowaway.app.adapter.IConnectView
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.AccountLinkControllerImpl
import net.mythrowaway.app.adapter.controller.ConnectControllerImpl
import net.mythrowaway.app.adapter.di.ConnectComponent
import net.mythrowaway.app.adapter.presenter.AccountLinkPresenterImpl
import net.mythrowaway.app.adapter.presenter.ConnectViewModel
import net.mythrowaway.app.databinding.ActivityConnectBinding
import net.mythrowaway.app.service.UsageInfoService
import net.mythrowaway.app.usecase.IAccountLinkPresenter
import net.mythrowaway.app.usecase.IConfigRepository
import net.mythrowaway.app.usecase.IConnectPresenter
import net.mythrowaway.app.viewmodel.ACCOUNT_LINK_TYPE
import javax.inject.Inject

class ConnectActivity : AppCompatActivity(), IConnectView, IAccountLinkView, CoroutineScope by MainScope() {
    @Inject lateinit var connectController: ConnectControllerImpl
    @Inject lateinit var accountLinkController: AccountLinkControllerImpl
    @Inject lateinit var connectPresenter: IConnectPresenter
    @Inject lateinit var accountLinkPresenter: IAccountLinkPresenter
    @Inject lateinit var config: IConfigRepository
    @Inject lateinit var usageInfoService: UsageInfoService

    private lateinit var connectComponent: ConnectComponent
    private var viewModel = ConnectViewModel()
    private val accountLinkViewModel: AccountLinkViewModel by viewModels()

    private lateinit var activityConnectBinding: ActivityConnectBinding
    private val activateActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> {
                setResult(Activity.RESULT_OK)
            }
        }
    }

    private val accountActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> {
                if(!usageInfoService.isReviewed()) {
                    usageInfoService.showReviewDialog(this)
                }
            }
        }
    }

    override fun setEnabledStatus(viewModel: ConnectViewModel) {
        activityConnectBinding.shareButton.isEnabled = viewModel.enabledShare
        activityConnectBinding.activationButton.isEnabled = viewModel.enabledActivate
        activityConnectBinding.alexaButton.isEnabled = viewModel.enabledAlexa
        this.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        connectComponent = (application as MyThrowTrash).appComponent.connectComponent().create()
        connectComponent.inject(this)

        super.onCreate(savedInstanceState)
        connectPresenter.setView(this)
        accountLinkPresenter.setView(this)
        (accountLinkPresenter as AccountLinkPresenterImpl).setViewModel(this.accountLinkViewModel)

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
                        "${getString(R.string.url_backend)}/enable_skill?code=${code}&state=${state}&id=$id"
                    )
                    accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_SESSION, session)
                    accountActivityLauncher.launch(accountLinkActivity)
                }
            }
        }

        activityConnectBinding.shareButton.setOnClickListener {
            val intent = Intent(this,PublishCodeActivity::class.java)
            startActivity(intent)
        }

        activityConnectBinding.activationButton.setOnClickListener {
            val intent = Intent(this, ActivateActivity::class.java)
            activateActivityLauncher.launch(intent)
        }

        activityConnectBinding.alexaButton.setOnClickListener {
            this.accountLinkViewModel.type = ACCOUNT_LINK_TYPE.APP
            if(AlexaAppUtil.isAlexaAppSupportAppToApp(this)) {
                launch {
                    accountLinkController.accountLinkWithApp()
                }
            } else {
                // ダイアログで表示
                PushAlexaAppDialog().show(supportFragmentManager,"pushDialog")
            }
        }

        activityConnectBinding.buttonStartLWA.setOnClickListener {
            this.accountLinkViewModel.type = ACCOUNT_LINK_TYPE.WEB
            launch {
                accountLinkController.accountLinkWithLWA()
            }
        }
        
        connectController.changeEnabledStatus()
    }

    /*
    アレクサアプリによるアカウントリンク
     */
    override suspend fun startAccountLinkWithAlexaApp() {
        // Alexaアプリからは新しいActivityが呼ばれるためセッション情報は永続化する
        config.saveAccountLinkSession(
            this.accountLinkViewModel.sessionId,this.accountLinkViewModel.sessionValue
        )
        Log.d(javaClass.simpleName, "start account link -> ${this.accountLinkViewModel.url}")
        coroutineScope {
            launch(Dispatchers.Main) {
                val i = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(accountLinkViewModel.url)
                )
                startActivity(i)
            }.join()
        }

        // Alexaスキルで同じアクティビティがスタックされるため終了する
        finish()
    }

    /**
    LoginWithAmazonによるアカウントリンク
     */
    override fun startAccountLinkWithLWA() {
        Log.i(javaClass.simpleName, "start account link -> ${this.accountLinkViewModel.url}")

        val accountLinkActivity = Intent(this, AccountLinkActivity::class.java)
        accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_URL,this.accountLinkViewModel.url)
        accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_SESSION,
            "${this.accountLinkViewModel.sessionId}=${this.accountLinkViewModel.sessionValue}")
        accountActivityLauncher.launch(accountLinkActivity)
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
