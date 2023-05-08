package com.vance.musiccharts.util

import android.graphics.Color

object Theme {

    const val color_bg: String = "color_bg"
    const val color_bg2: String = "color_bg2"
    const val color_text: String = "color_text"
    const val color_text2: String = "color_text2"

    private val colors: HashMap<String, Int> = hashMapOf()

    init {
        colors.apply {
            put(color_bg, 0xFF111726.toInt())
            put(color_bg2, 0xFF363857.toInt())
            put(color_text, Color.WHITE)
            put(color_text2, 0xFF717599.toInt())
        }
    }

    fun color(key: String): Int = colors[key] ?: Color.BLACK
}