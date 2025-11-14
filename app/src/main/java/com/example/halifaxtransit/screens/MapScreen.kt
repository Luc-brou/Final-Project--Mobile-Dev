package com.example.halifaxtransit.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.google.transit.realtime.GtfsRealtime
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
    gtfsFeed: GtfsRealtime.FeedMessage?,
    modifier: Modifier = Modifier
) {
    // FeedMessage.entityList is the generated property for repeated FeedEntity
    val busPositions = gtfsFeed?.entityList

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(-63.5826, 44.6510))
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        mapViewportState = mapViewportState,
        modifier = modifier.fillMaxSize()
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
        if (!busPositions.isNullOrEmpty()) {
            for (feedEntity in busPositions) {
                // defensive check: some entities may not have vehicle info
                val vehicle = feedEntity.vehicle ?: continue
                val pos = vehicle.position ?: continue
                val routeId = vehicle.trip?.routeId ?: "?"
                val lon = pos.longitude.toDouble()
                val lat = pos.latitude.toDouble()

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
}