package bluetoothle.peripheral.server

import android.bluetooth.BluetoothGattService
import android.content.Context
import bluetoothle.peripheral.server.internal.Characteristic
import bluetoothle.peripheral.server.internal.GattService
import no.nordicsemi.android.ble.annotation.CharacteristicPermissions
import no.nordicsemi.android.ble.annotation.CharacteristicProperties
import java.util.*

@DslMarker
private annotation class ServiceDsl

@DslMarker
private annotation class CharacteristicDsl

@ServiceDsl
class ServerBuilder {
    fun interface OnOpenSuccessListener {
        fun onSuccess()
    }

    fun interface OnOpenFailureListener {
        fun onFailure()
    }

    private val services = mutableListOf<GattService>()
    private var onOpenSuccessListener: OnOpenSuccessListener? = null
    private var onOpenFailureListener: OnOpenFailureListener? = null

    @ServiceDsl
    @CharacteristicDsl
    class ServiceBuilder {
        private val characteristics = mutableListOf<Characteristic>()

        @CharacteristicDsl
        class CharacteristicBuilder {
            var uuid: UUID? = null

            @CharacteristicProperties
            var properties: Int = 0

            @CharacteristicPermissions
            var permissions: Int = 0

            internal fun build(): Characteristic {
                return Characteristic(uuid ?: UUID.randomUUID(), properties, permissions)
            }
        }

        var uuid: UUID? = null

        fun characteristic(action: CharacteristicBuilder.() -> Unit) {
            val b = CharacteristicBuilder()
            action(b)
            characteristics.add(b.build())
        }

        internal fun build(): GattService {
            return GattService(
                uuid ?: UUID.randomUUID(),
                BluetoothGattService.SERVICE_TYPE_PRIMARY,
                characteristics
            )
        }
    }

    fun service(action: ServiceBuilder.() -> Unit) {
        val b = ServiceBuilder()
        action(b)
        services.add(b.build())
    }

    fun onOpenSuccess(listener: OnOpenSuccessListener) {
        onOpenSuccessListener = listener
    }

    fun onOpenFailure(listener: OnOpenFailureListener) {
        onOpenFailureListener = listener
    }

    internal fun build(context: Context): CharacteristicServer {
        return CharacteristicServer(context, services, object : CharacteristicServer.OnServerOpenStateListener {
            override fun onSuccess() {
                onOpenSuccessListener?.onSuccess()
            }

            override fun onFailure() {
                onOpenFailureListener?.onFailure()
            }
        })
    }
}