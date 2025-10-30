package com.emartins.icmbadges.services.print

import android.graphics.Bitmap

interface PrinterService {
    val paperSize: Int

    fun printText(data: Map<String, Any>)
    fun printQrCode(data: Map<String, Any>)
    fun printBadge(badgeBitmap: Bitmap?)
    fun cutPaper()
}