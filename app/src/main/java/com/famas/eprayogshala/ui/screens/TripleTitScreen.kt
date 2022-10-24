package com.famas.eprayogshala.ui.screens

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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.famas.eprayogshala.R
import com.famas.eprayogshala.ui.components.TitrationView
import com.famas.eprayogshala.util.MyCrossfade
import java.math.BigDecimal

@Composable
fun TripleTitScreen(
    tit1Threshold: BigDecimal,
    tit2Threshold: BigDecimal,
    tit3Threshold: BigDecimal,
    showSnack: (String) -> Unit,
    onClickSubmit: () -> Unit
) {
    ConstraintLayout {
        var currentScreen by remember { mutableStateOf(TripleTitScreen.TIT1) }
        val scrollState = rememberScrollState()
        var addedSol by remember { mutableStateOf(0.0.toBigDecimal()) }
        var preThreshold by remember { mutableStateOf(tit1Threshold) }

        //Intro layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(scrollState)
        ) {

            Text(modifier = Modifier
                .fillMaxWidth(), text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append("Step3: ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(
                        "Determination of total Hardness of water"
                    )
                }
            })

            Spacer(modifier = Modifier.height(15.dp))
            Text(text = buildAnnotatedString {
                append("In this step we are going to do titrations with three samples.")
            })

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = buildAnnotatedString {
                append("Now we are going to do ${currentScreen}. You can ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Double click")
                }
                append(" on burette to add NaOH")
            })

            Spacer(modifier = Modifier.height(25.dp))

            //Changing screen
            MyCrossfade(
                targetState = currentScreen
            ) {
                when (it) {
                    TripleTitScreen.TIT1 -> {
                        Column {
                            Text(
                                text = "Do the Titration below",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.padding(2.dp)
                            )
                            Text(
                                text = "Double click on the burette to add ${if (addedSol < 8.0.toBigDecimal()) "1.0 ml" else "0.1 ml"} of NaOH",
                                style = MaterialTheme.typography.caption
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TitrationView(
                                firstImg = if (addedSol == preThreshold) R.drawable.flask_sol_wine_red else R.drawable.flask_sol_pale_blue,
                                addText = if (addedSol < 8.0.toBigDecimal()) "+1.0 ml" else "+0.1 ml",
                                listItem = "$addedSol ml",
                                readingName = "NaOH added : "
                            ) {
                                if (addedSol == preThreshold) showSnack("Titration completed")
                                else {
                                    addedSol += if (addedSol < 8.0.toBigDecimal()) 1.0.toBigDecimal() else 0.1.toBigDecimal()
                                }
                            }
                        }
                    }

                    TripleTitScreen.NONE1 -> {
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
                                text = "You have completed titration. Now you have to do another titration.",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Click continue to go next titration")
                        }
                    }

                    TripleTitScreen.NONE2 -> {
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
                                text = "You have completed titration. Now you have to do another titration.",
                                style = MaterialTheme.typography.subtitle2
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Click continue to go next titration")
                        }
                    }

                    TripleTitScreen.TIT2 -> {
                        Column {
                            Text(
                                text = "Do the Titration below",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.padding(2.dp)
                            )
                            Text(
                                text = "Double click on the burette to add ${if (addedSol < 3.0.toBigDecimal()) "1.0 ml" else "0.1 ml"} of NaOH",
                                style = MaterialTheme.typography.caption
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TitrationView(
                                firstImg = if (addedSol == preThreshold) R.drawable.flask_sol_wine_red else R.drawable.flask_sol_pale_blue,
                                addText = if (addedSol < 3.0.toBigDecimal()) "+1.0 ml" else "+0.1 ml",
                                listItem = "$addedSol ml",
                                readingName = "NaOH added : "
                            ) {
                                if (addedSol == preThreshold) showSnack("Titration completed")
                                else {
                                    addedSol += if (addedSol < 3.0.toBigDecimal()) 1.0.toBigDecimal() else 0.1.toBigDecimal()
                                }
                            }
                        }
                    }

                    TripleTitScreen.TIT3 -> {
                        Column {
                            Text(
                                text = "Do the Titration below",
                                style = MaterialTheme.typography.h5,
                                modifier = Modifier.padding(2.dp)
                            )
                            Text(
                                text = "Double click on the burette to add 0.1 ml of NaOH",
                                style = MaterialTheme.typography.caption
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            TitrationView(
                                firstImg = if (addedSol == preThreshold) R.drawable.flask_sol_wine_red else R.drawable.flask_sol_pale_blue,
                                addText = "+0.1 ml",
                                listItem = "$addedSol ml",
                                readingName = "NaOH added : "
                            ) {
                                if (addedSol == preThreshold) showSnack("Titration completed")
                                else {
                                    addedSol += 0.1.toBigDecimal()
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                style = MaterialTheme.typography.subtitle1,
                text = buildAnnotatedString {
                    append("Total volume of NaOH added : ")
                    withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.SemiBold)) {
                        append(addedSol.toString())
                    }
                })
        }



        val btn = createRef()
        val isButtonEnabled = (currentScreen == TripleTitScreen.TIT1 && addedSol >= preThreshold)
                || (currentScreen == TripleTitScreen.TIT2 && addedSol >= preThreshold)
                || (currentScreen == TripleTitScreen.TIT3 && addedSol >= preThreshold)
                || (currentScreen == TripleTitScreen.NONE1 || currentScreen == TripleTitScreen.NONE2)

        Button(
            modifier = Modifier
                .constrainAs(btn) { bottom.linkTo(parent.bottom, 16.dp) }
                .fillMaxWidth()
                .padding(10.dp),
            onClick = {
                when (currentScreen) {
                    TripleTitScreen.TIT1 -> {
                        addedSol = 0.toBigDecimal()
                        preThreshold = tit2Threshold
                        currentScreen = TripleTitScreen.NONE1
                    }
                    TripleTitScreen.NONE1 -> currentScreen = TripleTitScreen.TIT2
                    TripleTitScreen.TIT2 -> {
                        addedSol = 0.toBigDecimal()
                        preThreshold = tit3Threshold
                        currentScreen = TripleTitScreen.NONE2
                    }
                    TripleTitScreen.NONE2 -> currentScreen = TripleTitScreen.TIT3
                    TripleTitScreen.TIT3 -> onClickSubmit()
                }
            },
            enabled = isButtonEnabled,
            shape = CircleShape
        ) {
            Text(text = if (currentScreen == TripleTitScreen.TIT3) "Submit" else "Continue")
        }
    }
}


enum class TripleTitScreen {
    TIT1, TIT2, TIT3, NONE1, NONE2
}