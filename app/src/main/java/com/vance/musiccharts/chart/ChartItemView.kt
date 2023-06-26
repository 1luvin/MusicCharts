package com.vance.musiccharts.chart

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.github.luvin1.android.utils.Layout
import com.vance.musiccharts.R
import com.vance.musiccharts.asFloat
import com.vance.musiccharts.dp
import com.vance.musiccharts.heightF
import com.vance.musiccharts.mixWith
import com.vance.musiccharts.setTextSizeDp
import com.vance.musiccharts.util.Font
import kotlin.math.min
import kotlin.math.roundToInt

@SuppressLint("ViewConstructor")
class ChartItemView(
    context: Context,
    private val bgColor: Int,
    text: String
) : FrameLayout(context) {

    private val imageView: ImageView
    private val textView: TextView

    private val imageSize: Int = 18
    private val indent: Int = 2
    private val outlineWidth: Float = 1.5f.dp

    var isChecked: Boolean = true
        private set
    private var checkAnimator: ObjectAnimator? = null
    private var checkProgress: Float = 1f
        set(value) {
            imageView.apply {
                alpha = value
                scaleX = value
                scaleY = scaleX
            }
            textView.apply {
                setTextColor(bgColor.mixWith(Color.WHITE, value))
                updateLayoutParams<FrameLayout.LayoutParams> {
                    val l = (imageSize + indent).dp / 2
                    leftMargin = (l + l * value).roundToInt()
                }
            }
            fillPaint.color = Color.TRANSPARENT.mixWith(bgColor, value)
            invalidate()

            field = value
        }

    private val fillRect: RectF = RectF()
    private val fillPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bgColor
    }

    private val outlineRect: RectF = RectF()
    private val outlinePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = outlineWidth
        color = bgColor
    }

    init {
        setWillNotDraw(false)
        setPadding(10.dp, 0, 10.dp, 0)

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.FIT_CENTER
            setImageResource(R.drawable.done)
            setColorFilter(Color.WHITE)
        }
        addView(
            imageView, Layout.frame(
                imageSize, imageSize,
                Gravity.START or Gravity.CENTER_VERTICAL
            )
        )

        textView = TextView(context).apply {
            setTextColor(Color.WHITE)
            setTextSizeDp(16)
            typeface = Font.Regular
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.MIDDLE
            this.text = text
        }
        addView(
            textView, Layout.frame(
                Layout.WRAP_CONTENT, Layout.WRAP_CONTENT,
                Gravity.START or Gravity.CENTER_VERTICAL,
                imageSize + indent, 0, 0, 0
            )
        )
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return checkAnimator?.isRunning ?: super.dispatchTouchEvent(ev)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec)
        val textWidth = textView.paint.measureText(textView.text.toString()).roundToInt()
        val desiredWidth = paddingLeft + imageSize.dp + indent.dp + textWidth + paddingRight

        super.onMeasure(
            MeasureSpec.makeMeasureSpec(min(desiredWidth, maxWidth), MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(36.dp, MeasureSpec.EXACTLY)
        )

        val spec = MeasureSpec.makeMeasureSpec(imageSize.dp, MeasureSpec.EXACTLY)
        imageView.measure(spec, spec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        imageView.apply {
            pivotX = measuredWidth.toFloat()
            pivotY = measuredHeight / 2f
        }
        val wf = w.toFloat()
        val hf = h.toFloat()
        fillRect.apply {
            set(0f, 0f, wf, hf)
            inset(outlineWidth, outlineWidth)
        }
        outlineRect.apply {
            set(0f, 0f, wf, hf)
            inset(outlineWidth, outlineWidth)
        }
    }

    override fun onDraw(canvas: Canvas) {
        val c = heightF / 2
        canvas.apply {
            drawRoundRect(fillRect, c, c, fillPaint)
            drawRoundRect(outlineRect, c, c, outlinePaint)
        }
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        if (checked == isChecked) return
        isChecked = checked

        if (!animated) {
            checkProgress = checked.asFloat()
            return
        }

        checkAnimator = ObjectAnimator.ofFloat(this, "checkProgress", checkProgress, checked.asFloat()).apply {
            duration = 250
            interpolator = DecelerateInterpolator()

            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    checkAnimator = null
                }
            })
        }.also { it.start() }
    }
}