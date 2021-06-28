package com.jarvis.components.select.impl

import com.jarvis.components.select.data.IFrameExchange
import com.jarvis.components.select.frame.IFrameChangeListener

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
interface ISelectView<T> {

    fun doScaleChange(scale: Float)

    fun registerFrameChangeListener(listener: IFrameChangeListener)

    fun registerDataTransform(transform: IFrameExchange<T>)

    fun setFrameData(data: ArrayList<T>)

    fun showFrameData(data: T, hasAnim: Boolean = true)

    fun clearFrames()

    interface ISelectFrameChange<T> {
        fun onFrameChange(data: T)

        fun onFrameRelease(data: T, isAutoFocus: Boolean)

        fun onPointTouchDown(data: T)
    }
}