package com.example.halifaxtransit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.halifaxtransit.database.RoutesDao

class MainViewModelFactory(
    private val routesDao: RoutesDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(routesDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}