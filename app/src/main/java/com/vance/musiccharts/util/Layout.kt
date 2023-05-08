package com.vance.musiccharts.util

import android.widget.FrameLayout
import android.widget.LinearLayout
import com.vance.musiccharts.dp

object Layout {

    const val MATCH_PARENT: Int = -1
    const val WRAP_CONTENT: Int = -2

    private fun size(size: Int): Int = if (size < 0) size else size.dp

    /*
        FrameLayout
     */

    fun ezFrame(
        width: Int, height: Int,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): FrameLayout.LayoutParams {
        return frame(
            size(width), size(height),
            gravity,
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezFrame(
        width: Int, height: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): FrameLayout.LayoutParams {
        return frame(
            size(width), size(height),
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezFrame(width: Int, height: Int, gravity: Int): FrameLayout.LayoutParams {
        return frame(size(width), size(height), gravity)
    }

    fun ezFrame(width: Int, height: Int): FrameLayout.LayoutParams {
        return frame(size(width), size(height))
    }

    fun frame(
        width: Int, height: Int,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): FrameLayout.LayoutParams {
        return frame(width, height, leftMargin, topMargin, rightMargin, bottomMargin).apply {
            this.gravity = gravity
        }
    }

    fun frame(
        width: Int, height: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): FrameLayout.LayoutParams {
        return frame(width, height).apply {
            setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        }
    }

    fun frame(width: Int, height: Int, gravity: Int): FrameLayout.LayoutParams {
        return frame(width, height).apply {
            this.gravity = gravity
        }
    }

    fun frame(width: Int, height: Int): FrameLayout.LayoutParams {
        return FrameLayout.LayoutParams(width, height)
    }

    /*
        LinearLayout
     */

    fun ezLinear(
        width: Int, height: Int,
        weight: Float,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            size(width), size(height),
            weight,
            gravity,
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezLinear(
        width: Int, height: Int,
        weight: Float,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            size(width), size(height),
            weight,
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezLinear(
        width: Int, height: Int,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            size(width), size(height),
            gravity,
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezLinear(
        width: Int, height: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            size(width), size(height),
            size(leftMargin), size(topMargin), size(rightMargin), size(bottomMargin)
        )
    }

    fun ezLinear(
        width: Int, height: Int,
        weight: Float,
        gravity: Int
    ): LinearLayout.LayoutParams {
        return linear(size(width), size(height), weight, gravity)
    }

    fun ezLinear(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return linear(size(width), size(height), weight)
    }

    fun ezLinear(width: Int, height: Int, gravity: Int): LinearLayout.LayoutParams {
        return linear(size(width), size(height), gravity)
    }

    fun ezLinear(width: Int, height: Int): LinearLayout.LayoutParams {
        return linear(size(width), size(height))
    }

    fun linear(
        width: Int, height: Int,
        weight: Float,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            width, height, weight, leftMargin, topMargin, rightMargin, bottomMargin
        ).apply {
            this.gravity = gravity
        }
    }

    fun linear(
        width: Int, height: Int,
        weight: Float,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            width, height, leftMargin, topMargin, rightMargin, bottomMargin
        ).apply {
            this.weight = weight
        }
    }

    fun linear(
        width: Int, height: Int,
        gravity: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(
            width, height, leftMargin, topMargin, rightMargin, bottomMargin
        ).apply {
            this.gravity = gravity
        }
    }

    fun linear(
        width: Int, height: Int,
        leftMargin: Int, topMargin: Int, rightMargin: Int, bottomMargin: Int
    ): LinearLayout.LayoutParams {
        return linear(width, height).apply {
            setMargins(leftMargin, topMargin, rightMargin, bottomMargin)
        }
    }

    fun linear(
        width: Int, height: Int,
        weight: Float,
        gravity: Int
    ): LinearLayout.LayoutParams {
        return linear(width, height, weight).apply {
            this.gravity = gravity
        }
    }

    fun linear(width: Int, height: Int, weight: Float): LinearLayout.LayoutParams {
        return linear(width, height).apply {
            this.weight = weight
        }
    }

    fun linear(width: Int, height: Int, gravity: Int): LinearLayout.LayoutParams {
        return linear(width, height).apply {
            this.gravity = gravity
        }
    }

    fun linear(width: Int, height: Int): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(width, height)
    }
}