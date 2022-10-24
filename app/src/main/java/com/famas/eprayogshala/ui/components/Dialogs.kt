package com.famas.eprayogshala.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TakePhotoDialog(
    onGoBack: () -> Unit,
    onOk: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(onClick = { onOk() }) {
                Text(text = "Ok")
            }
        },
        dismissButton = {
            Button(onClick = { onGoBack() }) {
                Text(text = "Go back")
            }
        },
        title = {
            Text(text = "Take photo")
        },
        text = {
            Text(text = "You have to take your photo to continue to the experiment")
        }
    )
}


@Composable
fun ExitDialog(
    onCancel: () -> Unit,
    onExit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(onClick = { onExit() }) {
                Text(text = "Exit")
            }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Exit dialog")
        },
        text = {
            Text(text = "Do you really wanna exit from experiment")
        }
    )
}


@Composable
fun AskForSubmit(
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        confirmButton = {
            Button(onClick = { onSubmit() }) {
                Text(text = "Submit")
            }
        },
        dismissButton = {
            Button(onClick = { onCancel() }) {
                Text(text = "Cancel")
            }
        },
        title = {
            Text(text = "Submission")
        },
        text = {
            Text(text = "You are willing to submit your report")
        }
    )
}