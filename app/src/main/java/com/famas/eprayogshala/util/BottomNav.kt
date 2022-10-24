package com.famas.eprayogshala.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FileCopy
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNav(val route: String, val icon: (Boolean) -> ImageVector, val label: String) {

    object Experiments: BottomNav(
        Constants.ROUTE_EXPERIMENTS,
        { if (it) Icons.Filled.Home else Icons.Outlined.Home },
        "Experiments"
    )

    object Reports: BottomNav(
        Constants.ROUTE_REPORTS,
        { if (it) Icons.Filled.FileCopy else Icons.Outlined.FileCopy },
        "Reports"
    )
}
