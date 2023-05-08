package com.vance.musiccharts

import android.util.TypedValue
import android.view.View
import android.widget.TextView

/*
    View
 */

val View.widthF get() = width.toFloat()

val View.heightF get() = height.toFloat()

/*
    TextView
 */

fun TextView.setTextSizeDp(value: Float) = setTextSize(TypedValue.COMPLEX_UNIT_DIP, value)

fun TextView.setTextSizeDp(value: Int) = setTextSizeDp(value.toFloat())