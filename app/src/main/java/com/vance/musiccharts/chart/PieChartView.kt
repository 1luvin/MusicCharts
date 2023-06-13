package com.vance.musiccharts.chart

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.view.forEachIndexed
import com.google.android.material.chip.ChipGroup
import com.vance.musiccharts.asShuffled
import com.vance.musiccharts.dp
import com.vance.musiccharts.heightF
import com.vance.musiccharts.util.Layout
import com.vance.musiccharts.widthF

@SuppressLint("ViewConstructor")
class PieChartView(
    context: Context,
    title: String,
    subtitle: String,
    searchHint: String,
    onSearch: (ChartView, String) -> Unit
) : ChartView(context, title, subtitle, searchHint, onSearch) {

    private val chart: View
    private val itemsCheckGroup: ChipGroup
    private val itemColors: List<Int> = colors.asShuffled().take(7)

    private val animatedPercents: MutableList<Float> = MutableList(7) { 0f }
    private val tempRect: Rect = Rect()
    private var focusedIndex: Int = -1

    init {
        chart = object : View(context) {

            private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

            override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
                val w = MeasureSpec.getSize(widthMeasureSpec)
                val spec = MeasureSpec.makeMeasureSpec((w * 3/5f).toInt(), MeasureSpec.EXACTLY)
                super.onMeasure(spec, spec)
            }

            override fun draw(canvas: Canvas) {
                super.draw(canvas)

                var start = -90f
                var sweep: Float
                animatedPercents.forEachIndexed { index, percent ->
                    sweep = 360 * percent
                    paint.apply {
                        color = itemColors[index]
                        alpha = if (focusedIndex == -1 || index == focusedIndex) 255 else 128
                    }
                    canvas.drawArc(
                        0f, 0f, widthF, heightF,
                        start, sweep, true, paint
                    )
                    start += sweep
                }
            }
        }
        addView(
            chart, Layout.ezLinear(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.CENTER_HORIZONTAL
            )
        )

        itemsCheckGroup = object : ChipGroup(context) {

            init {
                chipSpacingHorizontal = 6.dp
                chipSpacingVertical = 6.dp
            }

            override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
                when (ev.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        forEachIndexed { index, view ->
                            view.getHitRect(tempRect)
                            if (tempRect.contains(ev.x.toInt(), ev.y.toInt())) {
                                focusedIndex = index
                                chart.invalidate()
                            }
                        }
                        requestDisallowInterceptTouchEvent(true)
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        focusedIndex = -1
                        chart.invalidate()
                        requestDisallowInterceptTouchEvent(false)
                    }
                }

                return true
            }
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
        itemNames.forEachIndexed { index, name ->
            PieChartItemView(
                context,
                bgColor = itemColors[index],
                text = name,
                value = itemValues[index]
            ).also {
                itemsCheckGroup.addView(it)
            }
        }

        val sum = itemValues.sum()
        val percents = itemValues.map { it / sum }
        val fromPercents = animatedPercents.toList()

        ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 500
            interpolator = DecelerateInterpolator()

            addUpdateListener {
                val v = it.animatedValue as Float

                for (i in animatedPercents.indices) {
                    animatedPercents[i] = fromPercents[i] + (percents[i] - fromPercents[i]) * v
                }

                chart.invalidate()
            }
        }.also { it.start() }
    }
}