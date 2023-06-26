package com.vance.musiccharts.extension

import android.graphics.Bitmap
import android.graphics.Canvas

fun Canvas.drawBitmap(bitmap: Bitmap) = drawBitmap(bitmap, 0f, 0f, null)