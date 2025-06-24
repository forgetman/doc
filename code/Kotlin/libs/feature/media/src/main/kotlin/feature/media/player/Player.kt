package feature.media.player

import androidx.media3.common.Player

interface Player : Player {
    companion object

    fun getId(): String
}