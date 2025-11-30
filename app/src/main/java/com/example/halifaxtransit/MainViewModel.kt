package com.example.halifaxtransit

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halifaxtransit.database.AppDatabase
import com.example.halifaxtransit.database.RoutesDao
import com.example.halifaxtransit.models.Route
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel : ViewModel() {
    private val _gtfs = MutableStateFlow<GtfsRealtime.FeedMessage?>(null)
    val gtfs = _gtfs.asStateFlow()

    private val _routes = MutableStateFlow<List<Route>>(emptyList())
    val routes = _routes.asStateFlow()

    // ✅ Add a property to hold the DAO instance
    private var routesDao: RoutesDao? = null

    fun loadGtfsBusPositions() {
        viewModelScope.launch {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
                val feed = withContext(Dispatchers.IO) {
                    GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                }
                _gtfs.value = feed
            } catch (e: Exception) {
                Log.e("TESTING", e.toString())
            }
        }
    }

    fun loadRoutes(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(context)
                routesDao = db.routesDao()                  // ✅ store instance
                val dbRoutes = routesDao!!.getAll()
                _routes.value = dbRoutes
                Log.d("TESTING", "Loaded ${dbRoutes.size} routes from DB")
            } catch (e: Exception) {
                Log.e("TESTING", "Error loading routes: $e")
            }
        }
    }

    fun toggleHighlight(routeId: String, highlight: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = routesDao ?: return@launch
            dao.setHighlight(routeId, highlight)
            _routes.value = dao.getAll()
        }
    }
}