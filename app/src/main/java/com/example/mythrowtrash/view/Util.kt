package com.example.mythrowtrash.view

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import androidx.core.content.ContextCompat
import kotlin.math.truncate

class Util(): Activity() {
    companion object {
        fun getEqualHeight(context: Context,percent: Float, divNum: Int): Int {
            val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val p = Point()
            wm.defaultDisplay.getSize(p)

            // 縦幅の6割を5等分する
            return truncate(p.y.toFloat() * percent / divNum.toFloat()).toInt()
        }

        fun getEqualWidth(context: Context,percent: Float, divNum: Int): Int {
            val wm: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val p = Point()
            wm.defaultDisplay.getSize(p)

            // 縦幅の6割を5等分する
            return truncate(p.x.toFloat() * percent / divNum.toFloat()).toInt()
        }
    }
}