package com.jarvis.components.select.frame

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.gesture.ITouchHandler
import com.jarvis.components.select.gesture.TouchHandler
import com.jarvis.components.select.utils.dp2px

/**
 * @author: yyf
 * @date: 2021/6/11
 * @desc:
 */
class SelectFrame(
    override var context: Context,
    override var data: FrameData,
    @Volatile
    override var isSelected: Boolean = false,
    override var host: IFrameController,
    override var isAutoFocus: Boolean = true,
    override var isSelecting: Boolean = false,
    override var isUnSelecting: Boolean = false
) : ISelectFrame, ITouchHandler.Callback {

    private var frame = RectF()
    private var point = PointF()

    init {
        calculateBaseSize()
    }

    companion object {
        private val DEFAULT_HOLLOW_RADIUS = 6.dp2px
        private val DEFAULT_HOLLOW_EDGE_TOUCH_RANGE = 12.dp2px
        private val DEFAULT_HOLLOW_MIN_FRAME_SIZE = 48.dp2px
        private val DEFAULT_POINT_INNER_RADIUS = 5.dp2px
        private val DEFAULT_POINT_OUTER_RADIUS = 8.dp2px
        private const val DEFAULT_POINT_INNER_ALPHA = 1f
        private const val DEFAULT_POINT_OUTER_ALPHA = 0.35f
        private const val DEFAULT_POINT_CORNET_ALPHA = 1f
    }

    private val pointPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            isAntiAlias = true
        }
    }
    private var pointInnerRadius = DEFAULT_POINT_INNER_RADIUS
    private var pointOuterRadius = DEFAULT_POINT_OUTER_RADIUS
    private var pointInnerAlpha = DEFAULT_POINT_INNER_ALPHA
    private var pointOuterAlpha = DEFAULT_POINT_OUTER_ALPHA
    private var pointCornerAlpha = DEFAULT_POINT_CORNET_ALPHA

    private val hollowPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            isAntiAlias = true
        }
    }
    private val hollowMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private var hollowRectRadius = DEFAULT_HOLLOW_RADIUS

    private val cornerPaint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = 4.dp2px
            style = Paint.Style.STROKE
            color = Color.WHITE
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
            isAntiAlias = true
            pathEffect = cornerEffect
        }
    }
    private val cornerEffect by lazy {
        CornerPathEffect(6.dp2px)
    }
    private val defaultCornerSize = 20.dp2px
    private var cornerSize = defaultCornerSize

    override fun calculateAreas(fraction: Float) {
        calculatePointAreas(fraction)
        calculateHollowRect(fraction)
        calculateDragShapeAreas()
        calculateLocateAreas()
        calculateCornerProperty(fraction)
    }

    override fun onBoundsChange(bounds: RectF?) {
        bounds?.let { maxRect.set(it) }
        calculateAreas(if (isSelected) 1f else 0f)
    }

    private fun calculateBaseSize() {
        point = PointF(data.centerX, data.centerY)
        val width = data.width
        val height = data.height
        val left = point.x - width / 2
        val right = point.x + width / 2
        val top = point.y - height / 2
        val bottom = point.y + height / 2
        frame = RectF(left, top, right, bottom)
    }

    private fun calculateDragShapeAreas() {
        trySetDragEdgeAreas(listOf(
            RectF(hollowRect.left - cornerSize, hollowRect.top - cornerSize,
                hollowRect.left + cornerSize, hollowRect.top + cornerSize),
            RectF(hollowRect.right - cornerSize, hollowRect.top - cornerSize,
                hollowRect.right + cornerSize, hollowRect.top + cornerSize),
            RectF(hollowRect.right - cornerSize, hollowRect.bottom - cornerSize,
                hollowRect.right + cornerSize, hollowRect.bottom + cornerSize),
            RectF(hollowRect.left - cornerSize, hollowRect.bottom - cornerSize,
                hollowRect.left + cornerSize + cornerSize, hollowRect.bottom + cornerSize)
        ))
    }

    private fun calculateLocateAreas() {
        trySetDragLocateAreas(listOf(hollowRect))
    }

    private fun calculateCornerProperty(fraction: Float) {
        cornerSize = defaultCornerSize * fraction
        pointCornerAlpha = DEFAULT_POINT_CORNET_ALPHA * fraction
    }

    private fun calculateFrameData() {
        data.centerX = hollowRect.centerX()
        data.centerY = hollowRect.centerY()
        data.width = hollowRect.width()
        data.height = hollowRect.height()
    }

    private fun markAnim(res: Int) {
        when (res) {
            0 -> {
                isSelecting = false
                isUnSelecting = false
            }
            1 -> {
                isSelecting = true
                isUnSelecting = false
            }
            -1 -> {
                isSelecting = false
                isUnSelecting = true
            }
        }
    }

    override fun onFrameDraw(canvas: Canvas) {
        canvas.save()
        drawHollow(canvas)
        drawBounds(canvas)
        canvas.restore()
    }

    override fun onPointDraw(canvas: Canvas) {
        canvas.save()
        drawPoint(canvas)
        canvas.restore()
    }

    override fun onAnimStart() {
        if (isSelected) {
            markAnim(-1)
            scaleRect.set(hollowRect)
        } else {
            markAnim(1)
            scaleRect.set(frame)
        }
    }

    override fun onAnimUpdate(fraction: Float) {
        calculateAreas(fraction)
        host.doInternalInvalidate()
    }

    override fun onAnimFinish() {
        isSelected = !isSelected
        calculateAreas(if (isSelected) 1f else 0f)
        markAnim(0)
        host.doInternalInvalidate()
    }

    private fun drawPoint(canvas: Canvas) {
        if (!isAutoFocus) return
        canvas.save()
        pointPaint.alpha = (pointOuterAlpha * 255).toInt()
        canvas.drawCircle(point.x, point.y, pointOuterRadius, pointPaint)
        pointPaint.alpha = (pointInnerAlpha * 255).toInt()
        canvas.drawCircle(point.x, point.y, pointInnerRadius, pointPaint)
        canvas.restore()
    }

    private fun drawHollow(canvas: Canvas) {
        hollowPaint.xfermode = hollowMode
        canvas.drawRoundRect(hollowRect, hollowRectRadius, hollowRectRadius, hollowPaint)
        hollowPaint.xfermode = null
    }

    private fun drawBounds(canvas: Canvas) {
        canvas.save()
        val path = Path()
        // left-top
        path.moveTo(hollowRect.left, hollowRect.top + cornerSize)
        path.lineTo(hollowRect.left, hollowRect.top)
        path.lineTo(hollowRect.left + cornerSize, hollowRect.top)
        // right-top
        path.moveTo(hollowRect.right - cornerSize, hollowRect.top)
        path.lineTo(hollowRect.right, hollowRect.top)
        path.lineTo(hollowRect.right, hollowRect.top + cornerSize)
        // right-bottom
        path.moveTo(hollowRect.right, hollowRect.bottom - cornerSize)
        path.lineTo(hollowRect.right, hollowRect.bottom)
        path.lineTo(hollowRect.right - cornerSize, hollowRect.bottom)
        // left-bottom
        path.moveTo(hollowRect.left + cornerSize, hollowRect.bottom)
        path.lineTo(hollowRect.left, hollowRect.bottom)
        path.lineTo(hollowRect.left, hollowRect.bottom - cornerSize)
        cornerPaint.alpha = (255 * pointCornerAlpha).toInt()
        canvas.drawPath(path, cornerPaint)
        canvas.restore()
    }

    override fun onFrameChange(selectedFrame: RectF) {
        hollowRect.set(selectedFrame)
        calculateDragShapeAreas()
        calculateLocateAreas()
        calculateFrameData()
        host.onFrameChange(data)
        host.doInternalInvalidate()
    }

    override fun onFrameRelease(selectedFrame: RectF) {
        hollowRect.set(selectedFrame)
        calculateDragShapeAreas()
        calculateLocateAreas()
        calculateFrameData()
        host.onFrameRelease(data)
        host.doInternalInvalidate()
    }

    override fun onPointTouchDown() {
        if (isAnimating()) return
        host.onPointTouchDown(data)
    }

    private fun calculatePointAreas(fraction: Float) {
        val left = point.x - DEFAULT_HOLLOW_EDGE_TOUCH_RANGE
        val right = point.x + DEFAULT_HOLLOW_EDGE_TOUCH_RANGE
        val top = point.y - DEFAULT_HOLLOW_EDGE_TOUCH_RANGE
        val bottom = point.y + DEFAULT_HOLLOW_EDGE_TOUCH_RANGE
        val pointRect = RectF(left, top, right, bottom)
        trySetPointLocateAreas(arrayListOf(pointRect))
        pointInnerRadius = (1 - fraction) * DEFAULT_POINT_INNER_RADIUS
        pointOuterRadius = (1 - fraction)  * DEFAULT_POINT_OUTER_RADIUS
        pointInnerAlpha = (1 - fraction) * DEFAULT_POINT_INNER_ALPHA
        pointOuterAlpha = (1 - fraction) * DEFAULT_POINT_OUTER_ALPHA
    }

    private fun calculateHollowRect(fraction: Float) {
        val f = RectF(scaleRect)
        val centerX = f.centerX()
        val centerY = f.centerY()
        val width = f.width()
        val height = f.height()
        val scaledF = RectF(
            centerX - width * fraction / 2,
            centerY - height * fraction / 2,
            centerX + width * fraction / 2,
            centerY + height * fraction / 2
        )
        hollowRect.set(scaledF)
    }

    private var hollowRect = RectF(frame)

    private var scaleRect = RectF()

    private var minRect = RectF(0f, 0f, DEFAULT_HOLLOW_MIN_FRAME_SIZE, DEFAULT_HOLLOW_MIN_FRAME_SIZE)

    private var maxRect = RectF()

    private val touchHandler by lazy {
        TouchHandler(context, minRect, maxRect, hollowRect, this)
    }

    private fun trySetDragEdgeAreas(areas: List<RectF>) {
        if (!isSelected) return
        touchHandler.setDragEdgeAreas(areas)
    }

    private fun trySetDragLocateAreas(areas: List<RectF>) {
        if (!isSelected) return
        touchHandler.setDragLocateAreas(areas)
    }

    private fun trySetPointLocateAreas(areas: List<RectF>) {
        touchHandler.setPointLocateAreas(areas)
    }

    override fun tryHandleTouchEvent(ev: MotionEvent?): Boolean {
        return if (isSelected) {
            handleTouchByFrame(ev)
        } else {
            handleTouchByPoint(ev)
        }
    }

    private fun handleTouchByPoint(ev: MotionEvent?) =
        touchHandler.onHandleTouchByPoint(ev)

    private fun handleTouchByFrame(ev: MotionEvent?) =
        touchHandler.onHandleTouchByFrame(ev)

    override fun compareTo(other: ISelectFrame): Int {
        return if(other.isAnimating()) 1 else -1
    }

    override fun isAnimating(): Boolean {
        return isSelecting || isUnSelecting
    }
}