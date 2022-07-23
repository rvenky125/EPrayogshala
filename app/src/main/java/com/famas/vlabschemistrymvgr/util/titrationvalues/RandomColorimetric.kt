package com.famas.vlabschemistrymvgr.util.titrationvalues

import java.math.BigDecimal
import kotlin.random.Random

fun getRandomColorimetricValues(): List<BigDecimal> {
    val list = arrayListOf<BigDecimal>()
    val threshold = Random.nextDouble(0.40, 0.50).toBigDecimal()
    for (i in 0..3) {
        when(i) {
            0 -> list.add(Random.nextDouble(0.34, 0.55).toBigDecimal())
            else -> list.add(list[i-1].plus(threshold))
        }
    }
    return list.map {
        try {
            it.toString().substring(0, 4).toBigDecimal()
        }
        catch (e: Exception) {
            it.toString().substring(0, 3).plus("1").toBigDecimal()
        }
    }
}