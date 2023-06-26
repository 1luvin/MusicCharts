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
import com.github.luvin1.android.utils.Layout
import com.google.android.material.chip.ChipGroup
import com.vance.musiccharts.alpha
import com.vance.musiccharts.asShuffled
import com.vance.musiccharts.dp
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.heightF
import com.vance.musiccharts.util.Constant
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Theme
import com.vance.musiccharts.widthF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("ViewConstructor")
class BarChartView(
    context: Context,
    title: String,
    subtitle: String,
    searchHint: String,
    onSearch: (ChartView, String) -> Unit
) : ChartView(context, title, subtitle, searchHint, onSearch) {

    private val chart: View
    private val itemsCheckGroup: ChipGroup
    private val itemChecks: MutableList<ChartItemView> = mutableListOf()

    private val BAR_HEIGHT: Int = 250.dp
    private val LINES: Int = 6

    private val values: MutableList<Float> = MutableList(LINES) { 0f }
    private val animValues: MutableList<Float> = values.toMutableList()
    private var valuesTranslateX: Float = 0f
    private var rects: List<RectF> = List(Constant.N) { RectF() }
    private val animRects: MutableList<RectF> = rects.toMutableList()

    private val rectPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var animator: ValueAnimator? = null

    private val itemColors: List<Int> = colors.asShuffled().take(Constant.N)

    private var textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        typeface = Font.Regular
        color = Theme.color(Theme.color_text)
    }

    private val linePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 1f.dp
        color = Theme.color(Theme.color_text2).alpha(0.3f)
    }

    init {
        chart = object : View(context) {

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                super.onMeasure(
                    MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(BAR_HEIGHT, MeasureSpec.EXACTLY)
                )

                textPaint.textSize = measuredHeight / LINES.toFloat() * 0.37f
            }

            override fun draw(canvas: Canvas) {
                super.draw(canvas)

                val h = heightF / LINES
                var t = h
                repeat(LINES) {
                    canvas.apply {
                        drawText("%.1f".format(animValues[it]), 0f, t - h * 0.2f, textPaint)
                        drawLine(0f, t, widthF, t, linePaint)
                    }
                    t += h
                }

                canvas.translate(valuesTranslateX + indent.dp, h)
                animRects.forEachIndexed { i, rect ->
                    rectPaint.color = itemColors[i]
                    canvas.drawRect(rect, rectPaint)
                }
            }
        }
        addView(
            chart, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, 0, indent, 0
            )
        )

        itemsCheckGroup = ChipGroup(context).apply {
            chipSpacingHorizontal = 6.dp
            chipSpacingVertical = 6.dp
        }
        addView(
            itemsCheckGroup, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, indent, indent, indent
            )
        )
    }

    override fun updateChart(data: Map<String, Number>) {
        super.updateChart(data)

        val sortedData = data.toList().sortedBy { (_, value) -> -value.toFloat() }.toMap()
        val itemNames = sortedData.keys.take(7)
        val itemValues = sortedData.values.map { it.toFloat() }.take(7)

        itemsCheckGroup.removeAllViewsInLayout()
        itemChecks.clear()

        itemNames.forEachIndexed { index, name ->
            ChartItemView(
                context,
                bgColor = itemColors[index],
                text = name
            ).apply {
                setOnClickListener {
                    if (!isChecked || (isChecked && itemChecks.count { it.isChecked } > 2)) {
                        setChecked(checked = !isChecked, animated = true)
                        redrawChart(itemValues)
                    }
                }
            }.also {
                itemsCheckGroup.addView(it)
                itemChecks.add(it)
            }
        }

        redrawChart(itemValues)
    }

    private fun redrawChart(itemValues: List<Float>) {
        val checkedValues = itemValues.filterIndexed { i, _ -> itemChecks[i].isChecked }
        val max = checkedValues.maxOf { it }
        val min = checkedValues.minOf { it }
        val heightPercents = checkedValues.map { (it - min) * (1f - 0.2f) / (max - min) + 0.2f }

        val fromValues: List<Float>
        val fromRects: List<RectF>
        if (animator != null) {
            animator!!.cancel()
            fromValues = animValues.toList()
            fromRects = animRects.map { RectF(it) }
        } else {
            fromValues = values.toList()
            fromRects = rects
        }

        var tmp = (max - min) / (LINES - 2)
        var tmp2 = max
        repeat(LINES - 1) {
            values[it] = tmp2
            tmp2 -= tmp
        }

        valuesTranslateX = values.maxOf {
            val t = "%.1f".format(it)
            textPaint.measureText(t)
        }

        val barWidth = (chart.width - (valuesTranslateX + indent.dp)) / checkedValues.size
        val lineHeight = BAR_HEIGHT - BAR_HEIGHT / LINES.toFloat()

        var ii = 0
        tmp = 0f
        rects = itemValues.mapIndexed { i, v ->
            if (itemChecks[i].isChecked) {
                val rect = RectF(tmp, lineHeight * (1 - heightPercents[ii++]), tmp + barWidth, lineHeight)
                tmp += barWidth
                rect
            } else {
                RectF(tmp, lineHeight, tmp, lineHeight)
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

                for (i in 0 until LINES) {
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