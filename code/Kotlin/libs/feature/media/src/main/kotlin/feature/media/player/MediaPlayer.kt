package feature.media.player

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.Listener
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import feature.media.AudioFocusManager
import feature.media.player.base.BasePlayer
import logger.L
import java.util.concurrent.TimeUnit

interface MediaPlayer : Player {
    fun setMediaSource(source: MediaSource)
    fun setMediaSources(sources: List<MediaSource>)

    class Builder(private val context: Context) {

        private var id: String? = null
        private var attributes: AudioAttributes? = null
        private var ignoreAudioFocusLoss: Boolean = false

        fun setId(id: String): Builder {
            this.id = id
            return this
        }

        fun setAudioAttributes(attributes: AudioAttributes): Builder {
            this.attributes = attributes
            return this
        }

        fun setIgnoreAudioFocusLoss(ignore: Boolean): Builder {
            ignoreAudioFocusLoss = ignore
            return this
        }

        fun build(): MediaPlayer {
            val attributes = this.attributes
            checkNotNull(attributes) { "Required attributes was null." }
            val id = id ?: "MediaPlayer-${System.currentTimeMillis()}"
            return MediaPlayerImpl(
                context,
                id,
                attributes,
                ignoreAudioFocusLoss
            )
        }
    }
}

internal class MediaPlayerImpl(
    context: Context,
    id: String,
    attributes: AudioAttributes,
    ignoreAudioFocusLoss: Boolean
) : BasePlayer(
    context,
    id,
    attributes,
    ignoreAudioFocusLoss
), MediaPlayer {

    companion object {
        private const val LOG_TAG = "MediaPlayer"
    }

    private val exoPlayer = ExoPlayer.Builder(context).apply {
        setAudioAttributes(attributes, false)
    }.build()

    private val sources = mutableListOf<MediaSource>()
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
            .setPlaylist(run {
                val duration = exoPlayer.duration
                val durationUs = when {
                    duration == C.TIME_UNSET -> duration
                    duration < 0 -> 0
                    else -> TimeUnit.MILLISECONDS.toMicros(duration)
                }
                when {
                    sources.isNotEmpty() -> sources.map {
                        getPlaceholderMediaItemData(it.mediaItem).buildUpon()
                            .setDurationUs(durationUs)
                            .build()
                    }

                    mediaItems.isNotEmpty() -> mediaItems.map {
                        getPlaceholderMediaItemData(it).buildUpon()
                            .setDurationUs(durationUs)
                            .build()
                    }

                    else -> emptyList()
                }
            })
            .setContentPositionMs(exoPlayer.contentPosition)
            .setContentBufferedPositionMs { exoPlayer.contentBufferedPosition }
            .setTotalBufferedDurationMs { exoPlayer.totalBufferedDuration }
            .setCurrentMediaItemIndex(exoPlayer.currentMediaItemIndex)
            .setPlaybackState(exoPlayer.playbackState)
            .setPlaybackSuppressionReason(exoPlayer.playbackSuppressionReason)
            .setPlayerError(exoPlayer.playerError)
            .setRepeatMode(exoPlayer.repeatMode)
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
        exoPlayer.stop()
        return Futures.immediateVoidFuture()
    }

    override fun handleSetMediaItems(
        mediaItems: MutableList<MediaItem>,
        startIndex: Int,
        startPositionMs: Long
    ): ListenableFuture<*> {
        this.sources.clear()
        this.mediaItems.clear()
        this.mediaItems.addAll(mediaItems)
        exoPlayer.setMediaItems(mediaItems, startIndex, startPositionMs)
        return Futures.immediateVoidFuture()
    }

    override fun handleSetVolume(volume: Float): ListenableFuture<*> {
        exoPlayer.volume = volume
        return Futures.immediateVoidFuture()
    }

    override fun handleSeek(mediaItemIndex: Int, positionMs: Long, seekCommand: Int): ListenableFuture<*> {
        L.d(LOG_TAG, "handleSeek, mediaItemIndex: $mediaItemIndex, positionMs: $positionMs, seekCommand: $seekCommand")
        exoPlayer.seekTo(mediaItemIndex, positionMs)
        return Futures.immediateVoidFuture()
    }

    override fun handleSetRepeatMode(repeatMode: Int): ListenableFuture<*> {
        exoPlayer.repeatMode = repeatMode
        return Futures.immediateVoidFuture()
    }

    override fun handleRelease(): ListenableFuture<*> {
        super.handleRelease()

        exoPlayer.release()

        return Futures.immediateVoidFuture()
    }

    /**
     * 特意暴露出来的方法，用于设置播放源, exo专用
     */
    override fun setMediaSource(source: MediaSource) {
        this.mediaItems.clear()
        this.sources.clear()
        this.sources.add(source)
        exoPlayer.setMediaSources(sources)
    }

    override fun setMediaSources(sources: List<MediaSource>) {
        mediaItems.clear()
        this.sources.clear()
        this.sources.addAll(sources)
        exoPlayer.setMediaSources(sources)
    }
}