package net.mythrowaway.app.domain.inquiry.presentation.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import net.mythrowaway.app.ui.theme.AppTheme

class InquiryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                InquiryScreen()
            }
        }
    }
}