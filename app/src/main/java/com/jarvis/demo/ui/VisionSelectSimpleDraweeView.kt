package com.jarvis.demo.ui

import android.content.Context
import android.util.AttributeSet
import com.jarvis.components.fresco.SelectSimpleDraweeView
import com.jarvis.demo.data.VisionBoxInfo

/**
 * @author: yyf
 * @date: 2021/6/16
 * @desc:
 */
class VisionSelectSimpleDraweeView: SelectSimpleDraweeView<VisionBoxInfo> {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}