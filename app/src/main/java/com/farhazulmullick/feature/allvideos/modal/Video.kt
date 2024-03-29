package com.farhazulmullick.feature.allvideos.modal

import android.net.Uri

data class Video(
    val videoId: String,
    val videoUri: Uri,
    val videoTitle: String,
    val videoDuration: Long,
    val videoSize: Long,
    val videoDateAdded: String,
    val videoFolderName: String,
    val videoPath: String

)
