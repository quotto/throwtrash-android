package net.mythrowaway.app.view.account_link

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import net.mythrowaway.app.viewmodel.AccountLinkViewModel
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.di.ConnectComponent
import net.mythrowaway.app.service.UsageInfoService
import net.mythrowaway.app.usecase.VersionRepositoryInterface
import javax.inject.Inject

class ConnectActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    @Inject
    lateinit var config: VersionRepositoryInterface
    @Inject
    lateinit var usageInfoService: UsageInfoService
    @Inject
    lateinit var accountLinkViewModelFactory: AccountLinkViewModel.Factory

    private lateinit var connectComponent: ConnectComponent
    override fun onCreate(savedInstanceState: Bundle?) {
        connectComponent = (application as MyThrowTrash).appComponent.connectComponent().create()
        connectComponent.inject(this)

        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider.create(
            this,
            accountLinkViewModelFactory
        )[AccountLinkViewModel::class.java]
        if (intent.action == Intent.ACTION_VIEW) {
            val uri = Uri.parse(intent.data.toString())
            val error = uri.getQueryParameter("error")
            if (error != null) {
                finish()
            } else {
                Log.d(javaClass.simpleName, "receive uri from alexa app ->${uri}")
                val code = uri.getQueryParameter("code")
                val state = uri.getQueryParameter("state")
                if (code != null && state != null) {
                    setContent {
                        FinishAccountLinkScreen(
                            viewModel = viewModel,
                            code = code,
                            state = state
                        )
                    }
                } else {
                    Toast.makeText(this, "エラーが発生しました", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        } else {
            setContent {
                StartAccountLinkScreen(
                    viewModel = viewModel
                )
            }
        }
    }
    override fun onDestroy() {
        Log.d(javaClass.simpleName, "onDestroy")
        super.onDestroy()
    }
}