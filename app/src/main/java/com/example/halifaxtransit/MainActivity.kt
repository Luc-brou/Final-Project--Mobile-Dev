package com.example.halifaxtransit

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.halifaxtransit.ui.theme.HalifaxTransitTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // load bus positions from GTFS
        mainViewModel.loadGtfsBusPositions()

        setContent {
            val context = LocalContext.current

            // Check if permission granted
            LaunchedEffect(Unit) {
                if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TESTING", "Permission previously granted, proceed...")
                } else {
                    Log.i("TESTING", "Permission not yet granted, launching permission request...")
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

            HalifaxTransitTheme {
                DisplayMap()
            }
        }
    } // End onCreate

    @Composable
    fun DisplayMap() {

        // Get entities (bus positions) from ViewModel
        val gtfsFeed by mainViewModel.gtfs.collectAsState()
        val busPositions = gtfsFeed?.entityList

        val mapViewportState = rememberMapViewportState {
            // set default camera zoom on Halifax centre
            setCameraOptions {
                zoom(12.0)
                center(Point.fromLngLat(-63.5826, 44.6510))
                pitch(0.0)
                bearing(0.0)
            }
        }

        MapboxMap(
            mapViewportState = mapViewportState,
        ) {
            // Map effect will take effect once permission is granted to display user's location.
            MapEffect(Unit) { mapView ->
                mapView.location.updateSettings {
                    locationPuck = createDefault2DPuck(withBearing = true)
                    enabled = true
                    puckBearing = PuckBearing.COURSE
                    puckBearingEnabled = true
                }
                mapViewportState.transitionToFollowPuckState()
            }

            // Display bus locations
            if (busPositions != null) {
                for(bus in busPositions){
                    val routeId = bus.vehicle.trip.routeId

                    // Insert a ViewAnnotation at specific geo coordinate.
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            // View annotation is placed at the specific geo coordinate
                            geometry(Point.fromLngLat(bus.vehicle.position.longitude.toDouble(),
                                bus.vehicle.position.latitude.toDouble()))
                        }
                    ) {
                        // ViewAnnotation content
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.bus),
                                contentDescription = "Route $routeId",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            Text(
                                text = routeId,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    } // End DisplayMap

} // End MainActivity

