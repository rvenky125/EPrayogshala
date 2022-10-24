package com.famas.eprayogshala.util.titrationvalues

import android.util.Log
import kotlin.random.Random

fun getRandomPhValues(type : DoubleTitrationType): List<Float> {
    val phValues = arrayListOf<Float>()

    when(type) {
        DoubleTitrationType.PILOT -> {
            for(i in 0..4) {
                when(i) {
                    0 -> phValues.add(Random.nextDouble(2.0, 2.5).toFloat())
                    1 -> phValues.add(Random.nextDouble(2.5, 2.8).toFloat())
                    2 -> phValues.add(Random.nextDouble(9.5, 11.5).toFloat())
                    3 -> phValues.add(phValues[2] + Random.nextDouble(1.0, 1.5).toFloat())
                }
            }
        }

        DoubleTitrationType.ACCURATE -> {
            val jumpIndex = Random.nextInt(17, 21)
            val finalIndex = jumpIndex + 6
            for (i in 0..finalIndex) {
                when(i) {
                    0 -> phValues.add(Random.nextDouble(2.0, 2.5).toFloat())
                    jumpIndex -> phValues.add(Random.nextDouble(8.5, 10.5).toString().substring(0, 4).toFloat())
                    else -> phValues.add(phValues[i-1] + listOf(0.01f, 0.02f, 0.03f).random())
                }
            }
        }
    }

    return phValues.map {
        try {
            Log.d("myTag", it.toString().substring(0, 4))
            it.toString().substring(0, 4).toFloat()
        }
        catch (e: Exception) {
            it.toString().substring(0, 3).plus("0").toFloat()
        }
    }
}