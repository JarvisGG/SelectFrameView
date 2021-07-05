package com.jarvis.components.select.frame

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.MotionEvent
import com.jarvis.components.select.anim.IAnimEngine
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.impl.ISelectWrapper
import com.jarvis.components.select.frame.*
import java.lang.ref.WeakReference

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc:
 */
class FrameController(
    override var context: Context,
    override var animEngine: IAnimEngine,
    override val host: ISelectWrapper
) : IFrameController {

    private var bounds: RectF? = null

    private val frames = arrayListOf<ISelectFrame>()

    private var activeFrame: ISelectFrame? = null

    private val changeListeners = arrayListOf<WeakReference<IFrameChangeListener>>()

    private val lock = Object()
    private val lock2 = Object()

    @SuppressWarnings("must called after onBoundsChange.")
    override fun buildFrames(datas: List<FrameData>) {
        synchronized(lock2) {
            datas.forEach { data ->
                val frameItem = buildUIFrame(data)
                frames.add(frameItem)
            }
            doInternalInvalidate()
        }
    }

    @SuppressWarnings("must called after onBoundsChange.")
    override fun doFrameShow(data: FrameData, hasAnim: Boolean) {
        if (activeFrame?.data?.origin == data.origin) {
            return
        }
        cleanUnFocusFrames()
        var index = -1
        frames.forEachIndexed { i, fd ->
            if (fd.data.origin == data.origin) {
                index = i
            }
        }
        if (index == -1) {
            val frameItem = buildUIFrame(data, false)
            frameItem.onBoundsChange(bounds)
            frames.add(frameItem)
            index = frames.size - 1
        } else {
            val extraFrame = frames[index]
            if (extraFrame.isSelecting) {
                return
            }
        }
        doInternalInvalidate()
        activeFrame = frames[index]
        doFrameAction(activeFrame, hasAnim)
    }

    @SuppressWarnings("must called after onBoundsChange.")
    override fun doFrameHide(hasAnim: Boolean) {
        doInternalInvalidate()
        activeFrame = null
        doFrameAction(activeFrame, hasAnim)
    }

    private fun cleanUnFocusFrames() {
        val iterator1 = frames.iterator()
        while (iterator1.hasNext()) {
            val item = iterator1.next()
            if (item.isAutoFocus) continue
            iterator1.remove()
        }
    }

    override fun clearFrames() {
        frames.clear()
        doInternalInvalidate()
    }

    private fun buildUIFrame(frameData: FrameData, isAutoFocus: Boolean = true): SelectFrame {
        return SelectFrame(context, frameData, false, this, isAutoFocus).apply {
            bindEngine(animEngine)
        }
    }

    override fun drawFrames(canvas: Canvas) {
        frames.sort()
        frames.forEach {
            it.onFrameDraw(canvas)
        }
        frames.forEach {
            it.onPointDraw(canvas)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var handle = false
        frames.forEach {
            val res = it.tryHandleTouchEvent(ev)
            if (res && !handle) handle = true
        }
        return handle
    }

    override fun doFrameAction(item: ISelectFrame?, hasAnim: Boolean) {
        if (animEngine.isAnim()) {
            animEngine.doEnd()
        }
        animEngine.doExecute(item, 0f, 1f, hasAnim)
    }

    override fun doInternalInvalidate() {
        host.doInternalInvalidate()
    }

    override fun onBoundsChange(bounds: RectF?) {
        this.bounds = bounds
        frames.forEach {
            it.onBoundsChange(bounds)
        }
    }

    override fun registerFrameChangeListener(listener: IFrameChangeListener) {
        synchronized(lock) {
            changeListeners.forEach {
                if (it.get() != null && it.get() == listener) {
                    return@synchronized
                }
            }
            changeListeners.add(WeakReference(listener))
        }
    }

    override fun onFrameChange(data: FrameData) {
        changeListeners.forEach {
            it.get()?.onFrameChange(data)
        }
    }

    override fun onFrameRelease(data: FrameData) {
        changeListeners.forEach {
            it.get()?.onFrameRelease(data)
        }
    }

    override fun onPointTouchDown(data: FrameData) {
        changeListeners.forEach {
            it.get()?.onPointTouchDown(data)
        }
    }

}