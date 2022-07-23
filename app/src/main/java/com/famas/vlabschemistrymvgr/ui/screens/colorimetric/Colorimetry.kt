package com.famas.vlabschemistrymvgr.ui.screens.colorimetric

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.famas.vlabschemistrymvgr.R
import com.famas.vlabschemistrymvgr.ui.components.ColorimetryBox
import com.famas.vlabschemistrymvgr.ui.components.EnterCard
import com.famas.vlabschemistrymvgr.ui.components.HeadCard
import com.famas.vlabschemistrymvgr.ui.components.ValueCard
import com.famas.vlabschemistrymvgr.util.Constants
import com.famas.vlabschemistrymvgr.util.loadPicture
import com.famas.vlabschemistrymvgr.util.titrationvalues.getRandomColorimetricValues
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@ExperimentalAnimationApi
@Composable
fun Colorimetry(
    modifier: Modifier = Modifier,
    onClickSubmit: (String, String) -> Unit
) {
    var finalM by remember { mutableStateOf("") }
    val list = remember { getRandomColorimetricValues() }
    val ukValue = rememberSaveable { Random.nextDouble(0.55, 2.00).toString().substring(0, 5) }
    var index by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showUkValue by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .animateContentSize()
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append("Name of the experiment: ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("Colorimetric Titration")
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append("AIM: ")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("To determine the amount of iron in the given test and calculate the percentage of iron")
                    }
                }
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append("Step 1:")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append(" Preparation of 0.01m stock solution of ferric salt")
                    }
                }
            )
            Spacer(modifier = Modifier.height(5.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                loadPicture(imageRes = R.drawable.colorimeter).value?.let {
                    Image(bitmap = it.asImageBitmap(), contentDescription = "")
                }
                Text(text = "Colorimeter", style = MaterialTheme.typography.caption)
            }

            Spacer(modifier = Modifier.height(5.dp))

            if (index > 0) {
                Text(text = buildAnnotatedString {
                    append("Molarity of Fe(III) stock solution: ")
                    withStyle(SpanStyle(color = Color.Blue)) {
                        append("0.01M")
                    }
                })
                Spacer(modifier = Modifier.height(25.dp))
            }

            if (index > 1) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                            append("Step 2:")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append(" Construction of standardized curve of Beer's law")
                        }
                    }
                )
                Spacer(modifier = Modifier.height(5.dp))
                //for step2 picture
                loadPicture(imageRes = R.drawable.flasks).value?.let {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        bitmap = it.asImageBitmap(),
                        contentDescription = ""
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
            }

            if (index > 2) {
                Text(text = buildAnnotatedString {
                    append("Volume of the stock solution : ")
                    withStyle(SpanStyle(color = Color.Blue)) {
                        append("5.5 ml")
                    }
                })
            }
            if (index > 3) {
                Text(text = buildAnnotatedString {
                    append("Total volume of the final solution : ")
                    withStyle(SpanStyle(color = Color.Blue)) {
                        append("100 ml")
                    }
                })
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Molarity of the final solution = ")
                    EnterCard(
                        modifier = Modifier.weight(1f),
                        text = finalM,
                        setTxt = { finalM = it },
                        anotherText = Constants.POWER_STRING
                    )
                }
            }

            if (index > 4) {
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                            append("Step 3 :")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append(" Preparation of cement sample and development of color")
                        }
                    }
                )
            }

            if (index > 5) {
                Spacer(modifier = Modifier.height(5.dp))
                ColorimetryBox()
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (index > 6) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                            append("Step 4 :")
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append(" Determination of ferric iron in the given test sample")
                        }
                    }
                )
            }

            if (index > 7) {

                Text(text = "Enter the observed values below")
                Column {
                    Row(Modifier.fillMaxWidth()) {
                        HeadCard(s = "Molarity of solution", modifier = Modifier.weight(0.5f))
                        HeadCard(s = "Optical Density", modifier = Modifier.weight(0.5f))
                    }
                    repeat(4) {
                        Row(Modifier.fillMaxWidth()) {
                            var mySol by remember { mutableStateOf("") }
                            EnterCard(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .weight(0.5f)
                                    .padding(horizontal = 5.dp),
                                text = mySol,
                                anotherText = Constants.POWER_STRING,
                                setTxt = { mySol = it }
                            )
                            if (mySol.length <= 2) {
                                Spacer(modifier = Modifier.weight(0.5f))
                            }
                            AnimatedVisibility(
                                visible = mySol.length > 2,
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(horizontal = 5.dp)
                            ) {
                                ValueCard(s = buildAnnotatedString { append(list[it].toString()) })
                                if (it == 3) showUkValue = true
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                AnimatedVisibility(visible = showUkValue) {
                    Text(text = buildAnnotatedString {
                        append("Optical density of unknown cement sample solution: ")
                        withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                            append(ukValue)
                        }
                    })
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (index == 4 && finalM.isBlank()) {
                        Toast.makeText(context, "Molarity of final solution is empty", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        if (index == 9) {
                            if (finalM.isNotBlank() && showUkValue) {
                                onClickSubmit(finalM, ukValue)
                            }
                            else
                                Toast.makeText(context, "Some fields are empty", Toast.LENGTH_SHORT).show()
                        }
                        else {
                            index++
                            coroutineScope.launch {
                                delay(400)
                                scrollState.animateScrollTo(
                                    scrollState.maxValue,
                                    animationSpec = tween(
                                        durationMillis = 700,
                                        easing = FastOutSlowInEasing
                                    )
                                )
                            }
                        }
                    }
                },
                shape = CircleShape
            ) {
                Text(text = if (index == 9) "submit" else "continue")
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}