package com.farhazulmullick.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.farhazulmullick.data.local.recentwatch.entity.VideoDetails

const val DATABASE_NAME = "app_database"

@Database(
    entities = [VideoDetails::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    companion object{
        var INSTANCE: AppDatabase? = null
    }
}

val lock = object {}
fun Context.buildVideoPlayerDatabase() = AppDatabase.INSTANCE ?: synchronized(lock) {
        val instance = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            DATABASE_NAME
        ).build()

        AppDatabase.INSTANCE = instance
        instance
    }