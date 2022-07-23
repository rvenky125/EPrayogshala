package com.famas.vlabschemistrymvgr.util

import android.os.CountDownTimer
import java.util.*

fun Date.startTimer(
    onTick: (String) -> Unit,
    onFinish: () -> Unit
) {

    val curTime = Calendar.getInstance().time
    object : CountDownTimer(this.time - curTime.time, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            val minutes = (millisUntilFinished/1000) / 60
            val seconds = (millisUntilFinished/1000) % 60

            onTick("${minutes}m ${seconds}s")
        }

        override fun onFinish() {
            onFinish()
        }
    }.start()
}