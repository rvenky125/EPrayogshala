package com.famas.vlabschemistrymvgr.ui.screens.conductometric

import android.widget.ImageView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import com.bumptech.glide.Glide
import com.famas.vlabschemistrymvgr.R
import com.famas.vlabschemistrymvgr.ui.components.TitrationView


@ExperimentalFoundationApi
@Composable
fun SecondScreen(
    m1: String,
    v1: String,
    n1: String,
    m2: String,
    v2: Double,
    n2: String,
    setV1: (String) -> Unit,
    setN1: (String) -> Unit,
    setM2: (String) -> Unit,
    setN2: (String) -> Unit,
    step2String: String = " Standardization of sodium hydroxide solution supplied",
    v1Label: String = "Volume of oxalic acid",
    n1Label: String = "Number of moles of oxalic acid",
    m1Label: String = "Sodium hydroxide",
    m2Label: String = "molarity of Oxalic acid",
    addSolText: String = "NaOH",
    showSnack: (String) -> Unit,
    onPrevClick: () -> Unit,
    onSubClick: () -> Unit
) {
    var addedV2 by remember { mutableStateOf(0.0.toBigDecimal()) }

    val constrains = ConstraintSet {
        val step2Lt = createRefFor("step2_lt")
        val titImg = createRefFor("tit_img")
        val titImgTxt = createRefFor("tit_img_txt")
        val obsValLt = createRefFor("obs_val_lt")
        val nextBtn = createRefFor("next_btn")
        val prevBtn = createRefFor("prev_btn")
        val finalValLt = createRefFor("final_val_lt")
        val titView = createRefFor("tit_view")

        constrain(step2Lt) {
            top.linkTo(parent.top, 20.dp)
            width = Dimension.fillToConstraints
        }
        constrain(titImg) {
            top.linkTo(step2Lt.bottom, 20.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.fillToConstraints
        }
        constrain(titImgTxt) {
            top.linkTo(titImg.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(titView) {
            top.linkTo(titImgTxt.bottom, 25.dp)
        }
        constrain(obsValLt) {
            top.linkTo(titView.bottom, 15.dp)
            width = Dimension.fillToConstraints
        }
        constrain(nextBtn) {
            top.linkTo(finalValLt.bottom, 16.dp)
            bottom.linkTo(parent.bottom, 16.dp)
        }
        constrain(prevBtn) {
            top.linkTo(finalValLt.bottom, 16.dp)
            start.linkTo(parent.start, 6.dp)
            bottom.linkTo(parent.bottom, 6.dp)
        }
        constrain(finalValLt) {
            top.linkTo(obsValLt.bottom, 20.dp)
            width = Dimension.fillToConstraints
        }
    }

    val scrollState = rememberScrollState()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(scrollState), constraintSet = constrains
    )
    {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .layoutId("step2_lt"),
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append("Step 2:")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(step2String)
                }
            }
        )

        val context = LocalContext.current
        AndroidView(
            modifier = Modifier.layoutId("tit_img"),
            factory = {
                ImageView(it)
            }) {
            Glide.with(context).asGif().load(R.drawable.titration).into(it)
        }

        Text(
            modifier = Modifier.layoutId("tit_img_txt"),
            text = "Titration Example",
            style = MaterialTheme.typography.caption
        )

        //TODO
        //Should titration
        Column(modifier = Modifier.layoutId("tit_view")) {
            Text(
                text = "Do the Titration below",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(2.dp)
            )
            Text(
                text = "Double click on the burette to add ${if (addedV2 < 9.0.toBigDecimal()) "1.0 ml" else "0.1 ml"} of $addSolText",
                style = MaterialTheme.typography.caption
            )
            Spacer(modifier = Modifier.height(10.dp))
            TitrationView(
                firstImg = if (addedV2 == v2.toBigDecimal()) {
                    R.drawable.flask_sol_pink
                } else {
                    R.drawable.flask_sol
                },
                addText = if (addedV2 < 9.0.toBigDecimal()) "+1.0 ml" else "+0.1 ml",
                listItem = "$addedV2 ml",
                readingName = "$addSolText added : "
            ) {
                if (addedV2 == v2.toBigDecimal()) showSnack("Titration completed")
                else {
                    addedV2 += if (addedV2 < 9.0.toBigDecimal()) 1.0.toBigDecimal() else 0.1.toBigDecimal()
                }
            }
        }

        Column(modifier = Modifier.layoutId("obs_val_lt"))
        {
            Text(text = "Enter the observed values below")

            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "M1(Molarity of $m1Label) = ")
                Text(text = m1, fontWeight = FontWeight.SemiBold, color = Color.Blue)
            }
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "V1 = ")
                OutlinedTextField(
                    label = { Text(text = v1Label) },
                    value = v1,
                    onValueChange = setV1,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number
                    )
                )
                Text(text = " ml")
            }

            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "n1 = ")
                OutlinedTextField(
                    label = { Text(text = n1Label) },
                    value = n1,
                    onValueChange = setN1,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                text = buildAnnotatedString {
                    append("V2(Volume of $addSolText) = ")
                    withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.SemiBold)) {
                        append(if (addedV2 == v2.toBigDecimal()) "$v2 ml" else "")
                    }
                }
            )

            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "n2 = ")
                OutlinedTextField(
                    label = { Text(text = "Number of moles of moles of $addSolText sol") },
                    value = n2,
                    onValueChange = setN2,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        Column(
            modifier = Modifier.layoutId("final_val_lt"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calculate the $m2Label and enter below",
                style = MaterialTheme.typography.subtitle1,
                color = Color.Blue
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "M2 = ")
                OutlinedTextField(
                    label = { Text(text = m2Label) },
                    value = m2,
                    onValueChange = setM2,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        var calculatedM2 = ""
        try {
            if (v1.isNotBlank() && v1.isNotBlank() && n2.isNotBlank() && n1.isNotBlank())
                calculatedM2 =
                    ((m1.toFloat() * v1.toFloat() * n2.toFloat()) / v2 * n1.toFloat()).toString()
                        .substring(0, 5)
        } catch (e: Exception) {
            setV1("")
            setN1("")
            setN2("")
            setM2("")
            showSnack("Please enter appropriate values")
        }
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .layoutId("next_btn"),
            onClick = {
                if (m2.length > 3 && calculatedM2.length > 3) {
                    if (m2.substring(0, 4) == calculatedM2.substring(0, 4)) {
                        onSubClick()
                    } else showSnack("please enter correct M2 value")
                } else showSnack("Please enter at least two digits after dot in M2")
            },
            shape = CircleShape,
            enabled = m2.isNotBlank() && v1.isNotBlank() && v1.isNotBlank() && n2.isNotBlank() && n1.isNotBlank() && addedV2 == v2.toBigDecimal()
        ) {
            Text(text = "Continue")
        }
    }
}