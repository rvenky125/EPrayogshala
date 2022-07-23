package com.famas.vlabschemistrymvgr.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.famas.vlabschemistrymvgr.R

// Set of Material typography styles to start with

private val ubuntu = FontFamily(
    Font(R.font.ubuntu_medium, weight = FontWeight.Medium),
    Font (R.font.ubuntu_regular, weight = FontWeight.Normal)
)

val Typography = Typography(
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    button = TextStyle(
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    defaultFontFamily = ubuntu
)