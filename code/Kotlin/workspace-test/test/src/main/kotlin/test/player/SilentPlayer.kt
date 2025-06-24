package test.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import test.player.Player as SelfPlayer

interface SilentPlayer : SelfPlayer {
    class Builder(private val context: Context) {

        private var id: String? = null
        private var attributes: AudioAttributes? = null
        private var volumeChannel: String? = null

        fun setId(id: String): Builder {
            this.id = id
            return this
        }

        fun setAudioAttributes(attributes: AudioAttributes): Builder {
            this.attributes = attributes
            return this
        }

        fun setVolumeChannel(channel: String): Builder {
            this.volumeChannel = channel
            return this
        }

        fun build(): SilentPlayer {
            val id = id ?: "MediaPlayer-${System.currentTimeMillis()}"
            val attributes = this.attributes ?: SelfPlayer.musicAudioAttributes()
            return SilentPlayerImpl(context, id, volumeChannel ?: id, attributes)
        }
    }
}

/**
 * 无声音频播放器
 */
internal class SilentPlayerImpl(
    context: Context,
    id: String,
    volumeChannel: String,
    attributes: AudioAttributes
) : BasePlayer(context, id, volumeChannel, attributes), SilentPlayer {

    private var playWhenReady = false
    private var playbackState: Int = Player.STATE_IDLE
    private var playbackSuppressionReason: Int = PLAYBACK_SUPPRESSION_REASON_NONE

    private val mediaItems: MutableList<MediaItem> = mutableListOf()

    init {
        mediaItems.add(MediaItem.Builder().build())
    }

    override fun getState(): State {
        return baseState.buildUpon()
            .setPlayWhenReady(this.playWhenReady, PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            .setPlaylist(mediaItems.map { getPlaceholderMediaItemData(it) })
            .setPlaybackState(this.playbackState)
            .setPlaybackSuppressionReason(this.playbackSuppressionReason)
            .build()
    }

    override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {
        val playerCommand: @AudioFocusManager.PlayerCommand Int = updateAudioFocus(playWhenReady, playbackState)
        val newState = playWhenReady && playerCommand != AudioFocusManager.PLAYER_COMMAND_DO_NOT_PLAY
        if (newState && playbackState == STATE_ENDED) {
            // 自动还原到默认起始位置
            seekToDefaultPosition()
        }
        if (this.playWhenReady == newState) return Futures.immediateCancelledFuture<Unit>()
        this.playWhenReady = newState
        return Futures.immediateVoidFuture()
    }

    override fun handlePrepare(): ListenableFuture<*> {
        playbackState = STATE_READY
//        updateAudioFocus(playWhenReady, playbackState)
        return Futures.immediateVoidFuture()
    }

    override fun handleStop(): ListenableFuture<*> {
        playbackState = STATE_IDLE
        updateAudioFocus(playWhenReady, playbackState)
        return Futures.immediateVoidFuture()
    }

    override fun handleSetVolume(volume: Float): ListenableFuture<*> {
        return Futures.immediateCancelledFuture<Unit>()
    }
}