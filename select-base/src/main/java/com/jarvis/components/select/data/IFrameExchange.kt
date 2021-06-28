package com.jarvis.components.select.data

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
interface IFrameExchange<T> {
    /***/
    fun encode(data: T, isAutoFocus: Boolean): FrameData

    fun decode(data: FrameData): T

    fun setBounds(left: Int, top: Int, right: Int, bottom: Int)
}