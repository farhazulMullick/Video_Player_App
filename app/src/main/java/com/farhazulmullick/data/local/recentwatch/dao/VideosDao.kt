package com.farhazulmullick.data.local.recentwatch.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.farhazulmullick.data.local.recentwatch.entity.VideoDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {
    @Query("SELECT * FROM video_details")
    suspend fun getVideos(): Flow<List<VideoDetails>>

    @Query("SELECT 1 FROM video_details WHERE video_id = :id")
    suspend fun getVideoById(id: String)

}