package com.vance.musiccharts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.widget.NestedScrollView
import com.github.luvin1.android.utils.Layout
import com.vance.lib.ChartDataProvider
import com.vance.musiccharts.chart.BarChartView
import com.vance.musiccharts.chart.ChartView
import com.vance.musiccharts.chart.LineChartView
import com.vance.musiccharts.chart.PieChartView
import com.vance.musiccharts.extension.Log
import com.vance.musiccharts.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var Instance: MainActivity? = null
        fun getInstance(): MainActivity = Instance!!
    }

    private val chartProvider: ChartDataProvider = ChartDataProvider()
    private val mainScope: CoroutineScope = MainScope()

    private val PROVIDE_ALBUM: String = "Provide album name"
    private val ALBUM: String = "Album"
    private val PROVIDE_GENRE: String = "Provide genre name"
    private val GENRE: String = "Genre"
    private val PROVIDE_ARTIST: String = "Provide artist name"
    private val ARTIST: String = "Artist"

    private lateinit var scroll: NestedScrollView
    private lateinit var layout: LinearLayout
    private lateinit var errorAlert: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Instance = this

        Layout.initialize(this)

        setupWindow()
        createView()

        errorAlert = createAlert("Oops...", "", this)
    }

    override fun onDestroy() {
        super.onDestroy()
        chartProvider.closeHttpClient()
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

        addSectionView(ALBUM)

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of tracks",
                subtitle = PROVIDE_ALBUM,
                searchHint = ALBUM,
                onSearch = { chartView, album ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.album_popularityOfTracks(album)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            PieChartView(
                context = this,
                title = "Duration of tracks",
                subtitle = PROVIDE_ALBUM,
                searchHint = ALBUM,
                onSearch = { chartView, album ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.album_durationOfTracks(album)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addSectionView(GENRE)

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of genres",
                subtitle = PROVIDE_ALBUM,
                searchHint = ALBUM,
                onSearch = { chartView, _ ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.popularityOfGenres()
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            BarChartView(
                context = this,
                title = "Number of releases",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_numberOfReleases(genre)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of artists",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_popularityOfArtists(genre)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            BarChartView(
                context = this,
                title = "Number of artists",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_numberOfArtists(genre)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            LineChartView(
                context = this,
                title = "Number of releases (1990-1999)",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_numberOfReleasesInDecade(0, genre)
                            val releases = data.values.map { it.toInt() }
                            withContext(Dispatchers.Main) {
                                (chartView as LineChartView).apply {
                                    updateLineChart(listOf(), releases)
                                }
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            LineChartView(
                context = this,
                title = "Number of releases (2000-2009)",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_numberOfReleasesInDecade(1, genre)
                            val releases = data.values.map { it.toInt() }
                            withContext(Dispatchers.Main) {
                                (chartView as LineChartView).apply {
                                    updateLineChart(listOf(), releases)
                                }
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            LineChartView(
                context = this,
                title = "Number of releases (2010-2019)",
                subtitle = PROVIDE_GENRE,
                searchHint = GENRE,
                onSearch = { chartView, genre ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.genre_numberOfReleasesInDecade(2, genre)
                            val releases = data.values.map { it.toInt() }
                            withContext(Dispatchers.Main) {
                                (chartView as LineChartView).apply {
                                    updateLineChart(listOf(), releases)
                                }
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addSectionView(ARTIST)

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of albums",
                subtitle = PROVIDE_ARTIST,
                searchHint = ARTIST,
                onSearch = { chartView, artist ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.artist_popularityOfAlbums(artist)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            LineChartView(
                context = this,
                title = "Activity",
                subtitle = PROVIDE_ARTIST,
                searchHint = ARTIST,
                onSearch = { chartView, artist ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data =
                                chartProvider.artist_activity(artist).toList().sortedBy { (key, _) -> key }.toMap()
                            val years = data.keys.toList()
                            val releases = data.values.map { it.size }
                            withContext(Dispatchers.Main) {
                                (chartView as LineChartView).apply {
                                    updateLineChart(years, releases)
                                    title = "Activity (${years.first()}-${years.last()})"
                                }
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
                        }
                    }
                }
            )
        )

        addChart(
            BarChartView(
                context = this,
                title = "Popularity of tracks",
                subtitle = PROVIDE_ARTIST,
                searchHint = ARTIST,
                onSearch = { chartView, artist ->
                    mainScope.launch(Dispatchers.IO) {
                        try {
                            val data = chartProvider.artist_popularityOfTracks(artist)
                            withContext(Dispatchers.Main) {
                                chartView.updateChart(data)
                            }
                        } catch (t: Throwable) {
                            handleError(t, chartView)
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
            view, Layout.linear(
                Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
                12, 24, 12, 24
            )
        )
    }

    private fun addChart(chartView: ChartView) = layout.addView(
        chartView, Layout.linear(
            Layout.MATCH_PARENT, Layout.WRAP_CONTENT,
            0, 0, 0, 12
        )
    )

    private suspend fun handleError(t: Throwable, chartView: ChartView) {
        Log("Error: ${t.message}\n${t.printStackTrace()}")
        t.message?.let { value -> updateAlertMessage(errorAlert, value) }
        withContext(Dispatchers.Main) {
            chartView.stopLoading()
            errorAlert.show()
            Log("Alert Was shown")
        }
    }
}