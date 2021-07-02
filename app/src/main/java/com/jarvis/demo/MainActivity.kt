package com.jarvis.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.jarvis.components.fresco.FrameChangeWrapper
import com.jarvis.components.select.impl.ISelectView
import com.jarvis.demo.data.VisionBoxInfo
import com.jarvis.demo.transform.VisionInfoTransform
import com.jarvis.demo.ui.VisionSelectSimpleDraweeView
import com.jarvis.selectframeview.R

/**
 * @author: yyf
 * @date: 2021/6/28
 * @desc:
 */
class MainActivity: AppCompatActivity() {

    private val transform by lazy {
        VisionInfoTransform()
    }

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    private lateinit var selectView: VisionSelectSimpleDraweeView

    private val frameCallback = FrameChangeWrapper(object : ISelectView.ISelectFrameChange<VisionBoxInfo> {
        override fun onFrameChange(data: VisionBoxInfo) {
        }
        /** isAutoFocus 是否要保留点 */
        override fun onFrameRelease(data: VisionBoxInfo, isAutoFocus: Boolean) {
//            if (isAutoFocus) {
//                selectView.showFrameData(VisionBoxInfo(data.centerX,data.centerY,data.width,data.height), false)
//            }
        }

        override fun onPointTouchDown(data: VisionBoxInfo) {
            selectView.showFrameData(VisionBoxInfo(data.centerX,data.centerY,data.width,data.height))
        }
    }, transform)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.container_picture_select)

        setup()
    }

    private fun setup() {
        selectView = findViewById(R.id.js_view)
        selectView.setImageURI("https://inews.gtimg.com/newsapp_bt/0/13560755311/641")
        selectView.registerDataTransform(transform)
        selectView.registerFrameChangeListener(frameCallback)
        val focusList = arrayListOf(
            VisionBoxInfo(0.43f, 0.3f, 0.3f, 0.5f),
            VisionBoxInfo(0.5f, 0.5f, 0.5f, 0.5f)
        )
        selectView.setFrameData(focusList)
        selectView.showFrameData(focusList[0])

        /** 刷新数据 */
        val refresh = findViewById<Button>(R.id.refresh)
        refresh.setOnClickListener {
            selectView.setFrameData(arrayListOf(
                VisionBoxInfo(0.58f, 0.525f, 0.33f, 0.21f),
                VisionBoxInfo(0.28f, 0.48f, 0.53f, 0.51f)
            ))
        }

        /** 选择一个焦点 frame */
        val focus = findViewById<Button>(R.id.append_focus)
        focus.setOnClickListener {
            selectView.showFrameData(focusList[1])
//            handler.postDelayed({
//                selectView.showFrameData(focusList[0])
//            }, 300)
//            handler.postDelayed({
//                selectView.showFrameData(focusList[1])
//            }, 1400)
        }

        /** 选择空焦点 frame */
        val unFocus = findViewById<Button>(R.id.append_unfocus)
        unFocus.setOnClickListener {
            selectView.showFrameData(VisionBoxInfo(0.7f, 0.75f, 0.4f, 0.4f))
        }

        val scaleSeekBar = findViewById<SeekBar>(R.id.seekbar_scale)
        scaleSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val scale = progress * 1f / 100
                selectView.doScaleChange(scale)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }
}