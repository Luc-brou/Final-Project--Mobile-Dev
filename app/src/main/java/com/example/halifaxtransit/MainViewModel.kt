package com.example.halifaxtransit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halifaxtransit.database.RoutesDao
import com.google.transit.realtime.GtfsRealtime.FeedMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URL

// Simple data class for bus positions
data class GtfsBusPosition(val id: String, val lat: Float, val lon: Float)

class MainViewModel(private val routesDao: RoutesDao? = null) : ViewModel() {

    private val _gtfs = MutableStateFlow<List<GtfsBusPosition>>(emptyList())
    val gtfs: StateFlow<List<GtfsBusPosition>> = _gtfs

    fun loadGtfsBusPositions() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/VehiclePositions.pb")
                val feed = FeedMessage.parseFrom(url.openStream())

                val positions = feed.entityList.mapNotNull { entity ->
                    entity.vehicle?.let { veh ->
                        GtfsBusPosition(
                            id = veh.vehicle.id,
                            lat = veh.position.latitude,
                            lon = veh.position.longitude
                        )
                    }
                }

                _gtfs.value = positions
            } catch (e: Exception) {
                Log.e("GTFS", "Failed to load feed", e)
            }
        }
    }
}