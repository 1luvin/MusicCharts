package com.vance.musiccharts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.vance.lib.ChartDataProvider
import com.vance.musiccharts.chart.BarChartView
import com.vance.musiccharts.chart.ChartView
import com.vance.musiccharts.chart.PieChartView
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.util.Font
import com.vance.musiccharts.util.Layout
import com.vance.musiccharts.util.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val chartProvider: ChartDataProvider = ChartDataProvider()
    private val mainScope: CoroutineScope = MainScope()

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: MainActivity? = null
        fun getInstance(): MainActivity = Instance!!
    }

    private lateinit var scroll: NestedScrollView
    private lateinit var layout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Instance = this

        setupWindow()
        createView()
    }

    private fun setupWindow() {
        val color = Theme.color(Theme.color_bg)
        window.apply {
            statusBarColor = color
            navigationBarColor = color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                navigationBarDividerColor = color
            }
            WindowCompat.getInsetsController(this, decorView).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    private fun createView() {
        layout = LinearLayout(this).apply {
            setPadding(0, 12.dp, 0, 0)
            orientation = LinearLayout.VERTICAL
        }

        scroll = NestedScrollView(this).apply {
            setBackgroundColor(Theme.color(Theme.color_bg))
            isVerticalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER

            addView(layout)
        }

        setContentView(scroll)

        addSectionView("Album")

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of tracks in album",
                subtitle = "Provide album name",
                searchHint = "Album",
                onSearch = { chartView, query ->
                    mainScope.launch(Dispatchers.IO) {
                        val data = chartProvider.popularityOfTracksInAlbum(query).toList().sortedBy { (_, value) -> -value }.toMap()
                        val itemNames = data.keys.take(7)
                        val itemValues = data.values.map { it.toFloat() }.take(7)
                        withContext(Dispatchers.Main) {
                            chartView.updateChart(itemNames, itemValues)
                        }
                    }
                }
            )
        )

        addChart(
            PieChartView(
                context = this,
                title = "Duration of tracks in album",
                subtitle = "Provide album name",
                searchHint = "Album",
                onSearch = { chartView, query ->
                    mainScope.launch(Dispatchers.IO) {
                        val data = chartProvider.durationOfTracksInAlbum(query).toList().sortedBy { (_, value) -> -value }.toMap()
                        val itemNames = data.keys.take(7)
                        val itemValues = data.values.map { it / 60_000f }.take(7)
                        withContext(Dispatchers.Main) {
                            chartView.updateChart(itemNames, itemValues)
                        }
                    }
                }
            )
        )
    }

    private fun addSectionView(name: String) {
        val view = TextView(this).apply {
            setTextColor(0xFF2AABEE.toInt())
            setTextSizeDp(35)
            typeface = Font.Bold
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            text = name
        }
        layout.addView(
            view, Layout.ezLinear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                12, 24, 12, 24
            )
        )
    }

    private fun addChart(chartView: ChartView) = layout.addView(
        chartView, Layout.ezLinear(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            0, 0, 0, 12
        )
    )
}