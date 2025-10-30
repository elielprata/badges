package com.emartins.icmbadges.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.graphics.createBitmap
import java.io.File
import java.io.FileOutputStream

object PdfUtils {
    fun convertPdfToBitmap(context: Context, pdfBytes: ByteArray, pageIndex: Int = 0, scale: Float = 6f): Bitmap {
        val tempFile = File(context.cacheDir, "temp.pdf")
        FileOutputStream(tempFile).use { it.write(pdfBytes) }

        val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(pageIndex)

        val width = (page.width * scale).toInt()
        val height = (page.height * scale).toInt()

        val bitmap = createBitmap(width, height)
        //val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // força fundo branco
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        //page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        page.close()
        pdfRenderer.close()
        fileDescriptor.close()

        return bitmap
    }
}