package net.mythrowaway.app.view

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import net.mythrowaway.app.adapter.InformationViewInterface
import net.mythrowaway.app.adapter.MyThrowTrash
import net.mythrowaway.app.adapter.controller.InformationControllerImpl
import net.mythrowaway.app.adapter.di.InformationComponent
import net.mythrowaway.app.adapter.presenter.InformationPresenterImpl
import net.mythrowaway.app.databinding.ActivityInformationBinding
import net.mythrowaway.app.usecase.InformationPresenterInterface
import net.mythrowaway.app.viewmodel.InformationViewModel
import javax.inject.Inject

class InformationActivity : AppCompatActivity(), InformationViewInterface {
    @Inject
    lateinit var controller: InformationControllerImpl
    @Inject
    lateinit var presenter: InformationPresenterInterface

    private val viewModel: InformationViewModel by viewModels()
    private lateinit var activityInformationBinding: ActivityInformationBinding

    private lateinit var informationComponent: InformationComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        informationComponent = (application as MyThrowTrash).appComponent.informationComponent().create()
        informationComponent.inject(this)

        presenter.setView(this)
        (presenter as InformationPresenterImpl) .setViewModel(this.viewModel)
        super.onCreate(savedInstanceState)

        activityInformationBinding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(activityInformationBinding.root)

        activityInformationBinding.copyUserIdButton.setOnClickListener {
            (applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).let {
                val clipData = ClipData.newPlainText("userid",activityInformationBinding.userIdText.text)
                it.setPrimaryClip(clipData)
                Toast.makeText(applicationContext,"クリップボードにコピーしました",Toast.LENGTH_LONG).show()
            }
        }

        controller.loadInformation()
    }

    override fun showUserInfo(viewModel: InformationViewModel) {
        activityInformationBinding.userIdText.text = viewModel.userId
    }
}