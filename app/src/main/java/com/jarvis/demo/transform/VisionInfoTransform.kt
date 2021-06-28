package com.jarvis.demo.transform

import android.graphics.Rect
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.data.IFrameExchange
import com.jarvis.demo.data.VisionBoxInfo

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
class VisionInfoTransform: IFrameExchange<VisionBoxInfo> {

    private var bounds = Rect()

    override fun encode(data: VisionBoxInfo, isAutoFocus: Boolean): FrameData {
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        val centerX = boundsWidth * data.centerX
        val centerY = boundsHeight * data.centerY
        val width = boundsWidth * data.width
        val height = boundsHeight * data.height
        return FrameData(data, centerX, centerY, width, height, isAutoFocus)
    }

    override fun decode(data: FrameData): VisionBoxInfo {
        val boundsWidth = bounds.width()
        val boundsHeight = bounds.height()
        val centerX = data.centerX / boundsWidth
        val centerY = data.centerY / boundsHeight
        val width = data.width / boundsWidth
        val height = data.height / boundsHeight
        return VisionBoxInfo(centerX, centerY, width, height)
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        bounds.set(left, top, right, bottom)
    }
}