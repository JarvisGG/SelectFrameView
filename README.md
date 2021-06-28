![Platform](https://img.shields.io/badge/platform-android-blue.svg)
![SDK](https://img.shields.io/badge/SDK-17%2B-blue.svg)
[![](https://img.shields.io/badge/Author-JarvisGG-7AD6FD.svg)](http:\//jarvisgg.github.io/)

SelectFrameView
==================

模仿手淘自动识别 UI 选择控件

Name | Display
--- | ---
SelectSimpleDraweeView | <img src="https://github.com/JarvisGG/SelectFrameView/blob/develop/capture/select_frame.gif?raw=true">

### 用法

``` kotlin
class VisionSelectSimpleDraweeView: SelectSimpleDraweeView<VisionBoxInfo> {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
```

``` xml
<com.jarvis.demo.ui.VisionSelectSimpleDraweeView
    android:id="@+id/js_view"
    android:layout_width="0dp"
    android:layout_height="0dp"
    app:layout_constraintTop_toTopOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent" />
```

``` kotlin
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
}

/** 选择空焦点 frame */
val unFocus = findViewById<Button>(R.id.append_unfocus)
unFocus.setOnClickListener {
    selectView.showFrameData(VisionBoxInfo(0.7f, 0.75f, 0.4f, 0.4f))
}
```

### 引入
方式 1:
``` Gradle
repositories {
    // ...
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/JarvisGG/SelectFrameView")
        credentials {
            username = "JarvisGG"
            password = "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"
        }
    }
}

dependencies {
    implementation Libs.AndroidX.dynamicanimation // 目前 demo 依赖管理采用 buildSrc
    implementation "com.jarvis.libraries:select-base:1.0.0"
    // 如果需要 fresco 扩展，请添加
    implementation Libs.Fresco.core
    implementation "com.jarvis.libraries:select-fresco:1.0.0"
}
```
方式 2:
``` Gradle
repositories {
    maven { url "https://jitpack.io" }
}
dependencies {
    implementation 'com.github.JarvisGG:SelectFrameView:1.0.4'
}
```


