package com.farhazulmullick.feature.allvideos.modal

import android.net.Uri
import com.farhazulmullick.utils.EMPTY

data class Video(
    val videoId: String = String.EMPTY,
    val videoUri: Uri? = null,
    val videoTitle: String = String.EMPTY,
    val videoDuration: Long = 0L,
    val videoSize: Long= 0L,
    val videoDateAdded: String = String.EMPTY,
    val videoFolderName: String = String.EMPTY,
    val videoPath: String = String.EMPTY

)
