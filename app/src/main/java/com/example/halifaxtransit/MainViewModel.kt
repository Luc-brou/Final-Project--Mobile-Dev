package com.example.halifaxtransit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel: ViewModel() {
    private val _gtfs = MutableStateFlow<GtfsRealtime.FeedMessage?>(null)
    val gtfs = _gtfs.asStateFlow()

    // Get the Halifax transit bus positions
    fun loadGtfsBusPositions() {
        viewModelScope.launch {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")

                // Run code (which is blocking) on a background thread optimized for I/O, and suspend the coroutine until it's done.
                val feed = withContext(Dispatchers.IO) {
                    GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                }

                Log.d("TESTING", feed.toString())

                _gtfs.value = feed
            } catch (e: Exception) {
                Log.e("TESTING", e.toString() )
            }
        }
    }
}