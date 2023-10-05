package com.farhazulmullick.feature.allvideos.ui.composable


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.farhazulmullick.core_ui.commoncomposable.YSpacer
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.videoplayer.R
import java.io.File

@Composable
fun ContinueWatch(
    videoList: List<Video>,
    onVideoItemClicked: (Video) -> Unit
) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .padding(all = 8.dp)
    ) {

        Text(
            text = "Continue Watching",
            style = MaterialTheme.typography.titleMedium)

        YSpacer(gap = 16.dp)

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(space = 12.dp),
            content = {
            itemsIndexed (items = videoList){index: Int, item: Video ->
                VideoUi(videoItem = item, onVideoItemClicked = onVideoItemClicked)
            }
        })
    }

}

@Composable
fun VideoUi(
    videoItem: Video,
    onVideoItemClicked: (Video) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .width(width = 120.dp)
            .clickable { onVideoItemClicked(videoItem) }
    ){
        Image(
            modifier = Modifier
                .width(width = 120.dp)
                .clip(shape = MaterialTheme.shapes.medium)
                .aspectRatio(ratio = 1.7f),
            painter = painterResource(id = R.drawable.ic_play_round_black),
            contentDescription = null
        )

        Text(
            modifier = Modifier.width(width = 120.dp).padding(all = 8.dp),
            text = videoItem.videoTitle,
            maxLines = 2,
            style = MaterialTheme.typography.bodySmall
        )
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
fun PreviewVideo() {
    VideoUi(videoItem = Video(videoTitle = "Video1"))
}