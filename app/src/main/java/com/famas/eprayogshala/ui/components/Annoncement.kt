package com.famas.eprayogshala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
@Composable
fun Announcement(
    modifier: Modifier = Modifier,
    message: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(color = Color(0xFFE8F5E9)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(8.dp),
            imageVector = Icons.Default.Notifications,
            contentDescription = "Announcement"
        )
        Text(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            text = message,
            color = Color(0xFF1B5E20),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Start
        )
    }
}