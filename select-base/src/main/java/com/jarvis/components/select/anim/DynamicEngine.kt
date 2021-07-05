package com.jarvis.components.select.anim

import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.springAnimationOf
import com.jarvis.components.select.impl.ISelectWrapper
import java.lang.ref.WeakReference

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc: 弹簧动画
 */
class DynamicEngine(override val host: ISelectWrapper): IAnimEngine {

    companion object {
        private const val DEFAULT_STEP = 200f
    }

    private val holder = InnerHolder()

    private var oldFrame: IAnimEngine.IAnimItem? = null

    private var newFrame: IAnimEngine.IAnimItem? = null

    private var executor: SpringAnimation? = null

    private var endValue = 0f

    private val springForce by lazy {
        SpringForce().apply {
            stiffness = SpringForce.STIFFNESS_LOW
            dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
        }
    }

    override fun doExecute(
        frame: IAnimEngine.IAnimItem?,
        start: Float?,
        end: Float,
        hasAnim: Boolean
    ) {
        doExecuteInternal(frame, start, end, hasAnim)
    }

    private fun doExecuteInternal(
        frame: IAnimEngine.IAnimItem?,
        start: Float?,
        end: Float,
        hasAnim: Boolean
    ) {
        newFrame = frame
        holder.onAnimStart()
        executor = springAnimationOf(
            { f -> holder.onAnimUpdate(f)},
            { holder.fraction }
        ).apply {
            spring = springForce
            addEndListener { _, _, _, _ ->
                holder.onAnimFinish()
            }
        }
        this.endValue = end
        if (hasAnim) {
            executor?.animateToFinalPosition(end * DEFAULT_STEP)
        } else {
            holder.onAnimStart()
            holder.onAnimUpdate(endValue)
            holder.onAnimFinish()
        }
    }

    override fun registerAnimItem(item: IAnimEngine.IAnimItem) {
        holder.register(item)
    }

    override fun isAnim(): Boolean {
        return executor?.isRunning ?: false
    }

    override fun doEnd() {
        executor?.cancel()
    }

    override fun doCancel() {
        executor?.cancel()
        holder.onAnimFinish()
    }

    inner class InnerHolder {

        var fraction = 0f
            private set

        private val items = ArrayList<WeakReference<IAnimEngine.IAnimItem>>()

        fun onAnimStart() {
            this.fraction = 0f
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
                        newFrame -> it.get()?.onAnimUpdate(fraction / DEFAULT_STEP)
                        oldFrame -> it.get()?.onAnimUpdate(1 - fraction / DEFAULT_STEP)
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