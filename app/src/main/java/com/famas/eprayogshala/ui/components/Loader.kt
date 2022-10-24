package com.famas.eprayogshala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CircularLoaderDialog(message: String = "Loading") {
    Dialog(
        onDismissRequest = { /*TODO*/ },
        properties = DialogProperties(dismissOnBackPress = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(70.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CircularProgressIndicator()
                Text(text = message, style = MaterialTheme.typography.subtitle1)
            }
        }
    }
}

@Composable
fun LinearProgressLoader(
    progress: Float = 0.0f,
    message: String = "Downloading reviewed files"
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                if (progress != 0.0f) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp),
                        progress = progress
                    )
                }
                else LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(text = message, style = MaterialTheme.typography.caption)
            }
        }
    }
}