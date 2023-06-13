package com.vance.musiccharts.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import com.vance.musiccharts.dp
import com.vance.musiccharts.heightF
import com.vance.musiccharts.setTextSizeDp
import com.vance.musiccharts.util.Font

@SuppressLint("ViewConstructor")
class PieChartItemView(
    context: Context,
    bgColor: Int,
    text: String,
    value: Float
) : AppCompatTextView(context) {

    private val fillRect: RectF = RectF()
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bgColor
    }

    init {
        setWillNotDraw(false)

        setPadding(12.dp, 0, 12.dp, 0)
        setTextColor(Color.WHITE)
        setTextSizeDp(16)
        typeface = Font.Regular
        isSingleLine = true
        ellipsize = TextUtils.TruncateAt.MIDDLE
        gravity = Gravity.CENTER_VERTICAL

        val l = text.length + 1
        val t = "$text ${"%.2f".format(value)}"
        this.text = SpannableString(t).apply {
            setSpan(
                FontSpan(Font.Bold),
                l, t.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(36.dp, MeasureSpec.EXACTLY)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        fillRect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        val c = heightF / 2
        canvas.drawRoundRect(fillRect, c, c, fillPaint)

        super.onDraw(canvas)
    }
}