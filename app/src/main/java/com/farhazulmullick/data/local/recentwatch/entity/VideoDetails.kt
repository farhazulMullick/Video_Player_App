package com.farhazulmullick.data.local.recentwatch.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.farhazulmullick.utils.EMPTY

/**
 * Fields are var because on val it throws some compilation error
 * 'Cannot find setter for field' since setter is private.
 */

@Entity(tableName = "video_details")
class VideoDetails {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "video_id")
    var videoId: Int = 0

    @ColumnInfo(name = "video_title")
    var videoTitle: String = String.EMPTY

    @ColumnInfo("thumbnail")
    var thumbnail: String = String.EMPTY

    @ColumnInfo("video_duration")
    var duration: Long = 0L

    @ColumnInfo("last_watch_time")
    var lastWatchTime: Long = 0L

    @ColumnInfo("video_path")
    var videoPath: String = String.EMPTY
}