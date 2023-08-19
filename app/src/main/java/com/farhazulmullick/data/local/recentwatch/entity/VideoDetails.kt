package com.farhazulmullick.data.local.recentwatch.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.farhazulmullick.utils.EMPTY

@Entity(tableName = "video_details")
class VideoDetails {

    @PrimaryKey(autoGenerate = true)
    val videoId: String = String.EMPTY

    @ColumnInfo(name = "video_title")
    val videoTitle: String = String.EMPTY

    @ColumnInfo("thumbnail")
    val thumbnail: String = String.EMPTY

    @ColumnInfo("video_duration")
    val duration: Long = 0L

    @ColumnInfo("last_watch_time")
    val lastWatchTime: Long = 0L

    @ColumnInfo("video_path")
    val videoPath: String = String.EMPTY
}