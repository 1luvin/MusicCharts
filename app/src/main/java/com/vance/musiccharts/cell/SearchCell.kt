package com.vance.musiccharts.cell

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.vance.musiccharts.R
import com.vance.musiccharts.asFloat
import com.vance.musiccharts.dp
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.mixWith
import com.vance.musiccharts.setTextSizeDp
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Layout
import com.vance.musiccharts.util.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation

@SuppressLint("ViewConstructor")
class SearchCell(
    context: Context,
    hint: String,
    onSearch: (String) -> Unit
) : LinearLayout(context) {

    private val imageView: ImageView
    private val editText: EditText
    private val runButton: RunButton

    private val bgRect: RectF = RectF()
    private var bgRadius: Float = 0f
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Theme.color(Theme.color_bg2)
    }

    init {
        setWillNotDraw(false)

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER
            setImageResource(R.drawable.search)
            setColorFilter(Theme.color(Theme.color_text))
        }
        addView(
            imageView, Layout.ezLinear(
                40, 40,
                Gravity.CENTER_VERTICAL
            )
        )

        editText = AppCompatEditText(context).apply {
            setPadding(0, 1, 0, 0)
            background = null
            setTextColor(Theme.color(Theme.color_text))
            setTextSizeDp(18)
            typeface = Font.Regular
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END

            this.hint = hint
            setHintTextColor(Theme.color(Theme.color_text2))
            paintFlags = paintFlags and Paint.UNDERLINE_TEXT_FLAG.inv()

            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS // removes the underline when typing
            imeOptions = EditorInfo.IME_ACTION_DONE
            setSelectAllOnFocus(true)
        }
        addView(
            editText, Layout.linear(
                0, Layout.WRAP_CONTENT,
                weight = 1f,
                Gravity.CENTER_VERTICAL
            )
        )

        runButton = RunButton().apply {
            setOnClickListener {
                isLoading = true
                onSearch(editText.text.toString())
                Log("HOW THE FUCK")
            }
        }
        addView(
            runButton, Layout.linear(
                Layout.WRAP_CONTENT, Layout.MATCH_PARENT
            )
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val r = w - (runButton.measuredWidth + 12.dp)
        bgRect.set(0f, 0f, r + 0f, h + 0f)
        bgRadius = h / 4f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint)
    }

    fun stopLoading() {
        runButton.isLoading = false
    }

    private inner class RunButton : View(context) {

        private val bgRect: RectF = RectF()
        private var bgRadius: Float = 0f

        private val bgColor: Int = 0xFF00D662.toInt()
        private val bgColorLoading: Int = 0xFFF8C915.toInt()
        private val bgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
        }

        private val runDrawable: Drawable = ContextCompat.getDrawable(context, R.drawable.run)!!.apply {
            colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
        }
        private val loadingDrawable: CircularProgressDrawable = CircularProgressDrawable(context).apply {
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 3f.dp
            colorFilter = PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)

            callback = object : Drawable.Callback {
                override fun invalidateDrawable(who: Drawable) {
                    invalidate()
                }

                override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
                override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
            }

            alpha = 0
        }.also { it.start() }

        var isLoading: Boolean = false
            set(value) {
                if (field == value) return
                field = value

                val fromAlpha: Int
                val toAlpha: Int
                if (isLoading) {
                    fromAlpha = 0
                    toAlpha = 255
                } else {
                    fromAlpha = 255
                    toAlpha = 0
                }

                AnimatorSet().apply {
                    duration = 250

                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            isEnabled = false
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            isEnabled = !isLoading
                        }
                    })

                    playTogether(
                        ValueAnimator.ofFloat((!isLoading).asFloat(), isLoading.asFloat()).apply {
                            addUpdateListener {
                                val v = it.animatedValue as Float
                                bgPaint.color = bgColor.mixWith(bgColorLoading, v)
                                invalidate()
                            }
                        },
                        ValueAnimator.ofInt(fromAlpha, toAlpha).apply {
                            addUpdateListener {
                                val v = it.animatedValue as Int
                                runDrawable.alpha = 255 - v
                                loadingDrawable.alpha = v
                                invalidate()
                            }
                        }
                    )
                }.also { it.start() }
            }

        init {
            setWillNotDraw(false)
        }

        override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
            return isLoading || super.dispatchTouchEvent(event)
        }

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            val w = MeasureSpec.getSize(heightMeasureSpec) * 2
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                heightMeasureSpec
            )
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            bgRect.set(0f, 0f, w + 0f, h + 0f)
            bgRadius = h / 4f

            var s = (h / 1.7f).toInt()
            var l = (w - s) / 2
            var t = (h - s) / 2
            runDrawable.setBounds(l, t, l + s, t + s)

            s = h / 2
            l = (w - s) / 2
            t = (h - s) / 2
            loadingDrawable.setBounds(l, t, l + s, t + s)
        }

        override fun onDraw(canvas: Canvas) {
            canvas.apply {
                drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint)
                runDrawable.draw(this)
                loadingDrawable.draw(this)
            }
        }
    }
}