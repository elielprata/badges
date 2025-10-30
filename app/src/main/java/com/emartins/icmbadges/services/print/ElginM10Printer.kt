package com.emartins.icmbadges.services.print

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.Toast
import androidx.core.graphics.scale
import com.elgin.e1.Impressora.Termica
import com.topwise.cloudpos.aidl.printer.ImageUnit
import com.topwise.cloudpos.aidl.printer.PrintTemplate
import java.io.File
import java.io.FileOutputStream

open class ElginM10Printer(context: Activity) : PrinterService {
    override val paperSize: Int = 58

    private var mContext: Activity = context
    private var printerStatus: Int;

    init {
        Termica.setActivity(mContext)
        printerStatus = startPrinter()
    }

    // Função pra imiprir texto
    override fun printText(data: Map<String, Any>) {
        if (printerStatus == 0) {
            // Retrieve values from the map with null safety
            val text = data["text"] as? String ?: ""
            val align = data["align"] as? String ?: "Esquerda"  // Default to "Esquerda" if null
            val font = data["font"] as? String ?: ""
            val fontSize = (data["fontSize"] as? Int) ?: 12  // Default to 12 if null

            // Initialize result variable
            var result = 0
            var alignValue = 0
            var styleValue = 0

            // Determine alignment value
            alignValue = when (align) {
                "Esquerda" -> 0
                "Centralizado" -> 1
                else -> 2
            }

            // Determine style value
            if (font == "FONT B") {
                styleValue += 1
            }
            if (data["isUnderline"] as? Boolean == true) {
                styleValue += 2
            }
            if (data["isBold"] as? Boolean == true) {
                styleValue += 8
            }

            // Call the method and return the result
            Termica.ImpressaoTexto(text, alignValue, styleValue, 0);
            Termica.AvancaPapel(2)
        }
    }

    // Função pra imiprir QR Code
    override fun printQrCode(data: Map<String, Any>) {
        startPrinter()

        val size = data["qrSize"] as? Int ?: 5
        val text = data["text"] as? String ?: ""
        val align = data["align"] as? String ?: "center"
        val nivelCorrecao = 2

        // ALINHAMENTO VALUE
        val alignValue = when (align) {
            "left" -> 0
            "right" -> 2
            else -> 1
        }

        Termica.DefinePosicao(alignValue)

        val result = Termica.ImpressaoQRCode(text, size, nivelCorrecao)
        Toast.makeText( mContext, result.toString(), Toast.LENGTH_LONG).show()
    }

    override fun printBadge(badgeBitmap: Bitmap?) {
        try {
            if (badgeBitmap != null) {
                println("BITMAP_HEIGHT: ${badgeBitmap.height}")

                // Recorta a imagem conforme o código original
                var croppedBitmap = Bitmap.createBitmap(
                    badgeBitmap,
                    0, 16,
                    badgeBitmap.width, (badgeBitmap.height - 37)
                )

                // Redimensiona (função existente no seu projeto)
                croppedBitmap = resizeBitmapForWidth(croppedBitmap)

                // Rotaciona a imagem como antes
                val rotatedBitmap = rotateBitmap(croppedBitmap, 90f)
                Termica.ImprimeBitmap(rotatedBitmap)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun resizeBitmapForWidth(bitmap: Bitmap, targetWidth: Int = 660): Bitmap {
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return bitmap.scale(targetWidth, targetHeight)
    }

    private fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(degrees)
        val rotatedBitmap =
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
        original.recycle()
        return rotatedBitmap
    }

    // Função pra cortar papel
    override fun cutPaper() {
        val lines = 3
        Termica.Corte(lines)
    }

    // Função pra iniciar a comunicação com a impressora
    open protected fun startPrinter(): Int {
        stopPrinter()
        val result = Termica.AbreConexaoImpressora(5, "", "", 0)
        return result
    }

    // Função pra fechar a conexão com a impressora
    protected fun stopPrinter() {
        Termica.FechaConexaoImpressora()
    }
}