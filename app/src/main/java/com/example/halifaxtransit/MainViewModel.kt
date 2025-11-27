package com.example.halifaxtransit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.halifaxtransit.database.RoutesDao
import com.google.transit.realtime.GtfsRealtime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class MainViewModel(private val routesDao: RoutesDao) : ViewModel() {

    private val _gtfs = MutableStateFlow<GtfsRealtime.FeedMessage?>(null)
    val gtfs = _gtfs.asStateFlow()

    // Load Halifax transit bus positions
    fun loadGtfsBusPositions() {
        viewModelScope.launch {
            try {
                val url = URL("https://gtfs.halifax.ca/realtime/Vehicle/VehiclePositions.pb")
                val feed = withContext(Dispatchers.IO) {
                    GtfsRealtime.FeedMessage.parseFrom(url.openStream())
                }
                _gtfs.value = feed
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Example: insert a user
    fun insertUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            routesDao.insertAll(user)
        }
    }

    // Example: get all users
    fun loadUsers() {
        viewModelScope.launch(Dispatchers.IO) {
            val users = routesDao.getAll()
            // Do something with users (emit via StateFlow, log, etc.)
        }
    }
}