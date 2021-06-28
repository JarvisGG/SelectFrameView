package com.jarvis.components.select.impl

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.MotionEvent
import androidx.core.graphics.ColorUtils
import com.jarvis.components.select.anim.DynamicEngine
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.frame.FrameController
import com.jarvis.components.select.frame.IFrameChangeListener

/**
 * @author: yyf
 * @date: 2021/6/10
 * @desc:
 */
class SelectDrawable(
    private val context: Context
): Drawable(), ISelectWrapper {

    /** 默认采用弹簧刚性动画 */
    private val animEngine by lazy {
        DynamicEngine(this)
    }

    private val frameController by lazy {
        FrameController(context, animEngine, this)
    }

    private val maskPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = ColorUtils.setAlphaComponent(Color.BLACK, (255 * 0.2).toInt())
            isAntiAlias = true
        }
    }

    override fun setBounds(left: Int, top: Int, right: Int, bottom: Int) {
        super.setBounds(left, top, right, bottom)
        frameController.onBoundsChange(RectF(bounds))
    }

    override fun draw(canvas: Canvas) {
        val layerId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.saveLayer(RectF(bounds) , null)
        } else {
            canvas.saveLayer(RectF(bounds), null, Canvas.ALL_SAVE_FLAG)
        }
        drawCover(canvas)
        frameController.drawFrames(canvas)
        canvas.restoreToCount(layerId)
    }

    private fun drawCover(canvas: Canvas) {
        canvas.drawRect(bounds, maskPaint)
    }

    override fun doInternalInvalidate() {
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        maskPaint.alpha = alpha
        doInternalInvalidate()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        maskPaint.colorFilter = colorFilter
        doInternalInvalidate()
    }

    override fun getOpacity() = PixelFormat.TRANSLUCENT

    override fun buildFrames(data: List<FrameData>) {
        frameController.buildFrames(data)
    }

    override fun clearFrames() {
        frameController.clearFrames()
    }

    override fun doShowFrame(frame: FrameData, hasAnim: Boolean) {
        frameController.doFrameShow(frame, hasAnim)
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        frameController.dispatchTouchEvent(ev)
        return true
    }

    override fun registerFrameChangeListener(listener: IFrameChangeListener) {
        frameController.registerFrameChangeListener(listener)
    }


}