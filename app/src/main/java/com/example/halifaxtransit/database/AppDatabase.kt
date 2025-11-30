package com.example.halifaxtransit.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.halifaxtransit.models.Route

@Database(entities = [Route::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routesDao(): RoutesDao //abstract function

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder( //this is our database
                    context.applicationContext,
                    AppDatabase::class.java,
                    "HalifaxTransit.db"
                )
                    .createFromAsset("RoutesData.db") //this line seeds the database with the
                    .build()                                          //file located in src/main/assets
                INSTANCE = instance
                instance
            }
        }
    }
}