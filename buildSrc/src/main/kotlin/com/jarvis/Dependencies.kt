package com.jarvis

/**
 * @author: yyf
 * @date: 2021/6/28
 * @desc:
 */
object Versions {
    const val compileSdk = 30
    const val minSdk = 17
    const val targetSdk = 29
    const val buildTools = "30.0.3"
    const val kotlin = "1.3.72"
}

object Libs {
    const val agp="com.android.tools.build:gradle:4.2.0"

    object Kotlin {
        private const val version = Versions.kotlin
        const val kgp = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:$version"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:$version"
        const val ext_runtime = "org.jetbrains.kotlin:kotlin-android-extensions-runtime:$version"
    }


    object Fresco {
        const val core = "com.facebook.fresco:fresco:0.12.0"
    }

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0"
        const val dynamicanimation = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha03"
    }

    object Google {
        const val truth = "com.google.truth:truth:1.0.1"
        const val gson = "com.google.code.gson:gson:2.8.5"
        const val autoService = "com.google.auto.service:auto-service:1.0-rc4"
        const val material = "com.google.android.material:material:1.3.0"
        const val flexbox = "com.google.android:flexbox:2.0.1"
    }

    object Test {
        private const val version = "1.2.0"
        const val runner = "androidx.test:runner:$version"
        const val rules = "androidx.test:rules:$version"
        const val ext = "androidx.test.ext:junit:1.1.2"
        const val espressoCore = "androidx.test.espresso:espresso-core:3.3.0"
    }

}