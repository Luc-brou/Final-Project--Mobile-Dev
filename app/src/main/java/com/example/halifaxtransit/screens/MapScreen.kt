package com.example.halifaxtransit.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.halifaxtransit.GtfsBusPosition
import com.example.halifaxtransit.models.Route
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.maps.viewannotation.geometry

@Composable
fun BusMapScreen(
    gtfsFeed: List<GtfsBusPosition>,
    routes: List<Route>,
    selectedRoutes: Set<String>,
    modifier: Modifier = Modifier
) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(-63.5826, 44.6510)) // Halifax downtown
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        mapViewportState = mapViewportState,
        modifier = modifier.fillMaxSize()
    ) {
        // Enable user location puck
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
        gtfsFeed.forEach { bus ->
            val lon = bus.lon
            val lat = bus.lat
            val vehicleId = bus.id

            // Check if this bus belongs to a selected route
            val isHighlighted = selectedRoutes.contains(vehicleId)

            ViewAnnotation(
                options = viewAnnotationOptions {
                    geometry(Point.fromLngLat(lon, lat))
                }
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Image(
                        painter = painterResource(id = com.example.halifaxtransit.R.drawable.bus),
                        contentDescription = "Bus $vehicleId",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Text(
                        text = vehicleId,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isHighlighted) Color.Blue else Color.Black,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}