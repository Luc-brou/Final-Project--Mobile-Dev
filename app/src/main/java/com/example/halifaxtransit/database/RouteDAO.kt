package com.example.halifaxtransit.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.halifaxtransit.models.Route

@Dao
interface RoutesDao {
    @Query("SELECT * FROM Routes") //this query selects all routes
    fun getAll(): List<Route> //then we can use this function to grab all the routes for routes screen

    @Update
    fun updateRoute(route: Route) //function updates all routes

    @Query("UPDATE Routes SET Highlights = :highlight WHERE route_id = :id") //this is for our highlighted (checked) routes
    fun setHighlight(id: String, highlight: Boolean) //we use this in map screen when user checks a route they want to see
}