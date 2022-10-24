package com.famas.eprayogshala.util

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import kotlin.random.Random

object Constants {
    const val FIREBASE_WEB_CLIENT_ID = "889875793126-btbqkdk0bk4c11v7j3k71er9htqrnkbq.apps.googleusercontent.com"
    const val ROUTE_EXPERIMENTS = "__experiments"
    const val ROUTE_REPORTS = "__reports"
    val POWER_STRING = buildAnnotatedString {
        append("  X 10")
        withStyle(SpanStyle(baselineShift = BaselineShift.Superscript)) {
            append("-4")
        }
    }

    //ExperimentNames
    const val ACID_BASE_TIT = "Acid Base Titration"
    const val CONDUCTOMETRIC1 = "ConductometricTitration(Strong acid Strong base)"
    const val CONDUCTOMETRIC2 = "ConductometricTitration(Weak Acid with Strong Base)"
    const val POTENTIOMETRIC = "PotentiometricTitration"
    const val COLORIMETRIC = "Colorimetric Titration"
    const val PHMETRIC = "pHmetricTitration"
    const val HARDNESS_WATER = "Determination of Hardness of water"

    //Shared preference keys
    const val LECT_NAME_KEY = "__lect_name"
    const val LECT_ID_KEY = "__lect_id"
    const val MY_NAME_KEY = "__my_name"
    const val MY_BRANCH_KEY = "__ny_branch"
    const val MY_SECTION_KEY = "__my_section"
    const val MY_ROLL_NO_KEY = "__my_roll_no"
    const val MY_UUID_KEY = "__my_uuid"

    val tit1Threshold = listOf(
        8.1.toBigDecimal(),
        8.2.toBigDecimal(),
        8.3.toBigDecimal(),
        8.4.toBigDecimal(),
        8.5.toBigDecimal(),
        8.6.toBigDecimal(),
        8.7.toBigDecimal(),
        8.8.toBigDecimal(),
        8.9.toBigDecimal(),
        9.0.toBigDecimal()
    ).random()

    val tit3Threshold = listOf(
        0.1.toBigDecimal(),
        0.2.toBigDecimal(),
        0.3.toBigDecimal(),
        0.4.toBigDecimal(),
        0.5.toBigDecimal(),
        0.6.toBigDecimal(),
        0.7.toBigDecimal(),
        0.8.toBigDecimal(),
        0.9.toBigDecimal(),
        1.1.toBigDecimal(),
        1.2.toBigDecimal(),
        1.3.toBigDecimal(),
        1.4.toBigDecimal(),
        1.5.toBigDecimal(),
        1.6.toBigDecimal(),
        1.7.toBigDecimal(),
        1.8.toBigDecimal(),
        1.9.toBigDecimal(),
        2.0.toBigDecimal(),
    ).random()

    val tit2Threshold = listOf(
        3.1.toBigDecimal(),
        3.2.toBigDecimal(),
        3.3.toBigDecimal(),
        3.4.toBigDecimal(),
        3.5.toBigDecimal(),
        3.6.toBigDecimal(),
        3.7.toBigDecimal(),
        3.8.toBigDecimal(),
        3.9.toBigDecimal(),
        4.0.toBigDecimal()
    ).random()
}