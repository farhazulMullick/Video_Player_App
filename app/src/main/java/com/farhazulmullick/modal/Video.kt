package com.farhazulmullick.modal

import android.net.Uri

data class Video(
    val videoId: String,
    val videoUri: Uri,
    val videoName: String,
    val videoDuration: Long,
    val videoSize: Long,
    val videoDateAdded: String,
    val videoFolderName: String,
    val videoPath: String

)
