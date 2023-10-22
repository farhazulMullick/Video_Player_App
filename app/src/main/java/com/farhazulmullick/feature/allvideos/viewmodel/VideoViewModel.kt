package com.farhazulmullick.feature.allvideos.viewmodel

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.BUCKET_DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.BUCKET_ID
import android.provider.MediaStore.MediaColumns.DATA
import android.provider.MediaStore.MediaColumns.DATE_ADDED
import android.provider.MediaStore.MediaColumns.DISPLAY_NAME
import android.provider.MediaStore.MediaColumns.DURATION
import android.provider.MediaStore.MediaColumns.SIZE
import android.provider.MediaStore.MediaColumns._ID
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farhazulmullick.data.local.recentwatch.datasource.RecentWatchDataSource
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.feature.folders.modals.Folder
import com.farhazulmullick.utils.VIDEO_MODEL
import com.farhazulmullick.utils.getLongValue
import com.farhazulmullick.utils.getStringValue
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import javax.inject.Inject


@HiltViewModel
class VideoViewModel @Inject constructor(
    val recentWatchDataSource: RecentWatchDataSource,
    val savedStateHandle: SavedStateHandle
) : ViewModel() {
    companion object {
        const val TAG = "VideoViewModel"
    }

    val currentVideo by lazy {
        savedStateHandle.get<Video>(VIDEO_MODEL) ?: throw NullPointerException("video model can't be null")
    }

    private val contentUri: Uri? get() {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    }

    val videoList = MutableLiveData<List<Video>>()
    val folderList = MutableLiveData<List<Folder>>()
    var totalVideos: String? = null
    val position = MutableLiveData(-1)

    private val projections = arrayOf(
        MediaStore.Video.Media._ID,
        MediaStore.Video.Media.DISPLAY_NAME,
        MediaStore.Video.Media.DURATION,
        MediaStore.Video.Media.SIZE,
        MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
        MediaStore.Video.Media.DATE_ADDED,
        MediaStore.Video.Media.DATA,
        MediaStore.Video.Media.BUCKET_ID
    )

    init {
        Log.d(TAG, "viewModel -> ${this.hashCode()}")

    }

    private fun getCursor(
        context: Context,
        uri: Uri,
        projections: Array<String>?,
        selections: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? =
        context.contentResolver.query(
            uri,
            projections,
            selections,
            selectionArgs,
            sortOrder
        )


    private suspend fun queryForVideosData(
        context: Context,
        contentUri: Uri,
        projections: Array<String>?,
        selections: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
    ): List<Video> {
        val dataList = ArrayList<Video>()

        return withContext(Dispatchers.IO) {
            supervisorScope {
                try {
                    getCursor(context, contentUri, projections, selections, selectionArgs, sortOrder)?.apply {

                        val idColumn = getColumnIndex(_ID)
                        val displayColumn = getColumnIndex(DISPLAY_NAME)
                        val durationColumn = getColumnIndex(DURATION)
                        val sizeColumn = getColumnIndex(SIZE)
                        val dateAddedColumn = getColumnIndex(DATE_ADDED)
                        val bktDisplayColumn = getColumnIndex(BUCKET_DISPLAY_NAME)
                        val videoPath = getColumnIndex(DATA)

                        while (moveToNext()) {
                            val name = getStringValue(columnIdx = displayColumn)
                            val id = getLongValue(columnIdx = idColumn)
                            val duration = getLongValue(columnIdx = durationColumn)
                            val size = getLongValue(columnIdx = sizeColumn)
                            val dateAddedCol = getStringValue(columnIdx = dateAddedColumn)
                            val folderNameCol = getStringValue(columnIdx = bktDisplayColumn)
                            val pathCol = getStringValue(columnIdx = videoPath)

                            val videoUri = ContentUris.withAppendedId(contentUri!!, id)

                            val file = File(pathCol)
                            if (file.exists()) {
                                dataList.add(
                                    Video(
                                        videoId = id.toString(),
                                        videoUri = videoUri,
                                        videoTitle = name,
                                        videoDuration = duration,
                                        videoSize = size,
                                        videoDateAdded = dateAddedCol,
                                        videoFolderName = folderNameCol,
                                        videoPath = pathCol
                                    )
                                )
                            }
                        }

                        Log.d(TAG, "fetchVideos() -> cursor ${this.hashCode()}")
                        close()
                    }
                } catch (e: FileNotFoundException) {
                    Log.d(TAG, "${e.message}")
                    emptyList<Video>()
                }
            }
            dataList
        }
    }


    fun fetchAllVideos(context: Context) {
        if (contentUri==null) return
        val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"

        viewModelScope.launch {
            val videos: List<Video> = queryForVideosData(
                context = context,
                contentUri = contentUri!!,
                projections = projections,
                selections = null,
                selectionArgs = null,
                sortOrder = sortOrder
            )

            videoList.value = videos
        }
    }

    fun fetchAllfolders(context: Context) {
        if (contentUri==null) return
        val sortOrder = "$BUCKET_DISPLAY_NAME ASC"

        getCursor(context, contentUri!!, projections, null, null, sortOrder)?.apply {

            val bktIdColumn = getColumnIndex(BUCKET_ID)
            val bktDisplayColumn = getColumnIndex(BUCKET_DISPLAY_NAME)
            val pathColumn = getColumnIndex(DATA)

            val folderSet = mutableSetOf<Folder>()
            while (moveToNext()) {
                val folderIdCol = getStringValue(columnIdx = bktIdColumn)
                val folderNameCol = getStringValue(columnIdx = bktDisplayColumn)
                val pathCol = getStringValue(columnIdx = pathColumn)

                try {
                    val file = File(pathCol)
                    if (file.exists()) {
                        folderSet.add(
                            Folder(
                                folderId = folderIdCol,
                                folderName = folderNameCol
                            )
                        )
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "${e.message}")
                }


            }
            folderList.value = folderSet.toList()
            close()
        }

    }

    fun fetchVideosOfFolder(folderId: String, context: Context) {
        if (contentUri==null) return
        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(folderId)
        val sortOrder = MediaStore.Video.Media.DATE_ADDED + " DESC"

        viewModelScope.launch {
            val videos: List<Video> = queryForVideosData(
                context = context,
                contentUri = contentUri!!,
                projections = projections,
                selections = selection,
                selectionArgs = selectionArgs,
                sortOrder = sortOrder
            )
            videoList.value = videos
            totalVideos = "Total Videos: ${videos.size}"
        }
    }

    fun playNextVideo() {
        if (position.value!! == videoList.value!!.size - 1) {
            position.value = 0
        } else if (position.value!! < videoList.value!!.size - 1) {
            position.value = position.value?.plus(1)
        }
    }

    fun playPrevVideo() {
        if (position.value!! == 0) {
            position.value = videoList.value?.size!! - 1
        }
        else{
            position.value = position.value?.minus(1)
        }
    }

    fun getRecentVideoItems(): StateFlow<List<Video>> {
        val recentWatchList = MutableStateFlow<List<Video>>(emptyList())
        viewModelScope.launch(Dispatchers.IO) {
            recentWatchDataSource.getVideos().collectLatest {
                recentWatchList.emit(it)
            }
        }
        return recentWatchList
    }

    fun updateRecentVideoItemInDb(lastWatchTime: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            recentWatchDataSource.getVideoById(currentVideo.videoId).collect() {video ->
                if (video != null) {
                    recentWatchDataSource.updateVideoItem(
                        id = video.videoId,
                        lastWatchTime = lastWatchTime
                    )
                }else {
                    recentWatchDataSource.saveVideo(currentVideo.copy(
                        lastWatchTime = lastWatchTime
                    ))
                }
            }
        }
    }


}