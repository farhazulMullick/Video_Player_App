package com.farhazulmullick.data.local.recentwatch.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.farhazulmullick.data.local.recentwatch.entity.VideoDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface VideosDao {
    @Query("SELECT * FROM video_details ORDER BY created_at DESC LIMIT :limit")
    fun getVideos(limit: Int): Flow<List<VideoDetails>>

    @Query("SELECT * FROM video_details WHERE video_id = :id")
    fun getVideoById(id: String): Flow<VideoDetails?>

    @Insert
    suspend fun saveVideoStats(videoDetails: VideoDetails)

    @Query("UPDATE video_details SET last_watch_time = :lastWatchTime, created_at = :createdAt WHERE video_id = :id")
    suspend fun updateLastWatchPoint(id: String, lastWatchTime: Long, createdAt: Long): Int

    @Query("DELETE FROM video_details WHERE video_id = :id")
    suspend fun deleteVideoStatsById(id: String)
}