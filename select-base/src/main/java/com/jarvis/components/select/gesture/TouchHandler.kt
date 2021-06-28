package com.jarvis.components.select.gesture

import android.content.Context
import android.graphics.RectF
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.customview.widget.ViewDragHelper
import kotlin.math.hypot

/**
 * @author: yyf
 * @date: 2021/6/11
 * @desc:
 */
class TouchHandler(
    private val context: Context,
    private val minFrame: RectF,
    private val maxFrame: RectF,
    private val selectedFrame: RectF,
    private val callback: ITouchHandler.Callback
): ITouchHandler {

    companion object {
        const val STATE_IDLE = 0
        const val STATE_DRAGGING = 1
        private const val LEFT_TOP = 0
        private const val RIGHT_TOP = 1
        private const val RIGHT_BOTTOM = 2
        private const val LEFT_BOTTOM = 3
    }

    private var activePointerId = ViewDragHelper.INVALID_POINTER
    private var initialMotionX = hashMapOf<Int, Float>()
    private var initialMotionY = hashMapOf<Int, Float>()
    private var lastMotionX = hashMapOf<Int, Float>()
    private var lastMotionY = hashMapOf<Int, Float>()
    private var leftTopEdges = arrayListOf<Int>()
    private var rightTopEdges = arrayListOf<Int>()
    private var rightBottomEdges = arrayListOf<Int>()
    private var leftBottomEdges = arrayListOf<Int>()
    private var locations = arrayListOf<Int>()
    private var pointersDown = 0

    private val touchSlop by lazy {
        ViewConfiguration.get(context).scaledTouchSlop
    }

    private var dragState = STATE_IDLE

    override fun onHandleTouchByPoint(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        val action = ev.action
        if (action == MotionEvent.ACTION_DOWN &&
            isPointerInPointArea(ev.x, ev.y)) {
            cancel()
            callback.onPointTouchDown()
            return true
        }
        return false
    }

    override fun onHandleTouchByFrame(ev: MotionEvent?): Boolean {
        if (ev == null) return false
        val action = ev.actionMasked
        if (action == MotionEvent.ACTION_DOWN) {
            cancel()
        }
        if (!isPointerInDragArea(ev.x, ev.y) && action == MotionEvent.ACTION_DOWN) return false
        addMovement(ev)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val x: Float = ev.x
                val y: Float = ev.y
                val pointerId = ev.getPointerId(0)
                saveInitialMotion(x, y, pointerId)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = ev.actionIndex
                val x: Float = ev.getX(index)
                val y: Float = ev.getY(index)
                val pointerId: Int = ev.getPointerId(index)
                saveInitialMotion(x, y, pointerId)
            }
            MotionEvent.ACTION_MOVE -> {
                if (initialMotionX.isEmpty() || initialMotionY.isEmpty()) {
                    return true
                }
                val pointerCount = ev.pointerCount
                for (index in 0 until pointerCount) {
                    val pointerId = ev.getPointerId(index)
                    if (!isPointerDown(pointerId)) {
                        continue
                    }
                    val x = ev.getX(index)
                    val y = ev.getY(index)
                    val dx = x - (lastMotionX[pointerId] ?: 0f)
                    val dy = y - (lastMotionY[pointerId] ?: 0f)
                    doCustomDxInternal(dx, pointerId)
                    doCustomDyInternal(dy, pointerId)
                    saveLastMotion(x, y, pointerId)

                    val ddx = x - (initialMotionX[pointerId] ?: 0f)
                    val ddy = y - (initialMotionY[pointerId] ?: 0f)
                    if (dragState == STATE_IDLE && hypot(ddx, ddy) > touchSlop) {
                        dragState = STATE_DRAGGING
                    }
                }
                callback.onFrameChange(selectedFrame)
            }
            MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                val index = if (action == MotionEvent.ACTION_UP)
                    0 else ev.actionIndex
                val pointerId = ev.getPointerId(index)
                clearMotionHistory(pointerId)
                if (action == MotionEvent.ACTION_UP && dragState == STATE_DRAGGING) {
                    callback.onFrameRelease(selectedFrame)
                }
                dragState = STATE_IDLE
            }
            MotionEvent.ACTION_CANCEL -> {
                dragState = STATE_IDLE
                clearMotionHistory()
            }
        }
        return true
    }

    private fun doCustomDxInternal(dx: Float, pointerId: Int) {
        when {
            leftTopEdges.contains(pointerId) || leftBottomEdges.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left + dx, selectedFrame.top,
                    selectedFrame.right, selectedFrame.bottom), dx, 0f)
            }
            rightTopEdges.contains(pointerId) || rightBottomEdges.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left, selectedFrame.top,
                    selectedFrame.right + dx, selectedFrame.bottom), dx, 0f)
            }
            locations.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left + dx, selectedFrame.top,
                    selectedFrame.right + dx, selectedFrame.bottom), dx, 0f)
            }
        }
    }
    private fun doCustomDyInternal(dy: Float, pointerId: Int) {
        when {
            leftTopEdges.contains(pointerId) || rightTopEdges.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left, selectedFrame.top + dy,
                    selectedFrame.right, selectedFrame.bottom), 0f, dy)
            }

            leftBottomEdges.contains(pointerId) || rightBottomEdges.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left, selectedFrame.top,
                    selectedFrame.right, selectedFrame.bottom + dy), 0f, dy)
            }
            locations.contains(pointerId) -> {
                kickCoordinatorAreaInRangeByDrag(RectF(selectedFrame.left, selectedFrame.top + dy,
                    selectedFrame.right, selectedFrame.bottom + dy), 0f, dy)
            }
        }
    }

    private fun kickCoordinatorAreaInRangeByDrag(coordinatorArea: RectF, dx: Float, dy: Float): Boolean {
        if (coordinatorArea.bottom - coordinatorArea.top < minFrame.height()) {
            if (dy > 0) {
                coordinatorArea.bottom = coordinatorArea.top + minFrame.height()
            } else {
                coordinatorArea.top = coordinatorArea.bottom - minFrame.height()
            }
        }
        if (coordinatorArea.right - coordinatorArea.left < minFrame.width()) {
            if (dx > 0) {
                coordinatorArea.right = coordinatorArea.left + minFrame.width()
            } else {
                coordinatorArea.left = coordinatorArea.right - minFrame.width()
            }
        }
        if (maxFrame.contains(coordinatorArea)) {
            selectedFrame.set(coordinatorArea)
            return true
        }
        return false
    }
    
    private fun saveInitialMotion(x: Float, y: Float, pointerId: Int) {
        ensurePointerForLocation(x, y, pointerId)
        lastMotionX[pointerId] = x
        lastMotionY[pointerId] = y
        initialMotionX[pointerId] = x
        initialMotionY[pointerId] = y
        pointersDown = pointersDown or (1 shl pointerId)
    }

    private fun saveLastMotion(x: Float, y: Float, pointerId: Int) {
        if (!isPointerDown(pointerId)) {
            return
        }
        if (lastMotionX.containsKey(pointerId)) {
            lastMotionX[pointerId] = x
        }
        if (lastMotionY.containsKey(pointerId)) {
            lastMotionY[pointerId] = y
        }
    }

    private fun ensurePointerForLocation(x: Float, y: Float, pointerId: Int): Boolean {
        dragEdgeAreas.forEachIndexed { index, rectF ->
            if (rectF.contains(x, y)) {
                when(index) {
                    LEFT_TOP -> leftTopEdges.add(pointerId)
                    RIGHT_TOP -> rightTopEdges.add(pointerId)
                    RIGHT_BOTTOM -> rightBottomEdges.add(pointerId)
                    LEFT_BOTTOM -> leftBottomEdges.add(pointerId)
                }
                return true
            }
        }
        dragLocateAreas.forEachIndexed { _, rectF ->
            if (rectF.contains(x, y)) {
                locations.add(pointerId)
                return true
            }
        }
        return false
    }

    private fun isPointerInDragArea(x: Float, y: Float): Boolean {
        dragEdgeAreas.forEach {
            if (it.contains(x, y)) {
                return true
            }
        }
        dragLocateAreas.forEach {
            if (it.contains(x, y)) {
                return true
            }
        }
        return false
    }

    private fun isPointerInPointArea(x: Float, y: Float): Boolean {
        pointLocateAreas.forEach {
            if (it.contains(x, y))
                return true
        }
        return false
    }

    private fun clearMotionHistory(pointerId: Int) {
        if (initialMotionX.isEmpty() || !isPointerDown(pointerId)) {
            return
        }
        initialMotionX[pointerId] = 0f
        initialMotionY[pointerId] = 0f
        lastMotionX[pointerId] = 0f
        lastMotionY[pointerId] = 0f
        leftTopEdges.remove(pointerId)
        rightTopEdges.remove(pointerId)
        rightBottomEdges.remove(pointerId)
        leftBottomEdges.remove(pointerId)
        pointersDown = pointersDown and (1 shl pointerId).inv()
    }

    private fun clearMotionHistory() {
        if (initialMotionX.isEmpty()) return
        initialMotionX.clear()
        initialMotionY.clear()
        lastMotionX.clear()
        lastMotionY.clear()
        leftTopEdges.clear()
        rightTopEdges.clear()
        rightBottomEdges.clear()
        leftBottomEdges.clear()
        pointersDown = 0
    }

    private fun cancel(){
        activePointerId = ViewDragHelper.INVALID_POINTER
        clearMotionHistory()
//        velocityTracker.recycle()
    }

    private fun isPointerDown(pointerId: Int): Boolean {
        return pointersDown and 1 shl pointerId != 0
    }

    private fun addMovement(event: MotionEvent) {
//        val deltaX = event.rawX - event.x
//        val deltaY = event.rawY - event.y
//        event.offsetLocation(deltaX, deltaY)
//        velocityTracker.addMovement(event)
//        event.offsetLocation(-deltaX, -deltaY)
    }


    private var dragEdgeAreas: List<RectF> = arrayListOf()
    private var dragLocateAreas: List<RectF> = arrayListOf()
    private var pointLocateAreas: List<RectF> = arrayListOf()
    override fun setDragEdgeAreas(areas: List<RectF>) {
        dragEdgeAreas = areas
    }

    override fun setDragLocateAreas(areas: List<RectF>) {
        dragLocateAreas = areas
    }

    override fun setPointLocateAreas(areas: List<RectF>) {
        pointLocateAreas = areas
    }
}