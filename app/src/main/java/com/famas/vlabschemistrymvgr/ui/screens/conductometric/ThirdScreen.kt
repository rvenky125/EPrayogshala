package com.famas.vlabschemistrymvgr.ui.screens.conductometric

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.famas.vlabschemistrymvgr.R
import com.famas.vlabschemistrymvgr.ui.components.TitrationView

@ExperimentalFoundationApi
@Composable
fun ThirdScreen(
    id: String,
    totVolNaOH: Float,
    setTotVol: (Float) -> Unit,
    showSnack: (String) -> Unit,
    conductances: List<Float>,
    onSubClick: () -> Unit,
) {

    ConstraintLayout(modifier = Modifier.padding(horizontal = 10.dp)) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        )
        {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                        append("Step 3:")
                    }
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp)) {
                        append(if (id == "0") " Conductometric titration of hydrochloric acid solution with standardized sodium hydroxide solution" else " Conductometric titration of Acetic acid solution with standardized sodium hydroxide solution")
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Now you  have to get ready for Conductometric titration. Double click on the burette to add 0.5ml of base to the acid",
                    color = Color.Blue
                )
                Spacer(modifier = Modifier.height(7.dp))
                Text(
                    text = "* Please note the initial readings before clicking on burette",
                    color = Color.Red
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            TitrationView(
                firstImg = R.drawable.conductometer,
                listItem = conductances[(totVolNaOH / 0.5f).toInt()].toString(),
                addText = "0.5 ml",
                readingName = "Conductance: "
            ) {
                if (totVolNaOH < 8.0f) setTotVol(totVolNaOH + 0.5f)
                else showSnack("You are not able to add NaOH")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("Total volume of NaOH added = ")
                    }
                    withStyle(SpanStyle(color = Color.Blue, fontWeight = FontWeight.Bold)) {
                        append(totVolNaOH.toString())
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "* Please do calculate the values of C' and plot the corresponding graph and calculate the concentration of acid",
                color = Color.Red,
            )
        }

        val btn = createRef()
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(btn) {
                    bottom.linkTo(parent.bottom, 10.dp)
                },
            shape = CircleShape,
            onClick = { onSubClick() },
            enabled = totVolNaOH == 8.0f
        ) {
            Text(text = "Submit")
        }
    }
}