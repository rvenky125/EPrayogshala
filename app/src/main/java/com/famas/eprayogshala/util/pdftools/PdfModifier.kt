package com.famas.eprayogshala.util.pdftools

import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import java.io.File
import java.io.InputStream

fun File.addBitmapToPdf(
    stream: InputStream,
    onCompleted: () -> Unit
) {
    val bitmap = BitmapFactory.decodeStream(stream)

    val newPdfDoc = PdfDocument()

    val bitmapPaint = Paint()
    val list = renderPdf(this) {

    }

    for (i in list.indices) {
        val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, i + 1).create()
        val page = newPdfDoc.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(list[i], null, Rect(0, 0, 1200, 2010), bitmapPaint)
        newPdfDoc.finishPage(page)
    }

    val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, list.size + 1).create()
    val page = newPdfDoc.startPage(pageInfo)
    val canvas = page.canvas
    canvas.drawBitmap(bitmap, null, Rect(0, 0, 1200, 2010), bitmapPaint)

    newPdfDoc.finishPage(page)

    try {
        newPdfDoc.writeTo(this.outputStream())
        onCompleted()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    newPdfDoc.close()
}

fun File.addFileToPdf(
    fromFile: File,
    onCompleted: () -> Unit
) {
    val newPdfDoc = PdfDocument()
    val bitmapPaint = Paint()
    val oldList = renderPdf(this) {}
    val newList = renderPdf(fromFile) {}

    for (i in oldList.indices) {
        val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, i + 1).create()
        val page = newPdfDoc.startPage(pageInfo)
        val canvas = page.canvas
        canvas.drawBitmap(oldList[i], null, Rect(0, 0, 1200, 2010), bitmapPaint)
        newPdfDoc.finishPage(page)
    }

    for (i in newList.indices) {
        val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, oldList.size + (i + 1)).create()
        val page = newPdfDoc.startPage(pageInfo)
        page.canvas.drawBitmap(newList[i], null, Rect(0, 0, 1200, 2010), bitmapPaint)
        newPdfDoc.finishPage(page)
    }

    try {
        newPdfDoc.writeTo(this.outputStream())
        onCompleted()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    newPdfDoc.close()
}

fun File.removeLastPage(
    showToast: () -> Unit,
    onCompleted: () -> Unit
) {
    val newPdfDoc = PdfDocument()

    val bitmapPaint = Paint()
    val list = renderPdf(this) {}

    if (list.size != 1) {
        for (i in list.indices) {
            if (i == list.size - 1) continue
            val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, i + 1).create()
            val page = newPdfDoc.startPage(pageInfo)
            val canvas = page.canvas
            canvas.drawBitmap(list[i], null, Rect(0, 0, 1200, 2010), bitmapPaint)
            newPdfDoc.finishPage(page)
        }

        try {
            newPdfDoc.writeTo(this.outputStream())
            onCompleted()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        newPdfDoc.close()
    } else showToast()
}