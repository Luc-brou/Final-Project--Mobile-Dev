// MainActivity.kt
package com.example.halifaxtransit

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.*
import com.example.halifaxtransit.database.AppDatabase
import com.example.halifaxtransit.database.RoutesDao
import com.example.halifaxtransit.screens.BusMapScreen
import com.example.halifaxtransit.screens.RoutesScreen
import com.example.halifaxtransit.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var routesDao: RoutesDao
    private lateinit var viewModel: MainViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("TESTING", "Location permission granted")
        } else {
            Log.i("TESTING", "Location permission denied")
            Toast.makeText(
                this,
                "Please enable location permission in Settings to use this feature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        routesDao = db.routesDao()

        CoroutineScope(Dispatchers.IO).launch {
            val allRoutes = routesDao.getAll()
            Log.d("DB_TEST", "Loaded ${allRoutes.size} routes from DB")
        }

        // Instantiate ViewModel with a factory AFTER routesDao is available
        val factory = MainViewModelFactory(routesDao)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)
        viewModel.loadGtfsBusPositions()

        setContent {
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TESTING", "Permission already granted")
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            HalifaxTransitTheme {
                val gtfsFeed by viewModel.gtfs.collectAsState()

                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBarContent() },
                    bottomBar = {
                        NavigationBar(containerColor = MintLeaf) {
                            NavigationBarItem(
                                selected = currentRoute == "map",
                                onClick = {
                                    navController.navigate("map") {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId)
                                    }
                                },
                                icon = { Icon(Icons.Default.Place, contentDescription = "Map") },
                                label = { Text("Map") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RegalNavy,
                                    selectedTextColor = RegalNavy,
                                    unselectedIconColor = Verdigris,
                                    unselectedTextColor = Verdigris,
                                    indicatorColor = LightGreen
                                )
                            )
                            NavigationBarItem(
                                selected = currentRoute == "routes",
                                onClick = {
                                    navController.navigate("routes") {
                                        launchSingleTop = true
                                        popUpTo(navController.graph.startDestinationId)
                                    }
                                },
                                icon = { Icon(Icons.Default.Info, contentDescription = "Routes") },
                                label = { Text("Routes") },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = RegalNavy,
                                    selectedTextColor = RegalNavy,
                                    unselectedIconColor = Verdigris,
                                    unselectedTextColor = Verdigris,
                                    indicatorColor = LightGreen
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "map",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("map") {
                            BusMapScreen(gtfsFeed = gtfsFeed)
                        }
                        composable("routes") {
                            RoutesScreen()
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopBarContent() {
        TopAppBar(
            title = { Text("Halifax Transit") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = RegalNavy,
                titleContentColor = FrostedMint
            )
        )
    }
}