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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import kotlinx.coroutines.*
import net.mythrowaway.app.R
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.AccountLinkViewInterface
import net.mythrowaway.app.adapter.ConnectViewInterface
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.AccountLinkControllerImpl
import net.mythrowaway.app.adapter.controller.ConnectControllerImpl
import net.mythrowaway.app.adapter.di.ConnectComponent
import net.mythrowaway.app.adapter.presenter.AccountLinkPresenterImpl
import net.mythrowaway.app.databinding.ActivityConnectBinding
import net.mythrowaway.app.service.UsageInfoService
import net.mythrowaway.app.usecase.AccountLinkPresenterInterface
import net.mythrowaway.app.usecase.ConfigRepositoryInterface
import net.mythrowaway.app.usecase.ConnectPresenterInterface
import net.mythrowaway.app.viewmodel.AccountLinkType
import net.mythrowaway.app.viewmodel.ConnectViewModel
import javax.inject.Inject

class ConnectActivity : AppCompatActivity(), ConnectViewInterface, AccountLinkViewInterface, CoroutineScope by MainScope() {
    @Inject lateinit var connectController: ConnectControllerImpl
    @Inject lateinit var accountLinkController: AccountLinkControllerImpl
    @Inject lateinit var connectPresenter: ConnectPresenterInterface
    @Inject lateinit var accountLinkPresenter: AccountLinkPresenterInterface
    @Inject lateinit var config: ConfigRepositoryInterface
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
                Log.d(javaClass.simpleName, "receive uri from alexa app ->${uri}")
                val code = uri.getQueryParameter("code")
                val token = config.getAccountLinkToken()
                val redirectUri = config.getAccountLinkUrl()
                Log.d(javaClass.simpleName, "redirect_uri->${redirectUri},token->${token}")

                config.getUserId()?.let { _ ->
                    val accountLinkActivity = Intent(this, AccountLinkActivity::class.java)
                    accountLinkActivity.putExtra(
                        AccountLinkActivity.EXTRACT_URL,
                        "${getString(R.string.url_api)}/enable_skill?code=$code&token=$token&redirect_uri=$redirectUri"
                    )
                    accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_TOKEN, token)
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
            this.accountLinkViewModel.type = AccountLinkType.APP
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
            this.accountLinkViewModel.type = AccountLinkType.WEB
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
        val redirectUriPattern = Regex("^https://.+&redirect_uri=(https://[^&]+)")
        redirectUriPattern.matchEntire(this.accountLinkViewModel.url)?.also {
            config.saveAccountLinkUrl(it.groupValues[0])
            config.saveAccountLinkToken(this.accountLinkViewModel.token)
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
        } ?: run {
            coroutineScope {
                launch(Dispatchers.Main) {
                    Toast.makeText(applicationContext, "エラーが発生しました", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
    LoginWithAmazonによるアカウントリンク
     */
    override fun startAccountLinkWithLWA() {
        Log.i(javaClass.simpleName, "start account link -> ${this.accountLinkViewModel.url}")

        val accountLinkActivity = Intent(this, AccountLinkActivity::class.java)
        accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_URL,this.accountLinkViewModel.url)
        accountLinkActivity.putExtra(AccountLinkActivity.EXTRACT_TOKEN,this.accountLinkViewModel.token)
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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val packageInfo = packageManager.getPackageInfo(ALEXA_PACKAGE_NAME, PackageManager.PackageInfoFlags.of(0))
                packageInfo.longVersionCode > REQUIRED_MINIMUM_VERSION_CODE
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = packageManager.getPackageInfo(ALEXA_PACKAGE_NAME, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode > REQUIRED_MINIMUM_VERSION_CODE
                } else {
                    packageInfo != null
                }
            }

        } catch (e: PackageManager.NameNotFoundException) {
            // Alexaアプリがインストールされていない場合
            false
        }
    }
}
