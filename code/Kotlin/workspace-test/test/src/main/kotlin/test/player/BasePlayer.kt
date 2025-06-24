package test.player

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.SimpleBasePlayer
import androidx.media3.session.MediaSession
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * 基础player
 */
internal abstract class BasePlayer(
    private val context: Context,
    private val id: String,
    private val volumeChannel: String,
    private val attributes: AudioAttributes
) : SimpleBasePlayer(Looper.getMainLooper()), test.player.Player {

    protected open val baseState: State = State.Builder()
        .setAvailableCommands(
            Player.Commands.Builder().addAll(
                COMMAND_PREPARE,
                COMMAND_PLAY_PAUSE,
                COMMAND_STOP,
                COMMAND_RELEASE,

                COMMAND_SEEK_TO_DEFAULT_POSITION,
                COMMAND_SEEK_TO_MEDIA_ITEM,
                COMMAND_SEEK_IN_CURRENT_MEDIA_ITEM,

                COMMAND_CHANGE_MEDIA_ITEMS,
                COMMAND_SET_MEDIA_ITEM,

                COMMAND_GET_TIMELINE,

                COMMAND_SET_VOLUME,
                COMMAND_GET_VOLUME,
            ).build()
        )
        .setAudioAttributes(attributes)
        .build()

    private val playerControl = object : AudioFocusManager.PlayerControl {
        override fun setVolumeMultiplier(volumeMultiplier: Float) {
            this@BasePlayer.volume = volumeMultiplier
        }

        override fun executePlayerCommand(playerCommand: Int) {
            updatePlayWhenReady(playWhenReady, playerCommand)
        }
    }
    private val handler = Handler(Looper.getMainLooper())
    private val audioFocusManager = AudioFocusManager(context, handler, playerControl)

    private var mediaSession: MediaSession? = null

    /**
     * 临时丢失焦点
     * @see [AudioFocusManager.PLAYER_COMMAND_WAIT_FOR_CALLBACK]
     */
    private var transientAudioFocusLoss = false

    init {
        audioFocusManager.setAudioAttributes(attributes)
        initMediaSession()
    }

    private fun initMediaSession() {
        /**
         * 只有[C.USAGE_MEDIA]和[C.USAGE_GAME]允许接收media控制
         */
        if (attributes.usage != C.USAGE_MEDIA && attributes.usage != C.USAGE_GAME) return
        handler.post {
            fun setSessionPositionUpdateDelayMs(mediaSession: MediaSession, updateDelayMs: Long) {
                val method = MediaSession::class.java.getDeclaredMethod(
                    "setSessionPositionUpdateDelayMs",
                    Long::class.java
                )
                method.isAccessible = true
                method.invoke(mediaSession, updateDelayMs)
            }

            val newSession = MediaSession.Builder(context, this)
                .setId(id)
                .build()
            setSessionPositionUpdateDelayMs(newSession, 0)
            mediaSession = newSession
        }
    }

    protected fun updatePlayWhenReady(
        playWhenReady: Boolean,
        playerCommand: @AudioFocusManager.PlayerCommand Int,
    ) {
        /**
         * 如果是playerCommand是PLAYER_COMMAND_WAIT_FOR_CALLBACK,
         * 由于playWhenReady置为false了,
         * 还会回调一次PLAYER_COMMAND_DO_NOT_PLAY
         */
        when (playerCommand) {
            AudioFocusManager.PLAYER_COMMAND_PLAY_WHEN_READY -> {
                if (playWhenReady || transientAudioFocusLoss) {
                    if (!this.playWhenReady) {
                        transientAudioFocusLoss = false
                        this.playWhenReady = true
                    }
                }
            }

            AudioFocusManager.PLAYER_COMMAND_WAIT_FOR_CALLBACK -> {
                transientAudioFocusLoss = true
                if (this.playWhenReady) this.playWhenReady = false
            }

            AudioFocusManager.PLAYER_COMMAND_DO_NOT_PLAY -> {
                if (this.playWhenReady) this.playWhenReady = false
            }
        }
    }

    protected fun updateAudioFocus(playWhenReady: Boolean, @Player.State playbackState: Int): Int {
        return audioFocusManager.updateAudioFocus(playWhenReady, playbackState)
    }

    abstract override fun handleSetVolume(volume: Float): ListenableFuture<*>

    @CallSuper
    override fun handleRelease(): ListenableFuture<*> {
        audioFocusManager.release()

        mediaSession?.release()
        mediaSession = null

        return Futures.immediateVoidFuture()
    }

    override fun getId(): String {
        return id
    }

    override fun getVolumeChannel(): String {
        return volumeChannel
    }

    protected open fun allowRestoreVolumeWhenPlaying(): Boolean {
        return true
    }
}