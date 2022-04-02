package com.ifndev.spectacles

import android.app.Activity.RESULT_OK
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraEnhance
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.ar.core.ArCoreApk
import com.google.ar.core.examples.java.augmentedimage.AugmentedImageActivity
import com.ifndev.spectacles.viewModel.ActiveDatabaseViewModel
import com.ifndev.spectacles.viewModel.HistoryViewModel

class MenuComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Root()
        }
    }
}

class Root @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AbstractComposeView(context, attrs, defStyle) {

    @Composable
    override fun Content() {
        Root()
    }

}

@Composable
fun Root(
    activeDatabaseViewModel: ActiveDatabaseViewModel = ActiveDatabaseViewModel(),
    historyViewModel: HistoryViewModel = HistoryViewModel()
) {
    var lightColors = MaterialTheme.colorScheme

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        lightColors = dynamicLightColorScheme(context)
    }


    val navController = rememberNavController()

    MaterialTheme(colorScheme = lightColors) {
        HomeScreen(
            navController = navController,
            historyViewModel = historyViewModel,
            activeDatabaseViewModel = activeDatabaseViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel,
    activeDatabaseViewModel: ActiveDatabaseViewModel
) {
    val context = LocalContext.current
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()

    val drawScaffolding = listOf(
        "favorites",
        "history",
        "collections"
    )

    activeDatabaseViewModel.loadDatabaseFromFile("image_databases/databases.json", 0, context)
    Scaffold(
        bottomBar = {
            if (drawScaffolding.contains(currentNavBackStackEntry?.destination?.route)) NavBar(
                navController
            )
        },
        floatingActionButton = {
            if (drawScaffolding.contains(currentNavBackStackEntry?.destination?.route)) ScanFAB(
                navController,
                historyViewModel,
                activeDatabaseViewModel
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController as NavHostController,
                startDestination = "favorites"
            ) {
                composable("favorites") {
                    FavoritesTab(
                        navController = navController,
                        activeDatabaseViewModel
                    )
                }
                composable("history") {
                    HistoryTab(
                        navController = navController,
                        historyViewModel
                    )
                }
                composable("collections") { CollectionsTab(navController = navController) }
                composable("painting/{painting}") { backStackEntry ->
                    PaintingDetails(
                        navController = navController,
                        painting = activeDatabaseViewModel.getPainting(
                            backStackEntry.arguments?.getString(
                                "painting"
                            ) ?: "missingno"
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun CollectionsTab(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Collections")
    }
}

@Composable
fun ScanFAB(
    navController: NavController,
    historyViewModel: HistoryViewModel,
    activeDatabaseViewModel: ActiveDatabaseViewModel
) {
    val arcoreAvailableState = remember { mutableStateOf(false) }
    val context = LocalContext.current

    var launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { activityResult ->
            if (activityResult.resultCode == RESULT_OK) {
                val paintingid: String = activityResult.data?.getStringExtra("paintingid") ?: ""
                historyViewModel.addPainting(activeDatabaseViewModel.getPainting(paintingid))
                navController.navigate("painting/" + activeDatabaseViewModel.getPainting(paintingid).id)
            }
        })

    checkARavailability(LocalContext.current) { available ->
        arcoreAvailableState.value = available
    }
    if (arcoreAvailableState.value) {
        ExtendedFloatingActionButton(
            text = { Text(text = "Scanner") },
            onClick = {
                val intent = Intent(context, AugmentedImageActivity::class.java)
                intent.putExtra("activedbvm", activeDatabaseViewModel.getDatabaseAsJson())
                intent.putExtra(
                    "activedbfile",
                    activeDatabaseViewModel.activeDatabaseFileName.value
                )
                launcher.launch(intent)
            },
            icon = { Icon(Icons.Rounded.CameraEnhance, "Scanner") },
        )
    }
}

@Composable
fun NavBar(navController: NavController) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    Log.v(TAG, "NavBar: ${currentNavBackStackEntry?.destination?.route}")

    NavigationBar {
        NavigationBarItem(
            selected = currentNavBackStackEntry?.destination?.route == "favorites",
            onClick = { navController.navigate("favorites") },
            icon = { Icon(Icons.Rounded.Favorite, "Favorites") },
            label = { Text("Favorites") }
        )

        NavigationBarItem(
            selected = currentNavBackStackEntry?.destination?.route == "history",
            onClick = { navController.navigate("history") },
            icon = { Icon(Icons.Rounded.History, "History") },
            label = { Text("History") }
        )

        NavigationBarItem(
            selected = currentNavBackStackEntry?.destination?.route == "collections",
            onClick = { navController.navigate("collections") },
            icon = { Icon(Icons.Rounded.List, "Collections") },
            label = { Text("Collections") }
        )
    }
}

fun checkARavailability(context: Context, callback: (Boolean) -> Unit) {
    val availability = ArCoreApk.getInstance().checkAvailability(context)
    if (availability.isTransient) {
        // Continue to query availability at 5Hz while compatibility is checked in the background.
        Handler(Looper.getMainLooper()).postDelayed({
            checkARavailability(context, callback)
        }, 200)
    }

    callback(availability.isSupported)
}