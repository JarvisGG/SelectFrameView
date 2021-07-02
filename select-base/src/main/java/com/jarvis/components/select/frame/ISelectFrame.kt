package com.jarvis.components.select.frame

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import com.jarvis.components.select.anim.IAnimEngine
import com.jarvis.components.select.data.FrameData

/**
 * @author: yyf
 * @date: 2021/6/11
 * @desc:
 */
interface ISelectFrame: IAnimEngine.IAnimItem, Comparable<ISelectFrame> {

    var context: Context

    var data: FrameData

    var host: IFrameController

    var isAutoFocus: Boolean

    var isSelected: Boolean

    var isSelecting: Boolean

    var isUnSelecting: Boolean

    fun calculateAreas(fraction: Float)

    fun onBoundsChange(bounds: RectF?)

    fun onFrameDraw(canvas: Canvas)

    fun onPointDraw(canvas: Canvas)

    fun tryHandleTouchEvent(ev: MotionEvent?): Boolean
}

fun ISelectFrame.bindEngine(engine: IAnimEngine) {
    engine.registerAnimItem(this)
}