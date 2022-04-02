package com.ifndev.spectacles

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifndev.spectacles.model.FavoritesStore
import com.skydoves.landscapist.glide.GlideImage
import fr.patrickconti.specto.model.Painting
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PaintingDetails(navController: NavController, painting: Painting) {
    val backDropScaffoldState =
        rememberBackdropScaffoldState(initialValue = BackdropValue.Concealed)
    val favoritesStore = FavoritesStore(LocalContext.current)
    val scope = rememberCoroutineScope()

    val favorites = favoritesStore.favorites.collectAsState(initial = emptyList())

    BackdropScaffold(
        scaffoldState = backDropScaffoldState,
        backLayerBackgroundColor = MaterialTheme.colorScheme.background,
        frontLayerBackgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        peekHeight = 300.dp,
        headerHeight = 100.dp,
        persistentAppBar = true,
        appBar = {

            Row {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Outlined.ArrowBack, "Back")
                }
            }
        },
        backLayerContent = {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    GlideImage(
                        imageModel = "file:///android_asset/image_databases/" + painting.photo,
                        contentDescription = painting.title,
                        contentScale = ContentScale.FillHeight,
                        modifier = if (backDropScaffoldState.isConcealed) Modifier.height(
                            backDropScaffoldState.offset.value.dp
                        ) else Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        frontLayerContent = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(vertical = 20.dp),
                    verticalArrangement = Arrangement.Top
                )
                {
                    Text(painting.title, style = MaterialTheme.typography.displayMedium)
                    Text(
                        painting.artist.name + " - " + painting.year.toString(),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Row(modifier = Modifier.padding(vertical = 30.dp)) {
                        Chip(
                            active = favorites.value?.contains(painting.id) ?: false,
                            iconPassive = Icons.Rounded.FavoriteBorder,
                            iconActive = Icons.Rounded.Favorite,
                            text = "Favorite"
                        ) {
                            scope.launch {
                                if (favorites.value?.contains(painting.id) == true) {
                                    favoritesStore.removeFavorite(painting.id)
                                } else {
                                    favoritesStore.addFavorite(painting.id)
                                }
                            }
                        }
                    }
                    Text(
                        painting.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 30.dp, horizontal = 10.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        })
}
