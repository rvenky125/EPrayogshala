package com.famas.vlabschemistrymvgr.ui.screens.conductometric

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.famas.vlabschemistrymvgr.R
import com.famas.vlabschemistrymvgr.util.loadPicture

@Composable
fun FirstScreen(
    w1: String,
    w2: String,
    m1: String,
    subst: Float,
    expName: String,
    aimStr: String,
    step1Str: String,
    w1Label: String,
    w2Label: String,
    m1Label: String,
    gmw: Float = 126f,
    picText: String = "Oxalic Acid sol",
    m1MulFactor: Int = 10,
    setW1: (String) -> Unit,
    setW2: (String) -> Unit,
    setM1: (String) -> Unit,
    setSubst: (Float) -> Unit,
    showSnack: (String) -> Unit,
    onSubClick: () -> Unit,
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .verticalScroll(scrollState)
    ) {
        val (name, aim, proc_head, img, step1, textFields, sub_btn) = createRefs()

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(name) { top.linkTo(parent.top, 15.dp) },
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append("Name of the experiment: ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(expName)
                }
            }
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(aim) { top.linkTo(name.bottom, 15.dp) },
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append("AIM: ")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(aimStr)
                }
            }
        )

        Text(
            text = "PROCEDURE:",
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.constrainAs(proc_head) {
                start.linkTo(parent.start)
                top.linkTo(aim.bottom, 25.dp)
            }
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(step1) { top.linkTo(proc_head.bottom, 15.dp) },
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                    append("Step 1:")
                }
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(step1Str)
                }
            }
        )

        Column(
            modifier = Modifier.constrainAs(img) {
                top.linkTo(step1.bottom, 20.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            loadPicture(imageRes = R.drawable.flask).value?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .fillMaxHeight(0.2f),
                    bitmap = it.asImageBitmap(),
                    contentDescription = ""
                )
            }
            Text(
                text = picText,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption
            )
        }

        Column(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(textFields) {
                top.linkTo(img.bottom, 10.dp)
            }
        ) {
            Text(text = "Enter the observed values below")

            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "W1 = ")
                OutlinedTextField(
                    label = { Text(text = w1Label) },
                    value = w1,
                    onValueChange = setW1,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "W2 = ")
                OutlinedTextField(
                    label = { Text(text = w2Label) },
                    value = w2,
                    onValueChange = setW2,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }

            try {
                if (w1.isNotBlank() && w2.isNotBlank())
                    setSubst(w1.trimIndent().toFloat() - w2.trimIndent().toFloat())
            } catch (e: Exception) {
                setW1("")
                setW2("")
                Toast.makeText(
                    context,
                    "Please enter values without '-' and ',' ",
                    Toast.LENGTH_SHORT
                ).show()
                Toast.makeText(context, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "W1 -W2 is: ",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
                )
                if (w1.isNotBlank() && w2.isNotBlank())
                    Text(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Blue,
                        text = "$subst"
                    )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.padding(vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "M1 = ")
                OutlinedTextField(
                    label = { Text(text = m1Label) },
                    value = m1,
                    onValueChange = setM1,
                    keyboardOptions = KeyboardOptions(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(sub_btn) {
                    top.linkTo(textFields.bottom, 16.dp)
                    bottom.linkTo(parent.bottom, 16.dp)
                },
            onClick = {
                try {
                    val finalM1 = subst / gmw * m1MulFactor
                    if (finalM1.minus(m1.trim().toFloat()) in -0.003f..0.003f) onSubClick()
                    else showSnack("Please insert correct M1 value: $finalM1")
                } catch (e: Exception) {
                    setM1("")
                    showSnack("Please enter M1 appropriately")
                }
            },
            shape = CircleShape,
            enabled = m1.isNotBlank() && w1.isNotBlank() && w2.isNotBlank()
        ) {
            Text(text = "Continue")
        }
    }
}