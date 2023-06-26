package com.vance.musiccharts.chart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.graphics.createBitmap
import com.github.luvin1.android.utils.Layout
import com.vance.musiccharts.alpha
import com.vance.musiccharts.dp
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.extension.drawBitmap
import com.vance.musiccharts.heightF
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Theme
import com.vance.musiccharts.widthF
import kotlin.math.max
import kotlin.math.min

@SuppressLint("ViewConstructor")
class LineChartView(
    context: Context,
    title: String,
    subtitle: String,
    searchHint: String,
    onSearch: (ChartView, String) -> Unit
) : ChartView(context, title, subtitle, searchHint, onSearch) {

    private val chart: View

    private var valuesPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Font.Regular
        color = Theme.color(Theme.color_text)
    }

    private val bgLinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp
        color = Theme.color(Theme.color_text2).alpha(0.3f)
    }

    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f.dp
        strokeCap = Paint.Cap.ROUND
        color = Theme.color(Theme.color_positive)
    }

    private val pointPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 7f.dp
        strokeCap = Paint.Cap.ROUND
        color = Theme.color(Theme.color_text)
    }

    private var lastBitmap: Bitmap? = null
    private var bitmap: Bitmap? = null
    private var clipX: Float = 0f
    private var valuesTranslateX: Float = 0f

    init {
        chart = object : View(context) {

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(200.dp, MeasureSpec.EXACTLY),
                )
            }

            override fun draw(canvas: Canvas) {
                super.draw(canvas)

                lastBitmap?.let {
                    canvas.apply {
                        save()
                        clipRect(clipX, 0f, widthF, heightF)
                        drawBitmap(it)
                    }
                }

                bitmap?.let {
                    canvas.apply {
                        if (saveCount > 1) restore()
                        clipRect(0f, 0f, clipX, heightF)
                        drawBitmap(it)
                    }
                }
            }
        }
        addView(
            chart, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, 0, indent, indent
            )
        )
    }

    fun updateLineChart(years: List<Int>, releases: List<Int>) {
        searchCell.stopLoading()

        // Computing

        val maxReleases = releases.maxOf { it }
        val minReleases = releases.minOf { it }

        val lines = 5
        var tmp = maxReleases.toFloat()
        val diff = (maxReleases - minReleases) / (lines - 1f)
        val values = MutableList(lines) { 0f }
        valuesPaint.textSize = chart.height / lines.toFloat() * 0.37f
        repeat(lines) {
            values[it] = tmp
            tmp -= diff
        }

        valuesTranslateX = values.maxOf { valuesPaint.measureText("%.1f".format(it)) }

        val availableHeight = chart.height - chart.height / lines.toFloat()

        var tmp2 = valuesTranslateX + indent.dp * 2
        val availableWidth = (chart.width - tmp2 - indent.dp) / releases.size
        val points = releases.map {
            val p = (it - minReleases) / (maxReleases - minReleases + 0f)
            val point = PointF(tmp2, availableHeight - availableHeight * p)
            tmp2 += availableWidth
            point
        }

        // Drawing

        lastBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, false)
        bitmap?.recycle()
        bitmap = createBitmap(chart.width, chart.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap!!)

        val h = chart.heightF / lines
        var t = h
        repeat(lines) {
            canvas.apply {
                drawText("%.1f".format(values[it]), 0f, t - h * 0.2f, valuesPaint)
                drawLine(0f, t, widthF, t, bgLinePaint)
            }
            t += h
        }

        canvas.translate(0f, chart.heightF / lines)

        for (i in 0..(points.size - 2)) { //
            val fromPoint = points[i]
            val toPoint = points[i + 1]
            val colorKey = if (toPoint.y <= fromPoint.y) Theme.color_positive else Theme.color_negative
            linePaint.color = Theme.color(colorKey)
            canvas.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y, linePaint)
            canvas.drawPoint(fromPoint.x, fromPoint.y, pointPaint)
            canvas.drawPoint(toPoint.x, toPoint.y, pointPaint)
        }

        // Animating

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()

            addUpdateListener {
                val v = it.animatedValue as Float
                clipX = chart.widthF * v
                chart.invalidate()
            }
        }.also { it.start() }
    }
}