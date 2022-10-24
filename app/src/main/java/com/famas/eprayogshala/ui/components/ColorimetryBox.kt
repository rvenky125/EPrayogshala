package com.famas.eprayogshala.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.famas.eprayogshala.util.Constants


@ExperimentalAnimationApi
@Composable
fun ColorimetryBox() {
    val texts = remember {
        arrayOf(
            "Vol of Fe(III)",
            "Mol of Fe(III)",
            "Vol of HNO3",
            "Vol of KSCN",
            "total vol",
            "M"
        )
    }
    val values = remember {
        listOf(
            buildAnnotatedString { append("5.5${Constants.POWER_STRING}") },
            buildAnnotatedString { append("1.0 ml") },
            buildAnnotatedString { append("4.0 ml") },
            buildAnnotatedString { append("25 ml") }
        )
    }

    /*VerticalGrid(Modifier.fillMaxWidth()) {
        texts.forEach {
            HeadCard(s = it)
        }
        repeat(4) {
            EnterCard()
            values.forEach { annotatedString ->
                ValueCard(s = annotatedString)
            }
            EnterCard()
        }
    }*/

    Column {
        VerticalGrid(Modifier.fillMaxWidth()) {
            texts.forEach { HeadCard(s = it) }
        }
        VerticalGrid(Modifier.fillMaxWidth()) {
            val (txt, setTxt) = remember { mutableStateOf("") }
            var txt2 by remember { mutableStateOf("") }
            EnterCard(text = txt, setTxt = setTxt)
            if (txt.length > 2) {
                values.forEach { ValueCard(s = it) }
                EnterCard(
                    text = txt2,
                    anotherText = Constants.POWER_STRING,
                    setTxt = { txt2 = it }
                )
            }
        }
        VerticalGrid(Modifier.fillMaxWidth()) {
            val (txt, setTxt) = remember { mutableStateOf("") }
            var txt2 by remember { mutableStateOf("") }
            EnterCard(text = txt, setTxt = setTxt)
            if (txt.length > 2) {
                values.forEach { ValueCard(s = it) }
                EnterCard(
                    text = txt2,
                    anotherText = Constants.POWER_STRING,
                    setTxt = { txt2 = it }
                )
            }
        }
        VerticalGrid(Modifier.fillMaxWidth()) {
            val (txt, setTxt) = remember { mutableStateOf("") }
            var txt2 by remember { mutableStateOf("") }
            EnterCard(text = txt, setTxt = setTxt)
            if (txt.length > 2) {
                values.forEach { ValueCard(s = it) }
                EnterCard(
                    text = txt2,
                    anotherText = Constants.POWER_STRING,
                    setTxt = { txt2 = it }
                )
            }
        }
        VerticalGrid(Modifier.fillMaxWidth()) {
            val (txt, setTxt) = remember { mutableStateOf("") }
            var txt2 by remember { mutableStateOf("") }
            EnterCard(text = txt, setTxt = setTxt)
            if (txt.length > 2) {
                values.forEach { ValueCard(s = it) }
                EnterCard(
                    text = txt2,
                    anotherText = Constants.POWER_STRING,
                    setTxt = { txt2 = it }
                )
            }
        }
    }
}


@Composable
fun EnterCard(
    modifier: Modifier = Modifier,
    text: String,
    anotherText: AnnotatedString = buildAnnotatedString { append("") },
    setTxt: (String) -> Unit
) {
    BasicTextField(
        modifier = modifier,
        value = text,
        onValueChange = setTxt,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            autoCorrect = false,
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(onNext = {

        }),
        decorationBox = { innerTextField ->
            Card(
                modifier = Modifier
                    .size(60.dp, 35.dp)
                    .padding(2.dp),
                backgroundColor = Color(0xFFB3E5FC),
                contentColor = Color(0xFF01579B),
                border = BorderStroke(width = 1.dp, color = Color(0xFF01579B)),
                content = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart)
                    {
                        innerTextField()
                        Text(modifier = Modifier.align(Alignment.CenterEnd).padding(horizontal = 3.dp),text = anotherText, style = MaterialTheme.typography.caption)
                    }
                }
            )
        }
    )
}


@Composable
fun ValueCard(s: AnnotatedString, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(50.dp, 35.dp)
            .padding(1.dp),
        backgroundColor = Color(0xFFB3E5FC),
        contentColor = Color(0xFF01579B),
        border = BorderStroke(width = 1.dp, color = Color(0xFF01579B)),
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = s,
                modifier = Modifier.padding(2.dp),
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
fun HeadCard(s: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .size(50.dp, 40.dp)
            .padding(1.dp),
        backgroundColor = Color(0xFFFFCDD2),
        contentColor = Color(0xFFB71C1C),
        border = BorderStroke(width = 1.dp, color = Color(0xFFB71C1C))
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = s, modifier = Modifier
                    .fillMaxWidth()
                    .padding(2.dp),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    columns: Int = 6,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->

        val itemWidth = constraints.maxWidth / columns
        // Keep given height constraints, but set an exact width
        val itemConstraints = constraints.copy(
            minWidth = itemWidth,
            maxWidth = itemWidth
        )
        // Measure each item with these constraints
        val placeables = measurables.map { it.measure(itemConstraints) }
        // Track each columns height so we can calculate the overall height
        val columnHeights = Array(columns) { 0 }
        placeables.forEachIndexed { index, placeable ->
            val column = index % columns
            columnHeights[column] += placeable.height
        }
        val height = (columnHeights.maxOrNull() ?: constraints.minHeight)
            .coerceAtMost(constraints.maxHeight)
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            // Track the Y co-ord per column we have placed up to
            val columnY = Array(columns) { 0 }
            placeables.forEachIndexed { index, placeable ->
                val column = index % columns
                placeable.place(
                    x = column * itemWidth,
                    y = columnY[column]
                )
                columnY[column] += placeable.height
            }
        }
    }
}