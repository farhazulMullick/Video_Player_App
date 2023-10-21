package com.farhazulmullick.data.local.recentwatch.datasource

import com.farhazulmullick.data.local.recentwatch.entity.VideoDetails
import com.farhazulmullick.feature.allvideos.modal.Video
import kotlinx.coroutines.flow.Flow

interface RecentWatchDataSource {
    suspend fun getVideos(limit: Int = 10): Flow<List<Video>>
    suspend fun getVideoById(id: String): Flow<Video>
    suspend fun saveVideo(video: Video)
    suspend fun updateVideoItem(id: String, lastWatchTime: Long)
}