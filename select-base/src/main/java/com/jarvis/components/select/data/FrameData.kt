package com.jarvis.components.select.data

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc:
 */
data class FrameData(
    var origin: Any,
    var centerX: Float,
    var centerY: Float,
    var width: Float,
    var height: Float,
    /** 是否包含服务端锚点 */
    var isAutoFocus: Boolean = false
)
