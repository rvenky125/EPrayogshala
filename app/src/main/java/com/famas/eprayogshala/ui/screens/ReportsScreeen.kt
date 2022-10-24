package com.famas.eprayogshala.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.famas.eprayogshala.ui.components.FileItemCard
import com.famas.eprayogshala.ui.components.NothingHere
import com.famas.eprayogshala.util.NetworkListener
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun ReportsScreen(
    filesDir: File,
    callForReviewedFiles: () -> Unit,
    showPdf: (File) -> Unit
) {
    val isNetworkAvailable by NetworkListener.checkNetworkAvailability(LocalContext.current)
        .collectAsState(true)
    var tabIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = true) {
        delay(800)
        callForReviewedFiles()
    }

    Column {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(
                text = { Text(text = "Not Submitted") },
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 })

            Tab(text = { Text(text = "Submitted") },
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 })

            Tab(text = { Text(text = "Reviewed") },
                selected = tabIndex == 2,
                onClick = { tabIndex = 2 })
        }

        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "Swipe to delete a file",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ) {
            when (tabIndex) {
                0 -> {
                    var pdfsList by remember {
                        mutableStateOf(
                            filesDir.listFiles { _, n ->
                                n.contains(".pdf") && !n.contains("_sub") && !n.contains("_rev")
                            }
                        )
                    }

                    if (!pdfsList.isNullOrEmpty()) {
                        LazyColumn {
                            items<File>(pdfsList) { file ->
                                val swipeState = rememberDismissState()
                                SwipeToDismiss(
                                    state = swipeState,
                                    background = {
                                        SwipeBackground(
                                            onDelete = {
                                                file.delete()
                                                pdfsList = filesDir.listFiles { _, n ->
                                                    n.contains(".pdf") && !n.contains("_sub") && !n.contains(
                                                        "_rev"
                                                    )
                                                }
                                            },
                                            onCancel = { coroutineScope.launch { swipeState.reset() } }
                                        )
                                    }
                                ) {
                                    FileItemCard(
                                        name = file.name
                                    ) {
                                        showPdf(file)
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(90.dp))
                            }
                        }
                    } else NothingHere()
                }
                1 -> {
                    var pdfsList by remember {
                        mutableStateOf(
                            filesDir.listFiles { _, n ->
                                n.contains(".pdf") && n.contains("_sub")
                            }
                        )
                    }

                    if (!pdfsList.isNullOrEmpty()) {
                        LazyColumn {
                            items<File>(pdfsList) { file ->
                                val swipeState = rememberDismissState()
                                SwipeToDismiss(
                                    state = swipeState,
                                    background = {
                                        SwipeBackground(
                                            onDelete = {
                                                if (file.delete()) {
                                                    pdfsList = filesDir.listFiles { _, n ->
                                                        n.contains(".pdf") && n.contains("_sub")
                                                    }
                                                }
                                            },
                                            onCancel = { coroutineScope.launch { swipeState.reset() } }
                                        )
                                    }
                                ) {
                                    FileItemCard(name = file.name.substringAfter("_sub")) {
                                        showPdf(file)
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(90.dp))
                            }
                        }
                    } else {
                        NothingHere()
                    }
                }

                2 -> {
                    if (isNetworkAvailable) {
                        var pdfsList by remember {
                            mutableStateOf(
                                filesDir.listFiles { _, n ->
                                    n.contains(".pdf") && n.contains("_rev")
                                }
                            )
                        }

                        if (!pdfsList.isNullOrEmpty()) {
                            LazyColumn {
                                items<File>(pdfsList) { file ->
                                    val swipeState = rememberDismissState()
                                    SwipeToDismiss(
                                        state = swipeState,
                                        background = {
                                            SwipeBackground(
                                                onDelete = {
                                                    file.delete()
                                                    pdfsList = filesDir.listFiles { _, n ->
                                                        n.contains(".pdf") && n.contains("_rev")
                                                    }
                                                },
                                                onCancel = { coroutineScope.launch { swipeState.reset() } }
                                            )
                                        }
                                    ) {
                                        FileItemCard(
                                            name = file.name.substringAfter("_rev")
                                        ) {
                                            showPdf(file)
                                        }
                                    }
                                }

                                item {
                                    Spacer(modifier = Modifier.height(90.dp))
                                }
                            }
                        } else {
                            NothingHere()
                        }
                    } else NothingHere(msg = "No internet connection.. Please turn it on")
                }
            }
        }
    }
}


@Composable
fun SwipeBackground(
    onDelete: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {

        Surface(
            color = Color(0xFFDD2C00),
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    onDelete()
                }
                .padding(5.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White
            )
        }

        Surface(
            color = Color(0xFF00C853),
            modifier = Modifier
                .size(50.dp)
                .clickable {
                    onCancel()
                }
                .padding(5.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(
                modifier = Modifier.padding(10.dp),
                imageVector = Icons.Default.Cancel,
                contentDescription = "Delete",
                tint = Color.White
            )
        }
    }
}