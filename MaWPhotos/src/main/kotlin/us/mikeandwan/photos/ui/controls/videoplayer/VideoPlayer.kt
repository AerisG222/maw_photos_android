package us.mikeandwan.photos.ui.controls.videoplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.datasource.HttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import us.mikeandwan.photos.domain.models.Media
import us.mikeandwan.photos.domain.models.MediaType
import us.mikeandwan.photos.ui.getMediaUrl

// thank you gemini...
@Composable
fun VideoPlayer(
    activeMedia: Media,
    videoPlayerHttpDataSourceFactory: HttpDataSource.Factory,
    modifier: Modifier
) {
    if(activeMedia.type != MediaType.Video) {
        return
    }

    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer
            .Builder(context)
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
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.pause()
            exoPlayer.release()
        }
    }

    // credit: https://medium.com/@munbonecci/how-to-display-videos-using-exoplayer-on-android-with-jetpack-compose-1fb4d57778f4
    // Add a lifecycle observer to manage player state based on lifecycle events
    LocalLifecycleOwner.current.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_START -> {
                    if (exoPlayer.isPlaying.not()) {
                        exoPlayer.play()
                    }
                }

                // some of this may be duplicative w/ disposable effect above but keeping to try
                // to provide extra assurance the player is paused/removed
                Lifecycle.Event.ON_STOP -> { exoPlayer.pause() }
                Lifecycle.Event.ON_PAUSE -> { exoPlayer.pause() }
                Lifecycle.Event.ON_DESTROY -> { exoPlayer.release() }
                else -> { }
            }
        }
    })

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
            }
        }
    )
}
