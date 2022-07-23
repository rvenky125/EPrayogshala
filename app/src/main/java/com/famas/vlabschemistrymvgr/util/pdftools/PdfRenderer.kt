package com.famas.vlabschemistrymvgr.util.pdftools

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File

fun renderPdf(
    file: File,
    showToast: (String) -> Unit
): List<Bitmap> {
    val bitmaps = arrayListOf<Bitmap>()

    try {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)

        val pages = pdfRenderer.pageCount
        Log.d("myTag", "pages count: $pages")
        for (i in 0 until pages) {
            val page = pdfRenderer.openPage(i).apply {
                Log.d("myTag", "Creating page with index: $i")
                val bitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
                render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmaps.add(bitmap)
            }
            page.close()
        }
    }
    catch (e: Exception) {
        e.message?.let { showToast(it) }
    }

    return bitmaps
}