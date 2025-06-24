package feature.tts.cache

import com.jakewharton.disklrucache.DiskLruCache
import feature.tts.model.Params
import logger.L
import sugar.ext.safeUse
import vector.ext.ensureDirExist
import java.io.File

interface AudioCache {
    val directory: String

    /**
     * 如果版本号更新，DiskLruCache会清除之前的缓存
     */
    val version: Int

    fun write(params: Params, text: CharSequence, data: ByteArray)

    fun read(params: Params, text: CharSequence): ByteArray?

    fun getPath(params: Params, text: CharSequence): String

    fun hasCache(params: Params, text: CharSequence): Boolean

    fun remove(params: Params, text: CharSequence)

    fun clear()
}

@Suppress("FunctionName")
fun TextToSpeechAudioCache(directory: String, version: Int): AudioCache = AudioCacheImpl(directory, version)

private class AudioCacheImpl(override val directory: String, override val version: Int) : AudioCache {

    companion object {
        private const val LOG_TAG = "AudioCache"
        private const val MAX_SIZE = 1024 * 1024 * 10L // 缓存大小 10M
    }

    override fun write(params: Params, text: CharSequence, data: ByteArray) {
        val cacheKey = params.buildCacheKey(text)
        L.d(LOG_TAG, "write, cacheId: $cacheKey")
        openCache().safeUse { cache ->
            val editor = cache.edit(cacheKey)
            editor.newOutputStream(0).safeUse { output ->
                output.write(data)
            }
            editor.commit()
        }
    }

    override fun read(params: Params, text: CharSequence): ByteArray? {
        val cacheKey = params.buildCacheKey(text)
        L.d(LOG_TAG, "read, cacheId: $cacheKey")
        return openCache().safeUse { cache ->
            val snapshot = cache.get(cacheKey) ?: return null
            snapshot.safeUse {
                it.getInputStream(0).safeUse { input ->
                    input.readBytes()
                }
            }
        }
    }

    override fun getPath(params: Params, text: CharSequence): String {
        val cacheKey = params.buildCacheKey(text)
        return File(directory, cacheKey).absolutePath
    }

    override fun hasCache(params: Params, text: CharSequence): Boolean {
        val cacheKey = params.buildCacheKey(text)
        L.d(LOG_TAG, "hasCache, cacheId: $cacheKey")
        return openCache().safeUse { cache ->
            cache.get(cacheKey) != null
        } == true
    }

    override fun remove(params: Params, text: CharSequence) {
        val cacheKey = params.buildCacheKey(text)
        L.d(LOG_TAG, "remove, cacheId: $cacheKey")
        openCache().safeUse { cache ->
            cache.remove(cacheKey)
        }
    }

    override fun clear() {
        L.d(LOG_TAG, "clear")
        openCache().safeUse { cache ->
            cache.delete()
        }
    }

    private fun openCache(): DiskLruCache {
        val file = File(directory)
        file.ensureDirExist(false)
        return DiskLruCache.open(file, version, 1, MAX_SIZE)
    }

    private fun Params.buildCacheKey(text: CharSequence): String {
        val volume = (this.volume * 100).toInt()
        val speed = (this.speed * 100).toInt()
        return "${text.hashCode()}_${volume}_${volumeGain}_${speed}_${pitch}_${style.hashCode()}_${speaker.hashCode()}"
    }
}