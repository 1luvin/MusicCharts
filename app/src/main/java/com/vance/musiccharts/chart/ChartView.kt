package com.vance.musiccharts.chart

import android.content.Context
import android.text.TextUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import com.github.luvin1.android.utils.Layout
import com.vance.musiccharts.cell.SearchCell
import com.vance.musiccharts.setTextSizeDp
import com.vance.musiccharts.util.Constant
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Theme

abstract class ChartView(
    context: Context,
    title: String,
    subtitle: String,
    searchHint: String,
    onSearch: (ChartView, String) -> Unit
) : LinearLayout(context) {

    companion object {

        val colors: IntArray = intArrayOf(
            0xFFD65B66.toInt(),
            0xFFEFBA48.toInt(),
            0xFF73BFF3.toInt(),
            0xFF4CD7AD.toInt(),
            0xFFAFB9DC.toInt(),
            0xFF824BA6.toInt(),
            0xFF00743F.toInt(),
            0xFF0684F2.toInt(),
            0xFFF48804.toInt(),
        )
    }

    private val textView: TextView
    private val textView2: TextView
    protected val searchCell: SearchCell

    protected val indent: Int = Constant.indent

    init {
        orientation = VERTICAL

        textView = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text))
            setTextSizeDp(22)
            typeface = Font.SemiBold
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            text = title
        }
        addView(
            textView, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, 0, indent, 0
            )
        )

        textView2 = TextView(context).apply {
            setTextColor(Theme.color(Theme.color_text2))
            setTextSizeDp(16)
            typeface = Font.Regular
            text = subtitle
        }
        addView(
            textView2, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                indent, 0, indent, 0
            )
        )

        searchCell = SearchCell(
            context,
            hint = searchHint,
            onSearch = {
                onSearch(this, it)
            }
        )
        addView(
            searchCell, Layout.linear(
                Layout.MATCH_PARENT, 40,
                indent, indent, indent, indent * 2
            )
        )
    }

    @CallSuper
    open fun updateChart(data: Map<String, Number>) {
        searchCell.stopLoading()
    }
}