package com.jarvis.demo.data

import java.util.*

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
data class VisionBoxInfo(
    val centerX: Float = 0f,
    val centerY: Float = 0f,
    val width: Float = 0f,
    val height: Float = 0f
) {
    var id = UUID.randomUUID().toString()
}