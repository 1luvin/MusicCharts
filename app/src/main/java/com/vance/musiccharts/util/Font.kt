package com.vance.musiccharts.util

import android.graphics.Typeface
import com.vance.musiccharts.MainActivity

object Font {

    private val fonts: HashMap<String, Typeface> = hashMapOf()

    private fun font(style: String): Typeface {
        if (!fonts.containsKey(style)) {
            fonts[style] = Typeface.createFromAsset(
                MainActivity.getInstance().assets, "font/Nunito$style.ttf"
            )
        }

        return fonts[style]!!
    }

    val Regular: Typeface get() = font("Regular")
    val Medium: Typeface get() = font("Medium")
    val SemiBold: Typeface get() = font("SemiBold")
    val Bold: Typeface get() = font("Bold")
}