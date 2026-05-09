package net.mythrowaway.app.module.other.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import net.mythrowaway.app.ui.theme.AppTheme

class OtherActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      AppTheme {
        OtherScreen(
          onOpenTerms = {
            startActivity(Intent(this, TermsActivity::class.java))
          },
          onOpenPrivacyPolicy = {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
          },
          onOpenLicense = {
            OssLicensesMenuActivity.setActivityTitle("ライセンス")
            startActivity(Intent(this, OssLicensesMenuActivity::class.java))
          },
        )
      }
    }
  }
}
