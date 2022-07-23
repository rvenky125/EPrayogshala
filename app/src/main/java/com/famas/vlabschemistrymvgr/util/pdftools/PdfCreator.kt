package com.famas.vlabschemistrymvgr.util.pdftools

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.graphics.scale
import java.io.File
import java.io.FileOutputStream
import java.util.*

fun createPdf(
    stName: String,
    stImage: Bitmap?,
    rollNo: String,
    branch: String,
    section: String,
    expName: String,
    labTeacher: String,
    step1List: List<String> = listOf(),
    step2List: List<String> = listOf(),
    step3List: List<String> = listOf(),
    filesDir: File,
    onCreated: () -> Unit
) {
    val pdfDoc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(1200, 2010, 1).create()
    val page = pdfDoc.startPage(pageInfo)

    val canvas = page.canvas

    val timeText = Calendar.getInstance().time.toString()

    //scaling image
    stImage?.scale(330, 700, true)

    //paints
    val timePaint = Paint()
    val detailsPaint = Paint()
    val labResPaint = Paint()
    val imgPaint = Paint()

    //initialising paints
    timePaint.textSize = 30f
    timePaint.typeface = Typeface.SANS_SERIF
    detailsPaint.textSize = 35f
    labResPaint.typeface = Typeface.DEFAULT_BOLD
    labResPaint.textSize = 55f

    //default values
    val detailsMargin = 60f

    stImage?.let { canvas.drawBitmap(it, null, Rect(820, 62, 1108, 430), imgPaint) }

    canvas.drawText(" Time Stamp: $timeText", 39f, 329f, timePaint)

    canvas.drawText("Student name : $stName", detailsMargin, 455f, detailsPaint)
    canvas.drawText("Roll no : $rollNo", detailsMargin, 541f, detailsPaint)
    canvas.drawText("Branch : $branch", detailsMargin, 624f, detailsPaint)
    canvas.drawText("Section : $section", detailsMargin, 708f, detailsPaint)
    canvas.drawText("Experiment : $expName", detailsMargin, 791f, detailsPaint)
    canvas.drawText("Lab teacher: $labTeacher", detailsMargin, 875f, detailsPaint)

    canvas.drawText("Lab results:", detailsMargin, 1000f, labResPaint)

    if (step1List.isNotEmpty()) {
        canvas.drawText("Step1 : ", detailsMargin, 1090f, detailsPaint)
        step1List.forEachIndexed { i, s ->
            canvas.drawText(s, 200f, 1090f + 50 * i, detailsPaint)
        }
    }

    val step2Y = 1090f + (step1List.size * 47) + 90f
    if (step2List.isNotEmpty()) {
        canvas.drawText("Step2 : ", detailsMargin, step2Y, detailsPaint)
        step2List.forEachIndexed { i, s ->
            canvas.drawText(s, 200f, step2Y + 50 * i, detailsPaint)
        }
    }

    val step3Y = step2Y + (step2List.size * 47) + 130f
    if (step3List.isNotEmpty()) {
        canvas.drawText("Step3 : ", detailsMargin, step3Y, detailsPaint)
        step3List.forEachIndexed { i, s ->
            canvas.drawText(s, 200f, step3Y + 50 * i, detailsPaint)
        }
    }

    //Signature at bottom
    canvas.drawText("Signature:", 683f, 1903f, timePaint)
    //canvas.drawText(" Time Stamp: $timeText", 1065f, 2100f, timePaint)
    timePaint.setARGB(100, 140, 17, 17)
    timePaint.style = Paint.Style.FILL_AND_STROKE
    canvas.drawText("*Please do the calculations and plot graph if required", detailsMargin, 2000f, timePaint)

    //finishing pdf
    val fileName = "$expName.pdf"
    pdfDoc.finishPage(page)
    val file = File(filesDir, fileName)
    file.createNewFile()

    //saving pdf
    try {
        pdfDoc.writeTo(FileOutputStream(file))
        onCreated()
    }
    catch (e: Exception) {
        e.printStackTrace()
    }

    //closing pdf doc
    pdfDoc.close()
}