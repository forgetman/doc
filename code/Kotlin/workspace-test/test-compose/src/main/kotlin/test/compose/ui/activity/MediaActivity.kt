package test.compose.ui.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.MediaItem
import feature.media.ext.musicAudioAttributes
import feature.media.player.MediaPlayer
import feature.media.player.Player
import test.compose.ext.AppBar
import test.compose.ui.FlowButton
import test.compose.ui.FlowContent
import vector.app.compose.ui.activity.SimpleComposeActivityEx
import androidx.media3.common.Player as Media3Player

/**
 * @author yuansui
 * @since 2025/6/21
 */
class MediaActivity : SimpleComposeActivityEx() {

    private val player by lazy {
        MediaPlayer.Builder(this)
            .setAudioAttributes(Player.musicAudioAttributes())
            .build().apply {
                repeatMode = Media3Player.REPEAT_MODE_OFF
                setMediaItem(MediaItem.fromUri("asset:///bcg.mp3"))
                prepare()
                playWhenReady = false
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                AppBar(title = "Media")
            }
        ) { innerPadding ->
            FlowContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                FlowButton("播放") {
                    player.play()
                }
                FlowButton("暂停") {
                    player.pause()
                }
            }
        }
    }

    @Composable
    @Preview
    fun PreviewContent() {
        MaterialTheme(colorScheme = darkColorScheme()) {
            Content()
        }
    }
}