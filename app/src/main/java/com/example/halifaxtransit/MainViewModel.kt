package com.example.halifaxtransit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halifaxtransit.database.RoutesDao
import com.example.halifaxtransit.models.Route
import com.google.transit.realtime.GtfsRealtime.FeedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URL

// Simple data class for bus positions
data class GtfsBusPosition(
    val id: String,
    val lat: Double,
    val lon: Double
)

class MainViewModel(private val routesDao: RoutesDao? = null) : ViewModel() {

    private val _gtfs = MutableStateFlow<List<GtfsBusPosition>>(emptyList())
    val gtfs: StateFlow<List<GtfsBusPosition>> = _gtfs

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes: StateFlow<List<Route>> = _routes

    private val _selectedRoutes = MutableStateFlow<Set<String>>(emptySet())
    val selectedRoutes: StateFlow<Set<String>> = _selectedRoutes

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch(Dispatchers.IO) {
            val dbRoutes = routesDao?.getAll() ?: emptyList()
            _routes.value = dbRoutes
        }
    }

    fun toggleRouteSelection(routeId: String) {
        _selectedRoutes.value = _selectedRoutes.value.toMutableSet().apply {
            if (contains(routeId)) remove(routeId) else add(routeId)
        }
    }

    fun loadGtfsBusPositions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/VehiclePositions.pb")
                val feed = FeedMessage.parseFrom(url.openStream())

                // TESTING ONLY: log how many entities are in the feed (remove when not needed)
                Log.d("GTFS", "Feed entities: ${feed.entityCount}")

                val positions = feed.entityList.mapNotNull { entity ->
                    entity.vehicle?.let { veh ->
                        GtfsBusPosition(
                            id = veh.vehicle.id,
                            lat = veh.position.latitude.toDouble(),
                            lon = veh.position.longitude.toDouble()
                        )
                    }
                }

                _gtfs.value = positions
                Log.d("GTFS", "Loaded ${positions.size} bus positions")
            } catch (e: Exception) {
                Log.e("GTFS", "Failed to load feed", e)
            }
        }
    }
}