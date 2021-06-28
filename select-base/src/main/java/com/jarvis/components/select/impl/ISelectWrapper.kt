package com.jarvis.components.select.impl

import android.view.MotionEvent
import com.jarvis.components.select.data.FrameData
import com.jarvis.components.select.frame.IFrameChangeListener

/**
 * @author: yyf
 * @date: 2021/6/15
 * @desc:
 */
interface ISelectWrapper {
   fun buildFrames(data: List<FrameData>)

   fun doShowFrame(frame: FrameData, hasAnim: Boolean)

   fun clearFrames()

   fun setBounds(left: Int, top: Int, right: Int, bottom: Int)

   fun onTouchEvent(ev: MotionEvent?): Boolean

   fun registerFrameChangeListener(listener: IFrameChangeListener)

   fun doInternalInvalidate()
}