package com.jarvis.components.select.anim

import androidx.annotation.MainThread
import com.jarvis.components.select.impl.ISelectWrapper

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc:
 */
interface IAnimEngine {
    val host: ISelectWrapper

    @MainThread
    fun registerAnimItem(item: IAnimItem)

    @MainThread
    fun isAnim(): Boolean

    @MainThread
    fun doExecute(
        frame: IAnimItem,
        start: Float?,
        end: Float,
        hasAnim: Boolean
    )

    @MainThread
    fun doCancel()

    @MainThread
    fun doEnd()

    interface IAnimItem {

        fun isAnimating(): Boolean

        fun onAnimStart()

        fun onAnimUpdate(fraction: Float)

        fun onAnimFinish()
    }
}