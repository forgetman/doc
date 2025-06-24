package vector.util

import android.media.AudioFormat


/**
 * 生成wav格式的头部信息
 * 格式参考: http://soundfile.sapp.org/doc/WaveFormat/
 *
 * @param audioLength 音频长度
 * @param numChannels 通道数, Mono = 1, Stereo = 2, etc.
 * @param sampleRate 采样深度, 8000, 44100, etc.
 * @param audioFormat [android.media.AudioFormat.ENCODING_PCM_8BIT] or [android.media.AudioFormat.ENCODING_PCM_16BIT]
 *
 * @return 含有头部信息的空音频数组
 */
private class WavHeaderWriter(
    private val audioLength: Int,
    private val numChannels: Int,
    private val sampleRate: Int,
    private val audioFormat: Int
) {
    private val bytes = mutableListOf<Byte>()

    init {
        val bitsPerSample = when (audioFormat) {
            AudioFormat.ENCODING_PCM_16BIT -> {
                16
            }

            AudioFormat.ENCODING_PCM_8BIT -> {
                8
            }

            else -> {
                16
            }
        }

        /**
         * the "RIFF" chunk descriptor
         * the format of concern here is "WAVE", which requires two sub-chunks: "fmt" and "data"
         */
        // chunk id: (4)
        write("RIFF")
        /**
         * chunk size: (4)
        36 + SubChunk2Size, or more precisely:
        4 + (8 + SubChunk1Size) + (8 + SubChunk2Size)
        This is the size of the rest of the chunk
        following this number.  This is the size of the
        entire file in bytes minus 8 bytes for the
        two fields not included in this count:
        ChunkID and ChunkSize.
         */
        write(audioLength + 36)
        // format: (4)
        write("WAVE")

        /**
         * the "fmt" sub-chunk
         * describes the format of the sound information in the data sub-chunk
         */
        // sub-chunk1 id: (4)
        write("fmt ")
        // sub-chunk1 size: (4)
        write(16)
        // audio format: (2)
        write(1.toShort())
        // num channels: (2)
        write(numChannels.toShort())
        // sample rate: (4)
        write(sampleRate)
        // byte rate: (4) [sampleRate] * [numChannels] * [bitsPerSample] / 8
        val byteRate: Int = sampleRate * numChannels * bitsPerSample / 8
        write(byteRate)
        // block align: (2) [numChannels] * [bitsPerSample] / 8
        write((numChannels * bitsPerSample / 8).toShort())
        // bits per sample: (2)
        write(bitsPerSample.toShort())

        /**
         * the "data" sub-chunk
         * indicates the size of the sound information and contains the raw sound data
         */
        // sub-chunk2 id: (4)
        write("data")
        // sub-chunk2 size
        write(audioLength)
    }

    fun toBytes() = bytes.toByteArray()

    private fun write(int: Int) {
        bytes.add((int and 0xFF).toByte())
        bytes.add((int shr 8 and 0xFF).toByte())
        bytes.add((int shr 16 and 0xFF).toByte())
        bytes.add((int shr 24 and 0xFF).toByte())
    }

    private fun write(short: Short) {
        val temp: Int = short.toInt()
        bytes.add((temp and 0xFF).toByte())
        bytes.add((temp shr 8 and 0xFF).toByte())
    }

    private fun write(char: Char) {
        bytes.add(char.code.toByte())
    }

    private fun write(string: String) {
        string.forEach { char ->
            write(char)
        }
    }
}

/**
 * @author yuansui
 * @since 2021/12/18
 */
object WavUtil {

    private const val HEADER_SIZE = 44

    /**
     * 生成wav格式的头部信息
     *
     * @param audioLength 音频长度
     * @param numChannels 通道数, Mono = 1, Stereo = 2, etc.
     * @param sampleRate 采样深度, 8000, 44100, etc.
     * @param audioFormat [android.media.AudioFormat.ENCODING_PCM_8BIT] or [android.media.AudioFormat.ENCODING_PCM_16BIT]
     */
    @JvmStatic
    fun createHeader(
        audioLength: Int,
        numChannels: Int,
        sampleRate: Int,
        audioFormat: Int
    ): ByteArray {
        return WavHeaderWriter(audioLength, numChannels, sampleRate, audioFormat).toBytes()
    }

    /**
     * 音频文件加入头部信息
     *
     * @param origBytes 原始音频流(不带头部信息)
     * @param numChannels 通道数
     * @param sampleRate 采样深度
     * @param audioFormat [android.media.AudioFormat.ENCODING_PCM_8BIT] or [android.media.AudioFormat.ENCODING_PCM_16BIT]
     *
     * @return 新的音频数组
     */
    @JvmStatic
    fun copyWithHeader(
        origBytes: ByteArray,
        numChannels: Int,
        sampleRate: Int,
        audioFormat: Int
    ): ByteArray {
        val origSize: Int = origBytes.size
        val bytes = ByteArray(origSize + HEADER_SIZE)

        // 复制头部信息到bytes里
        val header = createHeader(origSize - HEADER_SIZE, numChannels, sampleRate, audioFormat)
        header.copyInto(bytes, 0)

        // 将原始数据复制到bytes里
        origBytes.copyInto(bytes, HEADER_SIZE)

        return bytes
    }
}


fun ByteArray.copyWithWavHeader(
    numChannels: Int,
    sampleRate: Int,
    audioFormat: Int
): ByteArray {
    return WavUtil.copyWithHeader(this, numChannels, sampleRate, audioFormat)
}