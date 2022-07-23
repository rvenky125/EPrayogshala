package com.famas.vlabschemistrymvgr.util.titrationvalues

import android.util.Log
import com.famas.vlabschemistrymvgr.util.titrationvalues.ConductancesCurveType.PARABOLA
import com.famas.vlabschemistrymvgr.util.titrationvalues.ConductancesCurveType.STRAIGHT_LINE
import kotlin.random.Random

fun getRandomConductances(type: ConductancesCurveType): List<Float> {
    val conductances = arrayListOf<Float>()

    when (type) {

        PARABOLA -> {
            val higherConductance = 10.62f
            val suddenIncrease = listOf(5, 6, 7).random()

            var i = 0
            while (i <= 16) {

                if (conductances.isEmpty()) conductances.add(
                    listOf(
                        10.50f,
                        10.51f,
                        10.52f,
                        10.54f,
                        10.55f,
                        10.56f,
                        10.57f,
                        10.58f,
                        10.59f,
                        10.60f,
                        10.61f,
                        higherConductance
                    ).random()
                )
                else {
                    when {
                        i == suddenIncrease -> {
                            conductances.add(
                                conductances[i - 1].plus(
                                    Random.nextDouble(0.30, 0.40).toFloat()
                                )
                            )
                        }
                        i > suddenIncrease -> {
                            conductances.add(
                                conductances[i - 1].plus(
                                    Random.nextDouble(0.70, 1.00).toFloat()
                                )
                            )
                        }
                        else -> {
                            if (i > 1 && (conductances[i - 1] - conductances[i - 2]) >= 1.00f) {
                                conductances.add(
                                    conductances[i - 1].minus(
                                        Random.nextDouble(0.70, 1.00).toFloat()
                                    )
                                )
                            } else {
                                conductances.add(
                                    conductances[i - 1].minus(
                                        Random.nextDouble(1.20, 1.70).toFloat()
                                    )
                                )
                            }
                        }
                    }
                }
                if (conductances[i] < 2.620f) conductances[i] = 2.620f

                try {
                    conductances[i] = conductances[i].toString().substring(0, 4).toFloat()
                }
                catch (e: Exception) {
                    conductances[i] = conductances[i].toString().substring(0, 3).toFloat()
                }

                Log.d("myTag", conductances[i].toString())
                i++
            }

        }

        STRAIGHT_LINE -> {
            val suddenIncrease = listOf(5, 6).random()
            var i = 0
            while (i <= 16) {
                when {
                    conductances.isEmpty() -> {
                        conductances.add(
                            Random.nextDouble(0.821, 0.830).toFloat()
                        )
                    }

                    i <= suddenIncrease -> {
                        conductances.add(
                            conductances[i - 1].plus(
                                Random.nextDouble(0.27, 0.35).toFloat()
                            )
                        )
                    }

                    i > suddenIncrease -> {
                        conductances.add(
                            conductances[i - 1].plus(
                                Random.nextDouble(0.90, 1.00).toFloat()
                            )
                        )
                    }
                }

                try {
                    conductances[i] = conductances[i].toString().substring(0, 5).toFloat()
                }
                catch (e: Exception) {
                    conductances[i] = conductances[i].toString().substring(0, 4).toFloat()
                }

                Log.d("myTag", conductances[i].toString())
                i++
            }
        }
    }

    return conductances
}


enum class ConductancesCurveType {
    PARABOLA, STRAIGHT_LINE
}