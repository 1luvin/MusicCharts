package com.vance.musiccharts.chart

import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan

class FontSpan(private val font: Typeface) : TypefaceSpan("") {

    override fun updateDrawState(paint: TextPaint) {
        applyFont(paint)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyFont(paint)
    }

    private fun applyFont(paint: Paint) {
        val oldStyle = paint.typeface?.style ?: 0

        val fake = oldStyle and font.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.isFakeBoldText = true
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.textSkewX = -0.25f
        }

        paint.typeface = font
    }
}