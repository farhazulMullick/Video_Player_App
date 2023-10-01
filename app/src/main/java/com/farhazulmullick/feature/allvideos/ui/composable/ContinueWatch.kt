package com.farhazulmullick.feature.allvideos.ui.composable


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.farhazulmullick.feature.allvideos.modal.Video
import com.farhazulmullick.videoplayer.R

@Composable
fun ContinueWatch(
    videoList: List<Video>,
    onVideoItemClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(all = 8.dp)
    ) {

        Text(
            text = "Continue Watching",
            style = MaterialTheme.typography.titleMedium)

        YSpacer(gap = 8.dp)

        LazyRow(content = {
            itemsIndexed (items = videoList){index: Int, item: Video ->
                VideoUi(videoItem = item, onVideoItemClicked = onVideoItemClicked)
            }
        })
    }

}

@Composable
fun YSpacer(gap: Dp){
    Spacer(modifier = Modifier.height(height = gap))
}

@Composable
fun VideoUi(
    videoItem: Video,
    onVideoItemClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .clickable { onVideoItemClicked() }
            .background(color = Color.Unspecified, shape = MaterialTheme.shapes.medium)
    ){
        Image(
            modifier = Modifier
                .width(width = 200.dp)
                .aspectRatio(ratio = 1.7f),
            painter = painterResource(id = R.drawable.ic_play_round_black),
            contentDescription = null
        )

        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = videoItem.videoTitle,
            maxLines = 2,
            style = MaterialTheme.typography.bodyLarge
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