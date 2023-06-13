package com.vance.musiccharts.util

object Theme {

    const val color_bg: String = "color_bg"
    const val color_bg2: String = "color_bg2"
    const val color_text: String = "color_text"
    const val color_text2: String = "color_text2"

    private val colors: Map<String, Int> = mapOf(
        color_bg to 0xFF111726.toInt(),
        color_bg2 to 0xFF121E39.toInt(),
        color_text to 0xFFFFFFFF.toInt(),
        color_text2 to 0xFF717599.toInt()
    )

    fun color(key: String): Int = colors[key] ?: 0xFF000000.toInt()
}