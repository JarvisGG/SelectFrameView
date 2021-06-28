package com.jarvis.components.select.anim

import android.animation.Animator
import android.animation.ValueAnimator
import com.jarvis.components.select.impl.ISelectWrapper
import java.lang.ref.WeakReference

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc: 默认线性动画
 */
class DefaultEngine(override val host: ISelectWrapper) : IAnimEngine {

    private val holder = InnerHolder()

    private var executor: ValueAnimator? = null

    private var oldFrame: IAnimEngine.IAnimItem? = null

    private var newFrame: IAnimEngine.IAnimItem? = null

    override fun doExecute(
        frame: IAnimEngine.IAnimItem,
        start: Float?,
        end: Float,
        hasAnim: Boolean
    ) {
        newFrame = frame
        holder.onAnimStart()
        executor = ValueAnimator.ofFloat(holder.fraction, end)
        executor?.addUpdateListener { animation -> holder.onAnimUpdate(animation.animatedFraction)}
        executor?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                holder.onAnimFinish()
            }
            override fun onAnimationCancel(animation: Animator?) {
                holder.onAnimFinish()
            }
        })
        if (hasAnim) {
            executor?.duration = 300
        } else {
            executor?.duration = 0
        }
        executor?.start()
    }

    override fun registerAnimItem(item: IAnimEngine.IAnimItem) {
        holder.register(item)
    }

    override fun isAnim(): Boolean {
        return executor?.isRunning ?: false
    }

    override fun doEnd() {
        executor?.end()
    }

    override fun doCancel() {
        executor?.cancel()
    }

    inner class InnerHolder {
        var fraction = 0f
            private set

        private val items = ArrayList<WeakReference<IAnimEngine.IAnimItem>>()

        fun onAnimStart() {
            items.forEach {
                if (it.get() != null) {
                    when (it.get()) {
                        newFrame -> it.get()?.onAnimStart()
                        oldFrame -> it.get()?.onAnimStart()
                    }
                }
            }
        }

        fun onAnimUpdate(fraction: Float) {
            this.fraction = fraction
            items.forEach {
                if (it.get() != null) {
                    when (it.get()) {
                        newFrame -> it.get()?.onAnimUpdate(fraction)
                        oldFrame -> it.get()?.onAnimUpdate(1 - fraction)
                    }
                }
            }
            host.doInternalInvalidate()
        }

        fun onAnimFinish() {
            items.forEach {
                if (it.get() != null) {
                    when (it.get()) {
                        newFrame -> it.get()?.onAnimFinish()
                        oldFrame -> it.get()?.onAnimFinish()
                    }
                }
            }
            oldFrame = newFrame
            newFrame = null
            host.doInternalInvalidate()
        }

        private val lock = Object()

        fun register(item: IAnimEngine.IAnimItem) {
            synchronized(lock) {
                items.forEach {
                    if (it.get() == item)
                        return@synchronized
                }
                items.add(WeakReference(item))
            }
        }
    }
}