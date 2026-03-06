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
    fun convertPdfToBitmap(
        context: Context,
        pdfBytes: ByteArray,
        pageIndex: Int = 0,
        scale: Float = 6f
    ): Bitmap {

        val tempFile = File(context.cacheDir, "temp.pdf")
        FileOutputStream(tempFile).use { it.write(pdfBytes) }

        val fileDescriptor = ParcelFileDescriptor.open(tempFile, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(pageIndex)

        val width = (page.width * scale).toInt()
        val height = (page.height * scale).toInt()

        val margin = 40 // margem lateral

        // bitmap original renderizado
        val originalBitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(originalBitmap)
        canvas.drawColor(Color.WHITE)

        page.render(originalBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // bitmap final com margem
        val finalBitmap = createBitmap(width + margin, height, Bitmap.Config.ARGB_8888)
        val finalCanvas = Canvas(finalBitmap)
        finalCanvas.drawColor(Color.WHITE)

        // desenha o PDF no bitmap com margem
        finalCanvas.drawBitmap(originalBitmap, 0f, 0f, null)

        page.close()
        pdfRenderer.close()
        fileDescriptor.close()

        return finalBitmap
    }
}