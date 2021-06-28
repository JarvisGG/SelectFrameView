package com.jarvis.demo

import android.app.Application
import com.facebook.drawee.backends.pipeline.Fresco

/**
 * @author: yyf
 * @date: 2021/6/28
 * @desc:
 */
class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
    }
}