package com.vance.musiccharts.extension

fun Log(msg: Any?) {
    val trace = Exception().stackTrace[1]
    val className = trace.className.substringBefore('$')
    android.util.Log.d("Log", "${className}:${trace.lineNumber} > $msg")
}