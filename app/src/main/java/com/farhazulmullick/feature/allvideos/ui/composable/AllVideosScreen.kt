package com.farhazulmullick.feature.allvideos.ui.composable


import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.farhazulmullick.core_ui.commoncomposable.YSpacer
import com.farhazulmullick.core_ui.extensions.getBitmap
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.feature.allvideos.viewmodel.VideoViewModel
import com.farhazulmullick.videoplayer.R


@Composable
fun VideosScreen(
    viewModel : VideoViewModel,
    onVideoItemClicked: (Video) -> Unit
) {
    val videoList by viewModel.videoList.observeAsState()

    LazyColumn(modifier = Modifier.padding(all = 8.dp)) {

        item {
            ContinueWatch(
                videoList = videoList ?: emptyList(),
                onVideoItemClicked = onVideoItemClicked
            )

            YSpacer(gap = 16.dp)
        }

        AllVideos(
            videoList = videoList ?: emptyList(),
            onVideoItemClicked = onVideoItemClicked
        )

    }

}

@Composable
fun ContinueWatch(
    videoList: List<Video>,
    onVideoItemClicked: (Video) -> Unit
) {
    Column {

        Text(
            text = "Continue Watching",
            style = MaterialTheme.typography.titleLarge)

        YSpacer(gap = 16.dp)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            content = {
            itemsIndexed (items = videoList){index: Int, item: Video ->
                VideoUiH(videoItem = item, onVideoItemClicked = onVideoItemClicked)
            }
        })
    }

}

@Composable
fun VideoUiH(
    videoItem: Video,
    onVideoItemClicked: (Video) -> Unit = {}
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .width(width = 110.dp)
            .clickable { onVideoItemClicked(videoItem) }
    ){
        var thumbnail by remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(key1 = true, block = {
            thumbnail = videoItem.videoUri?.let{ context.getBitmap(uri = it) }
        })

        thumbnail?.let { bitmap ->
            ThumbnailImage(thumbnail = bitmap)
        }

        Text(
            modifier = Modifier
                .width(width = 120.dp)
                .padding(all = 8.dp),
            text = videoItem.videoTitle,
            maxLines = 2,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun VideoUiV(
    videoItem: Video,
    onVideoItemClicked: (Video) -> Unit = {}
) {
    val context = LocalContext.current
    var thumbnail by remember { mutableStateOf<Bitmap?>(null) }
    LaunchedEffect(key1 = true, block = {
        thumbnail = videoItem.videoUri?.let { context.getBitmap(uri = it) }
    })
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onVideoItemClicked(videoItem) }
    ) {

        thumbnail?.let {bitmap ->
            ThumbnailImage(thumbnail = bitmap)
        }

        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            // video title
            Text(
                text = videoItem.videoTitle,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )

            // folder name
            Text(
                text = videoItem.videoFolderName,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ThumbnailImage(thumbnail: Bitmap) {
    Box (
        modifier = Modifier.wrapContentSize(),
        contentAlignment = Alignment.Center){
        Image(
            modifier = Modifier
                .height(height = 70.dp)
                .aspectRatio(ratio = 1.571f)
                .clip(shape = MaterialTheme.shapes.small),
            bitmap = thumbnail.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Icon(
            modifier = Modifier
                .size(size = 20.dp)
                .alpha(alpha = 0.8f),
            painter = painterResource(R.drawable.ic_play_round_black),
            contentDescription = null,
            tint = Color.White
        )
    }
}


fun LazyListScope.AllVideos(
    videoList: List<Video>,
    onVideoItemClicked: (Video) -> Unit = {}) {

    items(count = videoList.size) {
        VideoUiV(videoItem = videoList[it], onVideoItemClicked = onVideoItemClicked )
        YSpacer(gap = 12.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewContinueLearning() {
    ContinueWatch(videoList = listOf(
            Video(videoTitle = "Video1"),
            Video(videoTitle = "Video2"),
            Video(videoTitle = "Video3"),
            Video(videoTitle = "Video4"),
            Video(videoTitle = "Video5"),
        ),
        onVideoItemClicked = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewVideoUiH() {
    VideoUiH(videoItem = Video(videoTitle = "Video1"))
}