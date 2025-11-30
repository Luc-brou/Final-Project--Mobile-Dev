package com.example.halifaxtransit.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.halifaxtransit.MainViewModel
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
fun BusMapScreen( //this is our function to call in main
    viewModel: MainViewModel, //we pass in mainviewmodel
    gtfsFeed: GtfsRealtime.FeedMessage?, //and gtfsFeed as parameters
    modifier: Modifier = Modifier
) {
    val busPositions = gtfsFeed?.entityList
    val routes = viewModel.routes.collectAsState().value

    val mapViewportState = rememberMapViewportState { //this is for remembering how
        setCameraOptions {                            //much the map is zoomed in/out/moved
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
        MapEffect(Unit) { mapView ->
            mapView.location.updateSettings {
                locationPuck = createDefault2DPuck(withBearing = true)
                enabled = true
                puckBearing = PuckBearing.COURSE
                puckBearingEnabled = true
            }
            mapViewportState.transitionToFollowPuckState()
        }

        if (!busPositions.isNullOrEmpty()) { // displays bus locations
            for (feedEntity in busPositions) {
                val vehicle = feedEntity.vehicle ?: continue
                val pos = vehicle.position ?: continue
                val routeId = vehicle.trip?.routeId ?: "?"
                val lon = pos.longitude.toDouble() //converts to double
                val lat = pos.latitude.toDouble() //converts to double

                val route = routes.find { it.routeId == routeId }
                val isHighlighted = route?.highlights == true //variable for if is selected

                val busDrawableRes = if (isHighlighted) { //aka if highlighted bool = 1
                    com.example.halifaxtransit.R.drawable.busblue //bool = 1
                } else {
                    com.example.halifaxtransit.R.drawable.bus //bool = 0
                }

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
                            painter = painterResource(id = busDrawableRes), //dynamically chooses which bus image to render
                            contentDescription = "Route $routeId",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Text( //overlays the bus id on the bus image (ex: 8, 91)
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