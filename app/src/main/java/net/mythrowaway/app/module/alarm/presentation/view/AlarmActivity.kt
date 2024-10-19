package net.mythrowaway.app.module.alarm.presentation.view

import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import net.mythrowaway.app.application.MyThrowTrash
import net.mythrowaway.app.application.di.AlarmComponent
import net.mythrowaway.app.ui.theme.AppTheme
import net.mythrowaway.app.module.alarm.usecase.AlarmUseCase
import net.mythrowaway.app.module.alarm.presentation.view_model.AlarmViewModel
import javax.inject.Inject

class AlarmActivity : AppCompatActivity() {

    @Inject
    lateinit var alarmUseCase: AlarmUseCase

    private lateinit var alarmComponent: AlarmComponent
    private val viewModel: AlarmViewModel by viewModels {
        val alarmReceiver = AlarmReceiver()
        alarmReceiver.init(this)
        AlarmViewModel.Factory(alarmUseCase,alarmReceiver)
    }

    // API>=30では「今後表示しない」の選択肢が削除され、
    // 1度許可するか2回続けて拒否するとダイアログが表示されずに現在の許可状態を返す
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i(this.javaClass.simpleName, "User granted Notifications Permission")
            } else {
                Log.w(this.javaClass.simpleName, "User did not grant Notifications Permission")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        alarmComponent = (application as MyThrowTrash).appComponent.alarmComponent().create()
        alarmComponent.inject(this)

        // API33以降で通知権限が付与されていなければリクエストダイアログを表示する
        if(ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED) {
            Log.d(this.javaClass.simpleName, "Notifications Permission granted")
        } else {
            Log.d(this.javaClass.simpleName, "Notifications Permission not granted")
            if(VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.d(this.javaClass.simpleName, "Show request permission rationale")
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                Log.w(this.javaClass.simpleName, "Don't show request permission rationale")
            }
        }

      setContent {
        AppTheme {
          AlarmScreen(
            alarmViewModel = viewModel,
          )
        }
      }
    }
}