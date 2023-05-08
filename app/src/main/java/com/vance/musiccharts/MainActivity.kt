package com.vance.musiccharts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.vance.lib.ChartDataProvider
import com.vance.musiccharts.chart.BarChartView
import com.vance.musiccharts.util.Layout
import com.vance.musiccharts.util.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private val chartProvider: ChartDataProvider = ChartDataProvider()

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

        val dudes = arrayOf("Alan Walker", "Rarin", "Max Korzh")
        MainScope().launch(Dispatchers.IO) {
            dudes.forEach { dude ->
                val data = chartProvider.popularityOfAlbums(dude).toList().sortedBy { (_, value) -> -value }.toMap()
                withContext(Dispatchers.Main) {
                    addBarChart(
                        title = dude,
                        subtitle = "The popularity of albums",
                        data = data
                    )
                }
            }
        }
    }

    private fun addBarChart(title: String, subtitle: String, data: Map<String, Int>) {
        BarChartView(
            context = this,
            title = title,
            subtitle = subtitle,
            itemNames = data.keys.take(7),
            itemValues = data.values.map { it.toFloat() }.take(7)
        ).apply {
            alpha = 0f
            scaleX = 0.5f
            scaleY = scaleX
            pivotX = 0f
            pivotY = 0f
        }.also {
            layout.addView(
                it, Layout.ezLinear(
                    Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                    0, 0, 0, 12
                )
            )
            it.animate()
                .setDuration(250)
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .start()
        }
    }
}