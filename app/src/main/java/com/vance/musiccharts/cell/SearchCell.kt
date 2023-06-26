package com.vance.musiccharts.cell

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
import com.github.luvin1.android.utils.Layout
import com.vance.musiccharts.R
import com.vance.musiccharts.asFloat
import com.vance.musiccharts.dp
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.mixWith
import com.vance.musiccharts.setTextSizeDp
import com.vance.musiccharts.util.Constant
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Theme

@SuppressLint("ViewConstructor")
class SearchCell(
    context: Context,
    hint: String,
    onSearch: (String) -> Unit
) : LinearLayout(context) {

    private val imageView: ImageView
    private val editText: EditText
    private val searchButton: SearchButton

    private val bgRect: RectF = RectF()
    private var bgRadius: Float = 0f
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Theme.color(Theme.color_bg2)
    }

    init {
        setWillNotDraw(false)
        gravity = Gravity.CENTER_VERTICAL

        imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER
            setImageResource(R.drawable.search)
            setColorFilter(Theme.color(Theme.color_text))
        }
        addView(
            imageView, Layout.linear(
                40, 40
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

            inputType = EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            imeOptions = EditorInfo.IME_ACTION_DONE
            setSelectAllOnFocus(true)
        }
        addView(
            editText, Layout.linear(
                0, Layout.WRAP_CONTENT,
                weight = 1f
            )
        )

        searchButton = SearchButton().apply {
            setOnClickListener {
                editText.text?.let {
                    val trimmed = it.toString().trim()
                    if (trimmed.isNotEmpty()) {
                        isLoading = true
                        onSearch(trimmed)
                    }
                }
            }
        }
        addView(
            searchButton, Layout.linear(
                Layout.WRAP_CONTENT, Layout.MATCH_PARENT
            )
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val r = w - (searchButton.measuredWidth + Constant.indent.dp)
        bgRect.set(0f, 0f, r + 0f, h + 0f)
        bgRadius = h / 4f
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(bgRect, bgRadius, bgRadius, bgPaint)
    }

    fun stopLoading() {
        searchButton.isLoading = false
    }

    private inner class SearchButton : View(context) {

        private val bgRect: RectF = RectF()
        private var bgRadius: Float = 0f
        private val bgColor: Int = Theme.color(Theme.color_positive)
        private val bgColorLoading: Int = Theme.color(Theme.color_loading)
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

                ValueAnimator.ofFloat((!isLoading).asFloat(), isLoading.asFloat()).apply {
                    duration = 250

                    addUpdateListener {
                        val v = it.animatedValue as Float
                        bgPaint.color = bgColor.mixWith(bgColorLoading, v)
                        runDrawable.alpha = (255 * (1 - v)).toInt()
                        loadingDrawable.alpha = 255 - runDrawable.alpha
                        invalidate()
                    }
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

            var s = (h * 0.6f).toInt()
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