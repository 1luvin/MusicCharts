package com.vance.musiccharts.util

object Theme {

    private var count: Byte = 0
    val color_bg: Byte = count++
    val color_bg2: Byte = count++
    val color_text: Byte = count++
    val color_text2: Byte = count++
    val color_positive: Byte = count++
    val color_negative: Byte = count++
    val color_loading: Byte = count++

    private val colors: Map<Byte, Int> = mapOf(
        color_bg to 0xFF111726.toInt(),
        color_bg2 to 0xFF121E39.toInt(),
        color_text to 0xFFFFFFFF.toInt(),
        color_text2 to 0xFF717599.toInt(),
        color_positive to 0xFF00D662.toInt(),
        color_negative to 0xFFFF0000.toInt(),
        color_loading to 0xFFF8C915.toInt(),
    )

    fun color(key: Byte): Int = colors[key] ?: 0xFF000000.toInt()
}