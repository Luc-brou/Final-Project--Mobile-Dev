package com.example.halifaxtransit.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.halifaxtransit.models.Route

@Dao
interface RoutesDao {
    @Query("SELECT * FROM Routes")
    fun getAll(): List<Route>

    @Update
    fun updateRoute(route: Route)

    //not yet implememnted

    @Query("UPDATE Routes SET Highlights = :highlight WHERE route_id = :id")
    fun setHighlight(id: String, highlight: Boolean)

}