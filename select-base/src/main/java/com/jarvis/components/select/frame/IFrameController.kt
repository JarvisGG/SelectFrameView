package com.jarvis.components.select.frame

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import com.jarvis.components.select.anim.IAnimEngine
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.impl.ISelectWrapper

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc:
 */
interface IFrameController: IFrameChangeListener {

    var context: Context

    var animEngine: IAnimEngine

    val host: ISelectWrapper

    fun buildFrames(datas: List<FrameData>)


    fun clearFrames()

    fun drawFrames(canvas: Canvas)

    fun dispatchTouchEvent(ev: MotionEvent?): Boolean

    fun doFrameShow(data: FrameData, hasAnim: Boolean)

    fun doFrameShow(item: ISelectFrame, hasAnim: Boolean)

    fun doInternalInvalidate()

    fun onBoundsChange(bounds: RectF?)

    fun registerFrameChangeListener(listener: IFrameChangeListener)


}