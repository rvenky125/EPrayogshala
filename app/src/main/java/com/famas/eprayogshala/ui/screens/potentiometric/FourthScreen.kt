package com.famas.eprayogshala.ui.screens.potentiometric

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.famas.eprayogshala.R
import com.famas.eprayogshala.ui.components.TitrationView
import com.famas.eprayogshala.util.MyCrossfade
import com.famas.eprayogshala.util.Screens.*
import java.math.BigDecimal

@ExperimentalFoundationApi
@Composable
fun FourthScreen(
    isPhmetric: Boolean = false,
    listPilot: List<Float>,
    listAccurate: List<Float>,
    totVolAddingSol: BigDecimal,
    setTotVol: (BigDecimal) -> Unit,
    showSnack: (String) -> Unit,
    onSubClick: () -> Unit,
) {

    ConstraintLayout {
        var currentScreen by remember { mutableStateOf(SCREEN1) }
        var title by remember { mutableStateOf("Pilot titration") }
        val scrollState = rememberScrollState()

        val addingSolLimit: BigDecimal =
            if (currentScreen == SCREEN1) {
                if (!isPhmetric) 6.0.toBigDecimal() else 3.0.toBigDecimal()
            }
            else {
                if (!isPhmetric) 5.5.toBigDecimal() else ((listAccurate.size-2) * 0.1).toBigDecimal()
            }

        Log.d("myTag", "add sol limit: $addingSolLimit")

        //Intro layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(scrollState)
        )
        {

            Text(modifier = Modifier
                .fillMaxWidth(), text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append( if(!isPhmetric) "Step2: " else "Step3: ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(
                        if (!isPhmetric) " Determination of ferrous ammonium sulphate with standard potassium dichromate solution by a potentiometric titration"
                        else "pH metric titration of hydrochloric cid solution with standardized sodium hydroxide solution"
                    )
                }
            })

            Spacer(modifier = Modifier.height(15.dp))
            Text(text = buildAnnotatedString {
                append("In this step we are going to do two titrations. They are")
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(" 1.Pilot titration  2.Accurate Titration")
                }
            })

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = buildAnnotatedString {
                append("Now we are going to do $title. You can ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Double click")
                }
                append(" on burette to add ")

                if (!isPhmetric) {
                    append("K")
                    withStyle(
                        SpanStyle(
                            fontSize = 10.sp,
                            baselineShift = BaselineShift.Subscript
                        )
                    ) { append("2") }
                    append("Cr")
                    withStyle(
                        SpanStyle(
                            fontSize = 10.sp,
                            baselineShift = BaselineShift.Subscript
                        )
                    ) { append("2") }
                    append("O")
                    withStyle(
                        SpanStyle(
                            fontSize = 10.sp,
                            baselineShift = BaselineShift.Subscript
                        )
                    ) { append("7") }
                }
                else append("NaOH")
            })

            Spacer(modifier = Modifier.height(25.dp))
            Text(text = title, style = MaterialTheme.typography.h6)

            //Changing screen
            MyCrossfade(
                targetState = currentScreen
            ) {
                when (it) {
                    SCREEN1 -> {
                        Log.d("myTag", "index: ${(totVolAddingSol.div(1.0.toBigDecimal())).toInt()}")
                        TitrationView(
                            firstImg = if(isPhmetric) R.drawable.phmeter else R.drawable.potentiometer,
                            addText = "+1 ml",
                            listItem = listPilot[(totVolAddingSol.div(1.0.toBigDecimal())).toInt()].toString(),
                            readingName = if (isPhmetric) "pH : " else "Potential"
                        ) {
                            if (totVolAddingSol < addingSolLimit) {
                                setTotVol(totVolAddingSol.plus(1.0.toBigDecimal()))
                            } else showSnack("Click continue to go to the next step")
                        }
                    }

                    SCREEN2 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Well done!",
                                style = MaterialTheme.typography.h6,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "You have completed Pilot titration. Now you have to do Accurate titration.",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Click continue to go next titration")
                        }
                    }

                    SCREEN3 -> {
                        Log.d("myTag", "Index is: ${(totVolAddingSol.rem(3.0.toBigDecimal()) * 10.toBigDecimal()).toInt()}")
                        TitrationView(
                            firstImg = if(isPhmetric) R.drawable.phmeter else R.drawable.potentiometer,
                            addText = "+0.1 ml",
                            listItem = listAccurate[(totVolAddingSol.rem(3.0.toBigDecimal()) * 10.toBigDecimal()).toInt()].toString(),
                            readingName = if (isPhmetric) "pH : " else "Potential"
                        ) {
                            if (totVolAddingSol < addingSolLimit) {
                                setTotVol(totVolAddingSol.plus(0.1.toBigDecimal()))
                            }
                            else showSnack("Click on Submit")
                        }
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                style = MaterialTheme.typography.subtitle1,
                text = buildAnnotatedString {
                    append("Total volume of ")
                    if (!isPhmetric) {
                        append("K")
                        withStyle(
                            SpanStyle(
                                fontSize = 10.sp,
                                baselineShift = BaselineShift.Subscript
                            )
                        ) { append("2") }
                        append("Cr")
                        withStyle(
                            SpanStyle(
                                fontSize = 10.sp,
                                baselineShift = BaselineShift.Subscript
                            )
                        ) { append("2") }
                        append("O")
                        withStyle(
                            SpanStyle(
                                fontSize = 10.sp,
                                baselineShift = BaselineShift.Subscript
                            )
                        ) { append("7") }
                        append(" added: ")
                    }
                    else append("NaOH added : ")
                    withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.SemiBold)) {
                        append(totVolAddingSol.toString().substring(0, 3))
                    }
                })
        }

        val btn = createRef()
        Button(
            modifier = Modifier
                .constrainAs(btn) { bottom.linkTo(parent.bottom, 16.dp) }
                .fillMaxWidth()
                .padding(10.dp),
            onClick = {
                when (currentScreen) {
                    SCREEN1 -> currentScreen = SCREEN2
                    SCREEN2 -> {
                        setTotVol(if (!isPhmetric) 3.0.toBigDecimal() else 0.0.toBigDecimal())
                        currentScreen = SCREEN3
                        title = "Accurate titration"
                    }
                    SCREEN3 -> onSubClick()
                    else -> {}
                }
            },
            enabled = currentScreen == SCREEN2 || (currentScreen == SCREEN1 && totVolAddingSol >= addingSolLimit) || (currentScreen == SCREEN3 && totVolAddingSol >= addingSolLimit),
            shape = CircleShape
        ) {
            Text(text = if (currentScreen == SCREEN3) "Submit" else "Continue")
        }
    }
}