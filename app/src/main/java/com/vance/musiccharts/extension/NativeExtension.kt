package com.vance.musiccharts

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.core.graphics.ColorUtils
import kotlin.math.roundToInt

private val density: Float get() = MainActivity.getInstance().resources.displayMetrics.density

/*
    Int
 */

val Int.dp get() = if (this == 0) 0 else (this * density).roundToInt()

/*
    Int (color)
 */

fun Int.mixWith(
    @ColorInt color: Int,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float
): Int = ColorUtils.blendARGB(this, color, ratio)

fun Int.alpha(@FloatRange(from = 0.0, to = 1.0) value: Float) = ColorUtils.setAlphaComponent(this, (255 * value).toInt())

/*
    Float
 */

val Float.dp get() = if (this == 0f) 0f else this * density

val Float.px get() = if (this == 0f) 0f else this / density

/*
    Boolean
 */

fun Boolean.asFloat() = if (this) 1f else 0f

/*
    IntArray
 */

fun IntArray.asShuffled(): IntArray = apply { shuffle() }