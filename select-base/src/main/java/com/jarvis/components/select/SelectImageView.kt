package com.jarvis.components.select

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.data.IFrameExchange
import com.jarvis.components.select.frame.IFrameChangeListener
import com.jarvis.components.select.impl.ISelectView
import com.jarvis.components.select.impl.SelectDrawable
import java.util.*

/**
 * @author: yyf
 * @date: 2021/6/28
 * @desc:
 */
class SelectImageView<T>: AppCompatImageView, ISelectView<T> {

    private val drawable by lazy {
        SelectDrawable(context)
    }

    private var transform: IFrameExchange<T>? = null

    private var pendingQueue = LinkedList<Runnable>()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setupDrawables()
    }

    private fun setupDrawables() {
        setWillNotDraw(false)
        drawable.callback = this
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        transform?.setBounds(left, top, right, bottom)
        doFrameCreate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let { drawable.draw(it) }
    }

    private fun doFrameCreate() {
        val iterator = pendingQueue.iterator()
        while (iterator.hasNext()) {
            val runnable = iterator.next()
            runnable.run()
            iterator.remove()
        }
    }

    override fun setFrameData(data: ArrayList<T>) {
        if (transform == null) return
        pendingQueue.add(Runnable {
            drawable.clearFrames()
            drawable.buildFrames(data.map { transform!!.encode(it, true) })
            drawable.setBounds(left, top, right, bottom)
        })
        requestLayout()
    }

    override fun showFrameData(data: T, hasAnim: Boolean) {
        if (transform == null) return
        pendingQueue.add(Runnable {
            drawable.doShowFrame(transform!!.encode(data, false), hasAnim)
        })
        requestLayout()
    }

    override fun hideFrameData(hasAnim: Boolean) {
        if (transform == null) return
        pendingQueue.add(Runnable {
            drawable.doFrameHide(hasAnim)
        })
        requestLayout()
    }

    override fun clearFrames() {
        pendingQueue.add(Runnable {
            drawable.clearFrames()
        })
        requestLayout()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        super.onTouchEvent(ev)
        drawable.onTouchEvent(ev)
        return true
    }

    override fun verifyDrawable(dr: Drawable): Boolean {
        if (dr == drawable) return true
        return super.verifyDrawable(dr)
    }

    override fun registerFrameChangeListener(listener: IFrameChangeListener) {
        drawable.registerFrameChangeListener(listener)
    }

    override fun registerDataTransform(transform: IFrameExchange<T>) {
        this.transform = transform
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        pendingQueue.clear()
    }
}

class FrameChangeWrapper<T>(
    private val listener: ISelectView.ISelectFrameChange<T>,
    private val transform: IFrameExchange<T>
): IFrameChangeListener {
    override fun onFrameChange(data: FrameData) {
        listener.onFrameChange(transform.decode(data))
    }

    override fun onFrameRelease(data: FrameData) {
        listener.onFrameRelease(transform.decode(data), data.isAutoFocus)
    }

    override fun onPointTouchDown(data: FrameData) {
        listener.onPointTouchDown(data.origin as T)
    }
}