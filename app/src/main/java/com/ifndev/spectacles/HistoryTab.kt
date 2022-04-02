package com.ifndev.spectacles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.ifndev.spectacles.viewModel.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryTab(navController: NavController, viewModel: HistoryViewModel) {
    val historyState = viewModel.historyState

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        for (painting in historyState) {
            PaintingCard(navController, painting)
        }

        if (historyState.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No history yet")
            }
        }
    }
}