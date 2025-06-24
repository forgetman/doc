package feature.media.ext

import androidx.annotation.RawRes
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import feature.media.player.MediaPlayer
import feature.media.player.Player

/**
 * 音乐属性, 同级打断不恢复
 */
fun Player.Companion.musicAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .build()
}

/**
 * 打断MUSIC，停止后恢复MUSIC播放
 */
fun Player.Companion.interruptNotificationAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_ASSISTANT)
        .setContentType(C.AUDIO_CONTENT_TYPE_SONIFICATION)
        .build()
}

/**
 * 提示音属性
 * 仓: 不打断MUSIC，只会让其音量降低(如果这时候通知是静音的, 还是会降低音量, 但是听不见提示音)
 * 手机: 打断然后恢复
 */
fun Player.Companion.notificationAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_NOTIFICATION)
        .setContentType(C.AUDIO_CONTENT_TYPE_SONIFICATION)
        .build()
}

fun Player.Companion.ringAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_NOTIFICATION_RINGTONE)
        .setContentType(C.AUDIO_CONTENT_TYPE_SONIFICATION)
        .build()
}

fun Player.Companion.callAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_VOICE_COMMUNICATION)
        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
        .build()
}

fun Player.Companion.unknownAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_UNKNOWN)
        .setContentType(C.AUDIO_CONTENT_TYPE_UNKNOWN)
        .build()
}

fun Player.Companion.speechAudioAttributes(): AudioAttributes {
    return AudioAttributes.Builder()
        .setUsage(C.USAGE_MEDIA)
        .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
        .build()
}

fun Player.isMusic(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_MUSIC
        && audioAttributes.usage == C.USAGE_MEDIA
}

fun Player.isSpeech(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_SPEECH
        && audioAttributes.usage == C.USAGE_ASSISTANT
}

fun Player.isNotification(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_SONIFICATION
        && audioAttributes.usage == C.USAGE_NOTIFICATION
}

fun Player.isCall(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_SPEECH
        && audioAttributes.usage == C.USAGE_VOICE_COMMUNICATION
}

fun Player.isUnknown(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_UNKNOWN
        && audioAttributes.usage == C.USAGE_UNKNOWN
}

fun Player.isRing(): Boolean {
    return audioAttributes.contentType == C.AUDIO_CONTENT_TYPE_SONIFICATION
        && audioAttributes.usage == C.USAGE_NOTIFICATION_RINGTONE
}

fun Player.prepare(uri: String) {
    setMediaItem(MediaItem.fromUri(uri))
    prepare()
}

fun Player.prepare(@RawRes id: Int) {
    setMediaItem(MediaItem.fromUri("rawresource://$id"))
    prepare()
}

fun Player.prepareAsset(assetFileName: String) {
    setMediaItem(MediaItem.fromUri("asset://$assetFileName"))
    prepare()
}

fun MediaPlayer.prepare(data: ByteArray) {
    val source = ProgressiveMediaSource.Factory {
        ByteArrayDataSource(data)
    }.createMediaSource(MediaItem.fromUri("bytes://${System.currentTimeMillis()}"))
    setMediaSource(source)
}