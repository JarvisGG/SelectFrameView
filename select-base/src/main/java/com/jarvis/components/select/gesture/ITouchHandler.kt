package com.jarvis.components.select.gesture

import android.graphics.RectF
import android.view.MotionEvent

/**
 * @author: yyf
 * @date: 2021/6/11
 * @desc:
 */
interface ITouchHandler {
    /** 拖拽展示区域 */
    fun setDragEdgeAreas(areas: List<RectF>)
    /** 拖拽位置区域 */
    fun setDragLocateAreas(areas: List<RectF>)
    /** 中心点区域 */
    fun setPointLocateAreas(areas: List<RectF>)
    /** frame 处理 touch 事件 */
    fun onHandleTouchByFrame(ev: MotionEvent?): Boolean
    /** point 处理 touch 事件 */
    fun onHandleTouchByPoint(ev: MotionEvent?): Boolean

    interface Callback {
        /** 手指拖拽 frame 区域 */
        fun onFrameChange(selectedFrame: RectF)
        /** 手指释放 frame 区域 */
        fun onFrameRelease(selectedFrame: RectF)
        /** 坐标触摸 */
        fun onPointTouchDown()
    }
}
