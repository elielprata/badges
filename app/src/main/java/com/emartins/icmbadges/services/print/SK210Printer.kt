package com.emartins.icmbadges.services.print

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.RemoteException
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.graphics.scale
import com.imin.printerlib.util.BytesUtil.rotateBitmap
import com.emartins.icmbadges.utils.DeviceServiceManager
import com.topwise.cloudpos.aidl.printer.AidlPrinterListener
import com.topwise.cloudpos.aidl.printer.Align
import com.topwise.cloudpos.aidl.printer.ImageUnit
import com.topwise.cloudpos.aidl.printer.PrintCuttingMode
import com.topwise.cloudpos.aidl.printer.PrintTemplate
import com.topwise.cloudpos.aidl.printer.TextUnit
import java.text.SimpleDateFormat
import java.util.Date


class SK210Printer(context: Context) : PrinterService {
    override val paperSize: Int = 58

    private var mContext: Context = context
    private val mLock = Object()

    @Volatile
    var mInPrinter: Boolean = false

    private val mListen: AidlPrinterListener = object: AidlPrinterListener.Stub() {
        @Throws(RemoteException::class)
        override fun onError(i: Int) {
            mInPrinter = false
            synchronized(mLock) {
                mLock.notify()
            }
        }

        @Throws(RemoteException::class)
        override fun onPrintFinish() {
            mInPrinter = false
            synchronized(mLock) {
                mLock.notify()
            }
        }
    }

    override fun printText(data: Map<String, Any>) {
        try {
            val text = data["text"].toString()
            val template = PrintTemplate.getInstance()
            template.init(mContext)
            template.clear()
            //var fontSize = data["fontSize"]
            template.add(TextUnit(text, 20, Align.CENTER).setBold(true))

            mInPrinter = true
            // Get PrintManager Instance
            val printManager = DeviceServiceManager.getInstance().getPrintManager()
            printManager.addRuiImage(
                rotateBitmap(
                    template.printBitmap,
                    0
                ), 0
            )

            // Execute the print
            printManager.printRuiQueue(mListen)

            synchronized(mLock) {
                while (mInPrinter) {
                    try {
                        Log.d("Printer", "Aguardando impressão terminar...")
                        mLock.wait() // Espera até que a impressão termine
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printQrCode(data: Map<String, Any>) {
        try {
            val template = PrintTemplate.getInstance()
            val text = data["text"].toString()

            val qrCode: Bitmap? = generateQrCode(text, 350, 350)

            if (qrCode != null) {
                template.init(mContext)
                template.clear()
                template.add(ImageUnit(qrCode, 350, 350))

                mInPrinter = true
                // Get PrintManager Instance
                val printManager = DeviceServiceManager.getInstance().getPrintManager()
                printManager.addRuiImage(
                    rotateBitmap(
                        template.printBitmap,
                        0
                    ), 0
                )

                // Execute the print
                printManager.printRuiQueue(mListen)

                synchronized(mLock) {
                    while (mInPrinter) {
                        try {
                            Log.d("Printer", "Aguardando impressão terminar...")
                            mLock.wait() // Espera até que a impressão termine
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    override fun printBadge(badgeBitmap: Bitmap?) {
        try {
            val template = PrintTemplate.getInstance()
            if (badgeBitmap != null) {
                println("BITMAP_HEIGHT: ${badgeBitmap.height}")
                var croppedBitmap = Bitmap.createBitmap(
                    badgeBitmap,
                    0, 16,
                    badgeBitmap.width, (badgeBitmap.height - 37)
                )

                croppedBitmap = resizeBitmapForWidth(croppedBitmap)

                template.init(mContext)
                template.clear()
                template.add(ImageUnit(croppedBitmap, croppedBitmap.width, croppedBitmap.height))

                mInPrinter = true
                val rotatedBitmap = rotateBitmap(croppedBitmap, 90)
                val printManager = DeviceServiceManager.getInstance().getPrintManager()

                /*val imageParts = splitBitmapVertically(rotatedBitmap)
                // Get PrintManager Instance
                for (part in imageParts) {
                    printManager.addRuiImage(part,0)
                }*/
                printManager.addRuiImage(rotatedBitmap,0)


                // Execute the print
                printManager.printRuiQueue(mListen)

                synchronized(mLock) {
                    while (mInPrinter) {
                        try {
                            mLock.wait() // Espera até que a impressão termine
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    private fun resizeBitmapForWidth(bitmap: Bitmap, targetWidth: Int = 660): Bitmap {
        val aspectRatio = bitmap.height.toFloat() / bitmap.width.toFloat()
        val targetHeight = (targetWidth * aspectRatio).toInt()
        return bitmap.scale(targetWidth, targetHeight)
    }

    private fun splitBitmapVertically(bitmap: Bitmap, maxHeight: Int = 2000): List<Bitmap> {
        val parts = mutableListOf<Bitmap>()
        var y = 0
        while (y < bitmap.height) {
            val height = minOf(maxHeight, bitmap.height - y)
            parts.add(Bitmap.createBitmap(bitmap, 0, y, bitmap.width, height))
            y += height
        }
        return parts
    }

    override fun cutPaper() {
        printText(mapOf("text" to "\n\n"))
        if (mInPrinter) {
            Log.d("Printer", "Aguardando a impressão terminar antes de cortar o papel...")
            synchronized(mLock) {
                while (mInPrinter) {
                    try {
                        mLock.wait() // Espera a impressão terminar
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }


        val printManager = DeviceServiceManager.getInstance().getPrintManager()
        printManager.cuttingPaper(PrintCuttingMode.CUTTING_MODE_FULL)
    }

    private fun generateQrCode(text: String, width: Int, height: Int): Bitmap? {
        /*val writer = QRCodeWriter()
        try {
            val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height)
            val qrBitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until bitMatrix.width) {
                for (y in 0 until bitMatrix.height) {
                    qrBitmap.setPixel(x, y, if (bitMatrix[x, y]) -0x1000000 else -0x1)
                }
            }
            return qrBitmap
        } catch (error: Exception) {
            error.printStackTrace();
            return null
        }*/
        val qrBitmap: Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        return qrBitmap
    }

    private fun rotateBitmap(original: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(degrees)
        val rotatedBitmap =
            Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
        original.recycle()
        return rotatedBitmap
    }

    private fun curTime() : String {
        val date = Date(System.currentTimeMillis())
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS")
        val time = format.format(date)
        return time
    }
}