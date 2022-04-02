package com.ifndev.spectacles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun Chip(
    active: Boolean,
    iconPassive: ImageVector,
    iconActive: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .clickable { onClick() },
        tonalElevation = 8.dp,
        shape = RoundedCornerShape(11.dp),
        color = when {
            active -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.background
        },
        border = BorderStroke(
            width = 1.dp,
            color = when {
                active -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                when {
                    active -> iconActive
                    else -> iconPassive
                },
                text,
                modifier = Modifier.padding(end = 5.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(text)
        }
    }
}