package com.famas.eprayogshala.util.titrationvalues

import android.util.Log
import kotlin.random.Random

fun getRandomPotentials(type: DoubleTitrationType): List<Float> {
    val potentials = arrayListOf<Float>()

    when (type) {
        DoubleTitrationType.PILOT -> {
            var i = 0
            while (i < 8) {
                when {

                    potentials.isEmpty() -> {
                        potentials.add(Random.nextDouble(0.141, 0.151).toFloat())
                    }

                    i == 4 -> {
                        potentials.add(
                            potentials[i - 1].plus(
                                Random.nextDouble(0.250, 0.350).toFloat()
                            )
                        )
                    }

                    else -> {
                        potentials.add(
                            potentials[i - 1].plus(
                                Random.nextDouble(0.030, 0.080).toFloat()
                            )
                        )
                    }
                }

                i++
            }
        }

        DoubleTitrationType.ACCURATE -> {
            val suddenJump = listOf(13, 14).random()
            var i = 0

            while (i < 27) {
                when {
                    potentials.isEmpty() -> potentials.add(Random.nextDouble(0.220, 0.240).toFloat())

                    i == suddenJump -> potentials.add(potentials[i - 1].plus(0.110f))

                    else -> potentials.add(potentials[i-1].plus(listOf(0.003f, 0.004f, 0.005f, 0.006f, 0.007f).random()))
                }

                i++
            }
        }
    }

    potentials.forEach { Log.d("myTag", it.toString()) }

    return potentials.map {
        try {
            try {
                it.toString().substring(0, 5).toFloat()
            }
            catch (e: Exception) {
                it.toString().substring(0, 4).toFloat()
            }
        }
        catch (e: Exception) {
            it.toString().substring(0, 3).toFloat()
        }
    }
}

enum class DoubleTitrationType {
    PILOT, ACCURATE
}