package com.vance.musiccharts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.vance.lib.SomeUtil

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SomeUtil.someMethod()
    }
}