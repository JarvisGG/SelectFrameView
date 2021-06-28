package com.jarvis.components.select.frame

import com.jarvis.components.select.data.FrameData

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
interface IFrameChangeListener {
    fun onFrameChange(data: FrameData)

    fun onFrameRelease(data: FrameData)

    fun onPointTouchDown(data: FrameData)
}