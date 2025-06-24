package compat.network.model.wifi

import compat.network.ext.iterateBitSet
import sugar.ext.self
import java.util.BitSet

/**
 * @author yuansui
 * @since 2023/7/10
 *
 * TODO: 缺少compat支持
 * @see [android.net.wifi.WifiNetworkSpecifier]
 * @see [android.net.wifi.WifiNetworkSuggestion]
 */
class WifiConfigurationCompat private constructor(
    val ssid: String,
    val preSharedKey: String?,
    val networkId: Int,
    val wepKeys: Array<String?>,
    val wepTxKeyIndex: Int = 0,
    val requirePMF: Boolean = false,
    val allowedKeyManagement: BitSet,
    val allowedAuthAlgorithms: BitSet,
    val allowedGroupCiphers: BitSet,
    val allowedPairwiseCiphers: BitSet,
    val allowedProtocols: BitSet,
    val status: Status,
) {

    enum class Status {
        CURRENT,
        DISABLED,
        ENABLED,
    }

    class Builder {
        private var ssid: String = ""
        private var shareKey: String? = null
        private var networkId: Int = 0
        private var wepKeys: Array<String?> = arrayOfNulls(4)
        private var wepTxKeyIndex: Int = 0
        private var requirePMF: Boolean = false
        private val allowedKeyManagement: BitSet = BitSet()
        private val allowedAuthAlgorithms: BitSet = BitSet()
        private val allowedGroupCiphers: BitSet = BitSet()
        private val allowedPairwiseCiphers: BitSet = BitSet()
        private val allowedProtocols: BitSet = BitSet()
        private var status: Status = Status.CURRENT


        fun setSsid(ssid: String) = self {
            this.ssid = ssid
        }

        fun setShareKey(shareKey: String?) = self {
            this.shareKey = shareKey
        }

        fun setNetworkId(networkId: Int) = self {
            this.networkId = networkId
        }

        fun setWepKeys(index: Int, wepKey: String) = self {
            wepKeys[index] = wepKey
        }

        internal fun setWepKeys(wepKeys: Array<String?>) = self {
            this.wepKeys = wepKeys
        }

        fun setWepTxKeyIndex(wepTxKeyIndex: Int) = self {
            this.wepTxKeyIndex = wepTxKeyIndex
        }

        fun setRequirePMF(requirePMF: Boolean) = self {
            this.requirePMF = requirePMF
        }

        fun setAllowedKeyManagement(bitIndex: Int) = self {
            allowedKeyManagement.set(bitIndex)
        }

        internal fun setAllowedKeyManagement(bitSet: BitSet) = self {
            bitSet.iterateBitSet {
                allowedKeyManagement.set(it)
            }
        }

        fun setAllowedAuthAlgorithms(bitIndex: Int) = self {
            allowedAuthAlgorithms.set(bitIndex)
        }

        internal fun setAllowedAuthAlgorithms(bitSet: BitSet) = self {
            bitSet.iterateBitSet {
                allowedAuthAlgorithms.set(it)
            }
        }

        fun setAllowedGroupCiphers(bitIndex: Int) = self {
            allowedGroupCiphers.set(bitIndex)
        }

        internal fun setAllowedGroupCiphers(bitSet: BitSet) = self {
            bitSet.iterateBitSet {
                allowedGroupCiphers.set(it)
            }
        }

        fun setAllowedPairwiseCiphers(bitIndex: Int) = self {
            allowedPairwiseCiphers.set(bitIndex)
        }

        internal fun setAllowedPairwiseCiphers(bitSet: BitSet) = self {
            bitSet.iterateBitSet {
                allowedPairwiseCiphers.set(it)
            }
        }

        fun setAllowedProtocols(bitIndex: Int) = self {
            allowedProtocols.set(bitIndex)
        }

        internal fun setAllowedProtocols(bitSet: BitSet) = self {
            bitSet.iterateBitSet {
                allowedProtocols.set(it)
            }
        }

        fun setStatus(status: Status) = self {
            this.status = status
        }

        fun build(): WifiConfigurationCompat {
            return WifiConfigurationCompat(
                ssid,
                shareKey,
                networkId,
                wepKeys,
                wepTxKeyIndex,
                requirePMF,
                allowedKeyManagement,
                allowedAuthAlgorithms,
                allowedGroupCiphers,
                allowedPairwiseCiphers,
                allowedProtocols,
                status
            )
        }
    }

    fun newBuilder(): Builder {
        return Builder().setSsid(ssid)
            .setShareKey(preSharedKey)
            .setNetworkId(networkId)
            .setWepKeys(wepKeys)
            .setWepTxKeyIndex(wepTxKeyIndex)
            .setRequirePMF(requirePMF)
            .setAllowedAuthAlgorithms(allowedAuthAlgorithms)
            .setAllowedKeyManagement(allowedKeyManagement)
            .setAllowedGroupCiphers(allowedGroupCiphers)
            .setAllowedPairwiseCiphers(allowedPairwiseCiphers)
            .setAllowedProtocols(allowedProtocols)
    }

    override fun toString(): String {
        return "WifiConfigurationCompat(ssid='$ssid', preSharedKey='$preSharedKey', networkId='$networkId', requirePMF=$requirePMF, allowedKeyManagement=$allowedKeyManagement, allowedAuthAlgorithms=$allowedAuthAlgorithms, allowedGroupCiphers=$allowedGroupCiphers, allowedPairwiseCiphers=$allowedPairwiseCiphers, allowedProtocols=$allowedProtocols)"
    }
}
