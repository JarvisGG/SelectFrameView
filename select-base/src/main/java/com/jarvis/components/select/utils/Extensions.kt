package com.jarvis.components.select.utils

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import kotlin.math.round

/**
 * @author: yyf
 * @date: 2020/7/28
 * @desc: kt 扩展
 */

/**
 * dp 转 px
 */
val Number.dp2px @JvmName("dp2px") get() = round(toFloat() * Resources.getSystem().displayMetrics.density)
/**
 * sp 转 px
 */
val Number.sp2px @JvmName("sp2px") get() = round(toFloat() * Resources.getSystem().displayMetrics.scaledDensity)

/**
 * px 转 dp
 */
val Number.px2dp @JvmName("px2dp") get() = round(toFloat() / Resources.getSystem().displayMetrics.density)

/**
 * px 转 sp
 */
val Number.px2sp @JvmName("px2sp") get() = round(toFloat() / Resources.getSystem().displayMetrics.scaledDensity)

/**
 * 获得屏幕高度
 */
fun Context.screenHeight() = this.resources.displayMetrics.heightPixels

/**
 * 获得屏幕宽度
 */
fun Context.screenWidth() = this.resources.displayMetrics.widthPixels



inline fun <reified T : View> ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): T {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot) as T
}


/**
 * 获取状态栏高度
 */
fun Context.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

