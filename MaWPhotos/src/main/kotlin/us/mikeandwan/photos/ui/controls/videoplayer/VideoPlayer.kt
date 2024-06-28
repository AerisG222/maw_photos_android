package us.mikeandwan.photos.ui.controls.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.ui.getMediaUrl

@Composable
fun VideoPlayer(
    activeMedia: Media,
    videoPlayerHttpDataSourceFactory: HttpDataSource.Factory,
    modifier: Modifier = Modifier
) {
    if(activeMedia.type != MediaType.Video) {
        return
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val exoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(
            DefaultMediaSourceFactory(context)
                .setDataSourceFactory(videoPlayerHttpDataSourceFactory)
        )
        .build()
        .apply {
            setMediaItem(MediaItem.fromUri(activeMedia.getMediaUrl()))
            prepare()
            playWhenReady = true
        }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { lifecycle.currentState }
            .collect { state ->
                when (state) {
                    Lifecycle.State.STARTED -> if (!exoPlayer.isPlaying) exoPlayer.play()
                    Lifecycle.State.DESTROYED -> exoPlayer.pause()
                    else -> {}
                }
            }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    )
}
