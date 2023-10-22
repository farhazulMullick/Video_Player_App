package com.farhazulmullick.data.local.recentwatch.datasource

import android.net.Uri
import com.farhazulmullick.data.local.recentwatch.dao.VideosDao
import com.farhazulmullick.data.local.recentwatch.entity.VideoDetails
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.utils.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RecentWatchDataSourceImpl @Inject constructor(
    private val videosDao: VideosDao
) : RecentWatchDataSource {
    override suspend fun getVideos(limit: Int): Flow<List<Video>>  {
        return videosDao.getVideos(limit = limit).map {
            it.map {
                Video(
                    videoId = it.videoId,
                    videoUri = Uri.parse(it.thumbnail.value),
                    videoTitle = it.videoTitle.value,
                    videoDuration = it.duration ?: 0L,
                    videoSize = it.videoSize ?: 0L,
                    lastWatchTime = it.lastWatchTime ?: 0L
                )
            }
        }
    }


    override suspend fun getVideoById(id: String): Flow<Video?> {
        return videosDao.getVideoById(id).map {
            it?.let {
                Video(
                    videoId = it.videoId.value,
                    videoTitle = it.videoTitle.value,
                    videoDuration = it.duration ?: 0L,
                    videoPath = it.videoPath.value,
                    lastWatchTime = it.lastWatchTime ?: 0L,
                    videoUri = Uri.parse(it.thumbnail),
                    videoSize = it.videoSize ?: 0
                )
            }
        }
    }

    override suspend fun saveVideo(video: Video) {
        videosDao.saveVideoStats(video.mapper {
            VideoDetails(
                videoId = it.videoId,
                videoTitle = it.videoTitle,
                thumbnail = it.videoUri.toString(),
                duration = it.videoDuration,
                lastWatchTime = it.lastWatchTime,
                videoPath = it.videoPath,
                videoSize = it.videoSize
            )
        })
    }

    override suspend fun updateVideoItem(id: String, lastWatchTime: Long) {
        videosDao.updateLastWatchPoint(
            id = id,
            lastWatchTime = lastWatchTime
        )
    }
}

private fun <T, R> T.mapper(action: (T) -> R): R {
    return action(this)
}