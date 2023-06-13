package com.vance.musiccharts.chart

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.chip.ChipGroup
import com.vance.musiccharts.alpha
import com.vance.musiccharts.asShuffled
import com.vance.musiccharts.dp
import com.vance.musiccharts.heightF
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Layout
import com.vance.musiccharts.util.Theme
import com.vance.musiccharts.widthF

@SuppressLint("ViewConstructor")
class BarChartView(
    context: Context,
    title: String,
    subtitle: String,
    searchHint: String,
    onSearch: (ChartView, String) -> Unit
) : ChartView(context, title, subtitle, searchHint, onSearch) {

    companion object {

        private val barHeight: Int = 250.dp
        private const val linesSize: Int = 6
    }

    private val chart: View
    private val itemsCheckGroup: ChipGroup
    private val itemChecks: MutableList<ChartItemView> = mutableListOf()

    private var itemValues: List<Float> = listOf()

    private var values: MutableList<Float> = MutableList(linesSize) { 0f }
    private var animValues: MutableList<Float> = values.toMutableList()
    private var rects: List<RectF> = List(7) { RectF() }
    private var animRects: List<RectF> = rects.toList()

    private val rectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null
    private var rectPercents: List<Float> = listOf()

    private val itemColors: List<Int> = colors.asShuffled().take(7)

    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Font.Regular
        color = Theme.color(Theme.color_text)
    }

    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp
        color = Theme.color(Theme.color_text2).alpha(0.3f)
    }

    private var valuesTranslateX: Float = 0f

    init {
        chart = object : View(context) {

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(barHeight, MeasureSpec.EXACTLY)
                )
            }

            override fun draw(canvas: Canvas) {
                super.draw(canvas)

                val h = heightF / linesSize
                if (animValues.isNotEmpty()) {
                    textPaint.textSize = h * 0.37f
                    var t = h
                    repeat(linesSize) {
                        canvas.apply {
                            drawText("%.1f".format(animValues[it]), 0f, t - h * 0.2f, textPaint)
                            drawLine(0f, t, widthF, t, linePaint)
                        }
                        t += h
                    }
                }

                canvas.apply {
                    save()
                    translate(valuesTranslateX + indent.dp, h)
                }
                animRects.forEachIndexed { i, rect ->
                    rectPaint.color = itemColors[i]
                    canvas.drawRect(rect, rectPaint)
                }
                canvas.restore()
            }
        }
        addView(
            chart, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, 0, indent, 0
            )
        )

        itemsCheckGroup = ChipGroup(context).apply {
            chipSpacingHorizontal = 6.dp
            chipSpacingVertical = 6.dp
        }
        addView(
            itemsCheckGroup, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, indent, indent, indent
            )
        )
    }

    override fun updateChart(itemNames: List<String>, itemValues: List<Float>) {
        super.updateChart(itemNames, itemValues)

        itemsCheckGroup.removeAllViewsInLayout()
        itemChecks.clear()

        this.itemValues = itemValues

        itemNames.forEachIndexed { index, name ->
            ChartItemView(
                context,
                bgColor = itemColors[index],
                text = name
            ).apply {
                setOnClickListener {
                    if (!isChecked || (isChecked && itemChecks.count { it.isChecked } > 2)) {
                        setChecked(!isChecked, animated = true)
                        redrawChart()
                    }
                }
            }.also {
                itemsCheckGroup.addView(it)
                itemChecks.add(it)
            }
        }

        redrawChart()
    }

    private fun redrawChart() {
        val Values = itemValues.filterIndexed { i, _ -> itemChecks[i].isChecked }
        val max = Values.maxOf { it }
        val min = Values.minOf { it }
        val heightPercents = Values.map { (it - min) * (1f - 0.2f) / (max - min) + 0.2f }

        var fromValues: List<Float> = listOf()
        var fromRects: List<RectF> = listOf()
        animator?.let {
            it.cancel()
            fromValues = animValues.toList()
            fromRects = animRects.toList()
        } ?: run {
            fromValues = values.toList()
            fromRects = rects.toList()
        }

        val xx = (max - min) / (linesSize - 2)
        var p = max
        repeat(linesSize - 1) {
            values[it] = p
            p -= xx
        }

        valuesTranslateX = values.maxOf {
            val t = "%.1f".format(it)
            textPaint.measureText(t)
        }
        val w = (chart.width - (valuesTranslateX + indent.dp)) / Values.size

        val Height = barHeight - barHeight.toFloat() / linesSize

        var ii = 0
        var left = 0f
        rects = itemValues.mapIndexed { i, v ->
            if (itemChecks[i].isChecked) {
                val rect = RectF(left, Height * (1 - heightPercents[ii++]), left + w, Height)
                left += w
                rect
            } else {
                RectF(left, Height, left, Height)
            }
        }

        ii = 0
        val sum = Values.sum()
        rectPercents = itemValues.mapIndexed { i, v ->
            if (itemChecks[i].isChecked) {
                Values[ii++] / sum
            } else {
                0f
            }
        }

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 250
            interpolator = DecelerateInterpolator()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    animator = null
                }
            })

            addUpdateListener {
                val v = it.animatedValue as Float

                for (i in 0 until linesSize) {
                    animValues[i] = fromValues[i] + (this@BarChartView.values[i] - fromValues[i]) * v
                }

                for (i in animRects.indices) {
                    animRects[i].set(
                        fromRects[i].left + (rects[i].left - fromRects[i].left) * v,
                        fromRects[i].top + (rects[i].top - fromRects[i].top) * v,
                        fromRects[i].right + (rects[i].right - fromRects[i].right) * v,
                        fromRects[i].bottom + (rects[i].bottom - fromRects[i].bottom) * v
                    )
                }

                chart.invalidate()
            }
        }.also { it.start() }
    }
}