package net.mythrowaway.app.lib

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes
import androidx.test.espresso.IdlingResource

class ViewGoneIdlingResource(
  private val activity: Activity,
  @IdRes private val viewId: Int
) : IdlingResource {

  @Volatile
  private var callback: IdlingResource.ResourceCallback? = null

  override fun getName(): String = "ViewGoneIdlingResource for $viewId"
  override fun isIdleNow(): Boolean {
    val view = activity.findViewById<View>(viewId) ?: return true
    val isGone = view.visibility == View.INVISIBLE || view.visibility == View.GONE

    if (isGone) {
      callback?.onTransitionToIdle()
    }
    return isGone
  }

  override fun registerIdleTransitionCallback(cb: IdlingResource.ResourceCallback?) {
    this.callback = cb
  }
}
