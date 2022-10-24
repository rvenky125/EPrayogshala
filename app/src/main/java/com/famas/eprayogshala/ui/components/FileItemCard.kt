package com.famas.eprayogshala.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FilePresent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FileItemCard(
    modifier: Modifier = Modifier,
    name: String,
    onClick: () -> Unit
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .height(80.dp)
        .padding(5.dp),
        onClick = { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Icon(modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp), imageVector = Icons.Rounded.FilePresent, contentDescription = "", tint = MaterialTheme.colors.onSurface)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = name, style = MaterialTheme.typography.subtitle1)
        }
    }
}