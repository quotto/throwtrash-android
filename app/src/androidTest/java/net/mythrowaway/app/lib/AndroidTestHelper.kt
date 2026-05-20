package net.mythrowaway.app.lib

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.SearchCondition
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import net.mythrowaway.app.R

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

    // 指定したテキストの要素をタップする
    fun clickByText(
      text: String,
      timeout: Long = 10000,
    ) {
      val bySelector: BySelector = By.text(text)
      val searchCondition: SearchCondition<UiObject2> = Until.findObject(bySelector)
      val target = uiDevice.wait(searchCondition, timeout)
      checkNotNull(target) { "対象のテキストが見つかりませんでした: $text" }
      target.click()
    }

    // 指定したリソースIDの最初の要素をタップする
    fun clickFirstByRes(
      resourceName: String,
      timeout: Long = 10000,
    ) {
      val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
      val bySelector: BySelector = By.res(packageName, resourceName)
      val searchCondition: SearchCondition<UiObject2> = Until.findObject(bySelector)
      val target = uiDevice.wait(searchCondition, timeout)
      checkNotNull(target) { "対象のリソースIDが見つかりませんでした: $resourceName" }
      target.click()
    }

    // 指定したパッケージ/リソースIDのテキストを取得する
    fun getTextByRes(
      packageName: String,
      resourceName: String,
      timeout: Long = 10000,
    ): String {
      val bySelector: BySelector = By.res(packageName, resourceName)
      val searchCondition: SearchCondition<UiObject2> = Until.findObject(bySelector)
      val target = uiDevice.wait(searchCondition, timeout)
      checkNotNull(target) { "対象のリソースIDが見つかりませんでした: $packageName:$resourceName" }
      return target.text
    }

    // 指定したリソースIDのViewが非表示になるまで待機する
    fun waitUntilGone(
      resourceName: String,
      timeout: Long = 10000,
    ) {
      val packageName = InstrumentationRegistry.getInstrumentation().targetContext.packageName
      val bySelector: BySelector = By.res(packageName, resourceName)
      uiDevice.wait(Until.gone(bySelector), timeout)
    }

    fun dismissScheduleSearchStartupDialogIfDisplayed(timeout: Long = 5000) {
      val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
      val dialogTitle = resources.getString(R.string.title_schedule_search_import_dialog)
      val closeLabel = resources.getString(R.string.label_close_button)
      val titleSelector = By.text(dialogTitle)
      val dialog = uiDevice.wait(Until.findObject(titleSelector), timeout)
      if (dialog != null) {
        clickByText(closeLabel, timeout)
        uiDevice.wait(Until.gone(titleSelector), timeout)
      }
    }
  }
}
