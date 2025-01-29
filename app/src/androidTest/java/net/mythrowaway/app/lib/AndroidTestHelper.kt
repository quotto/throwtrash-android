package net.mythrowaway.app.lib

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until

class AndroidTestHelper {
  companion object {
    private val uiDevice by lazy {
      UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }
    // UiAutomator2による指定した要素が表示されるまで待機するヘルパーメソッド
    fun waitUntilDisplayed(
      text: String,
      timeout: Long = 10000,
    ) {
      val bySelector: BySelector = By.text(text)
      val searchCondition: SearchCondition<UiObject2> = Until.findObject(bySelector)
      uiDevice.wait(searchCondition, timeout)
    }
  }
}