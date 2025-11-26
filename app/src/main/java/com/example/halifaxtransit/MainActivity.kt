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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.halifaxtransit.ui.theme.HalifaxTransitTheme
import com.example.halifaxtransit.screens.BusMapScreen
import com.example.halifaxtransit.screens.RoutesScreen
import com.example.halifaxtransit.ui.theme.FrostedMint
import com.example.halifaxtransit.ui.theme.LightGreen
import com.example.halifaxtransit.ui.theme.MintLeaf
import com.example.halifaxtransit.ui.theme.RegalNavy
import com.example.halifaxtransit.ui.theme.Verdigris
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    // Request permission to get location. Register for the 'activity result'. This handles the permission request and its result.
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
    { isGranted ->
        if (isGranted) {
            Log.i("TESTING", "New permission granted by user, proceed...")
        } else {
            Log.i("TESTING", "Permission DENIED by user! Display toast...")

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

        // load bus positions from GTFS
        mainViewModel.loadGtfsBusPositions()

        setContent {
            val context = LocalContext.current

            // Check if permission granted
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TESTING", "Permission previously granted, proceed...")
                } else {
                    Log.i("TESTING", "Permission not yet granted, launching permission request...")
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            HalifaxTransitTheme {
                // collect the feed and pass into the BusMapScreen
                val gtfsFeed by mainViewModel.gtfs.collectAsState()

                // Navigation setup (WeatherApp-style Bottom Navigation)
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { TopBarContent() },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MintLeaf // background of the bottom bar
                        ) {
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
                                    indicatorColor = LightGreen // highlight behind selected item
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
                            // pass the collected feed into the BusMapScreen composable
                            BusMapScreen(gtfsFeed = gtfsFeed)
                        }
                        composable("routes") {
                            RoutesScreen()
                        }
                    }
                }
            }
        }
    } // End onCreate

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