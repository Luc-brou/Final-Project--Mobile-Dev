package com.example.halifaxtransit.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Routes") //this is the routes table we seed
data class Route(
    @PrimaryKey //primary key is route id
    @ColumnInfo(name = "route_id")
    val routeId: String,

    @ColumnInfo(name = "route_short_name") //short name, usually same as id
    val routeShortName: String,

    @ColumnInfo(name = "route_long_name") //this is the long name ex "Sackville"
    val routeLongName: String,

    @ColumnInfo(name = "Highlights") //this is boolean for if the user wants the route
    val highlights: Boolean          //highlighted or not. 1=yes 0=no
)