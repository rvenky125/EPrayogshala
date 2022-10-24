package com.famas.eprayogshala.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.famas.eprayogshala.util.experiments.Experiment
import com.famas.eprayogshala.util.loadPicture

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExperimentItemCard(experiment: Experiment, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(vertical = 10.dp),
        onClick = { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            loadPicture(imageRes = experiment.image).value?.let {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = it.asImageBitmap(),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
            }

            Card(modifier = Modifier.align(Alignment.BottomCenter)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        text = experiment.name,
                        style = MaterialTheme.typography.subtitle1
                    )
                }
            }
        }
    }

}