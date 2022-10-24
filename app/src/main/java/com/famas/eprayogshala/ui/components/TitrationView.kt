package com.famas.eprayogshala.ui.components

import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.famas.eprayogshala.R
import com.famas.eprayogshala.util.loadPicture

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitrationView(
    firstImg: Int,
    addText: String,
    listItem: String,
    readingName: String,
    modifier: Modifier = Modifier,
    buretteClick: () -> Unit
) {
    val draggableState = rememberDraggableState(onDelta = {
        Log.d("myTag", "Drag amount: $it")
    })

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var scale by remember { mutableStateOf(0f) }
        val animatedScale by animateFloatAsState(
            targetValue = scale,
            animationSpec = tween(durationMillis = 800),
            finishedListener = { scale = 0f })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                loadPicture(imageRes = firstImg).value?.let {
                    Image(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        bitmap = it.asImageBitmap(),
                        contentDescription = ""
                    )
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .scale(animatedScale),
                    text = addText,
                    color = Color.Green
                )
            }

            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = buildAnnotatedString {
                    append(readingName)
                    withStyle(SpanStyle(color = Color.Red)) {
                        append(listItem)
                    }
                },
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold)
            )
        }

        loadPicture(imageRes = R.drawable.burette).value?.let {
            Image(
                modifier = Modifier
                    .combinedClickable(
                        onDoubleClick = {
                            buretteClick()
                            scale = 2f
                        }, onClick = {})
                    .fillMaxWidth(0.3f)
                    .draggable(draggableState, orientation = Orientation.Horizontal)
                ,
                bitmap = it.asImageBitmap(),
                contentDescription = "burette"
            )
        }
    }
}