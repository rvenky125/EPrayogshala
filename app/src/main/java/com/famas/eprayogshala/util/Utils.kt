package com.famas.eprayogshala.util

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


/**
 * Transforms percentage from 1 to 100 into 0.01 to 1.00
 * @param per which accepts the percentage should be given from 1 to 100
 * */
fun generateFloatPercent(per: Int): Float {
    return when {
        (1..10).contains(per) -> "0.0$per".toFloat()
        per == 100 -> 1f
        else -> "0.$per".toFloat()
    }
}


fun String.mapFileNameToExperiment(): String =
    when {
        contains(Constants.ACID_BASE_TIT) -> "Acid Base Titration"
        contains(Constants.CONDUCTOMETRIC1) -> "Conductometric Titration(SASB)"
        contains(Constants.CONDUCTOMETRIC2) -> "Conductometric Titration(SBWA)"
        contains(Constants.COLORIMETRIC) -> "Colorimetric Titration"
        contains(Constants.POTENTIOMETRIC) -> "Potentiometric Titration"
        contains(Constants.PHMETRIC) -> "pHmetric Titration"
        else -> ""
    }

fun Context.checkForExperimentSwitch(
    lectUUID: String,
    experiment: String,
    coroutineScope: CoroutineScope,
    finish: () -> Unit
) {
    Firebase.firestore.document("_lecturers/$lectUUID/exp_timings/$experiment")
        .get()
        .addOnSuccessListener { snapshot ->
            val enable = (snapshot?.get("enable") as Timestamp?)?.toDate()
            val disable = (snapshot.get("disable") as Timestamp?)?.toDate()
            coroutineScope.launch(Dispatchers.Default) {
                repeat(Int.MAX_VALUE) {
                    delay(30L * 1000L)
                    if (enable != null && disable != null) {
                        val time = Calendar.getInstance().time
                        if (!(time.after(enable) && time.before(disable))) {
                            Toast.makeText(
                                this@checkForExperimentSwitch,
                                "Experiment has expired",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }
                }
            }
        }
}