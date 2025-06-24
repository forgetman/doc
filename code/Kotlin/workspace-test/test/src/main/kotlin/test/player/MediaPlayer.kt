package test.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

interface MediaPlayer : Player {
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

        fun build(): MediaPlayer {
            val attributes = this.attributes
            checkNotNull(attributes) { "Required attributes was null." }
            val id = id ?: "MediaPlayer-${System.currentTimeMillis()}"
            return MediaPlayerImpl(context, id, volumeChannel ?: id, attributes)
        }
    }
}

internal class MediaPlayerImpl(
    context: Context,
    id: String,
    volumeChannel: String,
    attributes: AudioAttributes
) : BasePlayer(context, id, volumeChannel, attributes), MediaPlayer {

    private val exoPlayer = ExoPlayer.Builder(context).apply {
        setAudioAttributes(attributes, false)
    }.build()

    private val mediaItems: MutableList<MediaItem> = mutableListOf()

    init {
        exoPlayer.addListener(object : Listener {

            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                val playerCommand = updateAudioFocus(playWhenReady, playbackState)
                updatePlayWhenReady(playWhenReady, playerCommand)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                val playerCommand = updateAudioFocus(playWhenReady, playbackState)
                updatePlayWhenReady(playWhenReady, playerCommand)
            }

            override fun onPlaybackSuppressionReasonChanged(playbackSuppressionReason: Int) {
                invalidateState()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
//                if (isPlaying) {
//                    if (allowRestoreVolumeWhenPlaying()) {
//                        //重置成当前Channel
//                        Volume.Channel.bringToTop(getVolumeChannel())
//                        Volume.restore(getVolumeChannel())
//                    }
//                    PlayerManager.setFocusedPlayer(this@MediaPlayerImpl)
//                }
                invalidateState()
            }

            override fun onPlayerError(error: PlaybackException) {
                invalidateState()
            }

            override fun onIsLoadingChanged(isLoading: Boolean) {
                invalidateState()
            }
        })
    }

    override fun getState(): State {
        return baseState.buildUpon()
            .setAudioAttributes(exoPlayer.audioAttributes)
            .setPlayWhenReady(exoPlayer.playWhenReady, PLAY_WHEN_READY_CHANGE_REASON_USER_REQUEST)
            .setIsLoading(exoPlayer.isLoading)
            .setPlaylist(mediaItems.map { getPlaceholderMediaItemData(it) })
            .setPlaybackState(exoPlayer.playbackState)
            .setPlaybackSuppressionReason(exoPlayer.playbackSuppressionReason)
            .setPlayerError(exoPlayer.playerError)
            .setVolume(exoPlayer.volume)
            .build()
    }

    override fun handlePrepare(): ListenableFuture<*> {
        exoPlayer.prepare()
        return Futures.immediateVoidFuture()
    }

    override fun handleSetPlayWhenReady(playWhenReady: Boolean): ListenableFuture<*> {
        val playerCommand: @AudioFocusManager.PlayerCommand Int = updateAudioFocus(playWhenReady, playbackState)
        val newState = playWhenReady && playerCommand != AudioFocusManager.PLAYER_COMMAND_DO_NOT_PLAY
        if (newState && playbackState == STATE_ENDED) {
            // 自动还原到默认起始位置
            seekToDefaultPosition()
        }
        if (exoPlayer.playWhenReady == newState) return Futures.immediateCancelledFuture<Unit>()
        exoPlayer.playWhenReady = newState
        return Futures.immediateVoidFuture()
    }

    override fun handleStop(): ListenableFuture<*> {
        updateAudioFocus(playWhenReady, STATE_IDLE)
        exoPlayer.stop()
        return Futures.immediateVoidFuture()
    }

    override fun handleSetMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<*> {
        this.mediaItems.clear()
        this.mediaItems.addAll(mediaItems)
        exoPlayer.setMediaItems(mediaItems, startIndex, startPositionMs)
        return Futures.immediateVoidFuture()
    }

    override fun handleSeek(mediaItemIndex: Int, positionMs: Long, seekCommand: Int): ListenableFuture<*> {
        exoPlayer.seekTo(mediaItemIndex, positionMs)
        return Futures.immediateVoidFuture()
    }

    override fun handleSetVolume(volume: Float): ListenableFuture<*> {
        exoPlayer.volume = volume
        return Futures.immediateVoidFuture()
    }

    override fun handleRelease(): ListenableFuture<*> {
        super.handleRelease()

        exoPlayer.release()
        return Futures.immediateVoidFuture()
    }
}