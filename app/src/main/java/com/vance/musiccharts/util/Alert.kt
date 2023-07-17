package com.vance.musiccharts.util

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun createAlert(title: String, message: String, context: Context): AlertDialog {
    val builder = AlertDialog.Builder(context)
    builder.setCancelable(true)
    builder.setTitle(title)
    builder.setMessage(message)
    builder.setPositiveButton("OK") { dialog, _ -> dialog.cancel() }
    return builder.create()
}

fun updateAlertMessage(alert: AlertDialog, newMessage: String) {
    alert.setMessage(newMessage)
}
