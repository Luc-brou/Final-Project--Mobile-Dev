package com.example.halifaxtransit.database

import androidx.room.Dao
import androidx.room.Query
import com.example.halifaxtransit.models.Route

@Dao
interface RoutesDao {
    @Query("SELECT * FROM routes")
    fun getAll(): List<Route>

    @Query("SELECT * FROM routes WHERE route_id = :id")
    fun loadAllByIds(id: String): Route?

    @Query("SELECT * FROM routes WHERE route_short_name= :shortName")
    fun getByShortName(shortName: String): Route?

    @Query("SELECT * FROM routes WHERE Highlights = 1")
    fun getHighlighted(): List<Route>

}