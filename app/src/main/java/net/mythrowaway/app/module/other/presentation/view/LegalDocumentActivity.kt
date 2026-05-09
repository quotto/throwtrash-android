package net.mythrowaway.app.module.other.presentation.view

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import net.mythrowaway.app.ui.theme.AppTheme

class TermsActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        LegalDocumentScreen(
          title = "利用規約",
          sections = termsSections,
        )
      }
    }
  }
}

class PrivacyPolicyActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        LegalDocumentScreen(
          title = "プライバシーポリシー",
          sections = privacyPolicySections,
        )
      }
    }
  }
}
