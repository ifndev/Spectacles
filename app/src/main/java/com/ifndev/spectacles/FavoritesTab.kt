package com.ifndev.spectacles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.ifndev.spectacles.model.FavoritesStore
import com.ifndev.spectacles.viewModel.ActiveDatabaseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesTab(navController: NavController, activeDatabaseViewModel: ActiveDatabaseViewModel) {
    val favoritesStore = FavoritesStore(LocalContext.current)
    val scope = rememberCoroutineScope()

    val favorites = favoritesStore.favorites.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (favorites.value.isNullOrEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No favorites yet")
            }
        } else {
            for (painting in favorites.value!!) {
                PaintingCard(navController, activeDatabaseViewModel.getPainting(painting))
            }
        }
    }
}