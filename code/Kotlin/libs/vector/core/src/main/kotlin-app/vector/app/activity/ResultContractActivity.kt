package vector.app.activity

import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.coroutines.flow.MutableStateFlow
import vector.ext.intentFor
import vector.util.Launcher

internal class ResultContractActivity : AppCompatActivity() {

    companion object {
        private val contactFlow = MutableStateFlow<ContractWrapper?>(null)

        fun start(host: Any?, contract: ContractWrapper) {
            val context = when (host) {
                is Fragment -> host.requireContext()
                is AppCompatActivity -> host
                else -> return
            }
            contactFlow.value = contract
            Launcher.startActivity(context, context.intentFor<ResultContractActivity>())
        }
    }

    data class ContractWrapper(
        val intent: Any,
        val contract: ActivityResultContract<Any, Any>,
        val callback: ActivityResultCallback<Any>
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFormat(PixelFormat.TRANSPARENT)

        super.onCreate(savedInstanceState)

        window.setGravity(Gravity.START or Gravity.TOP)
        val attrs = window.attributes
        attrs.x = 0
        attrs.y = 0
        attrs.width = 1
        attrs.height = 1
        window.attributes = attrs

        val contract = contactFlow.value
        if (contract == null) {
            finish()
            return
        }
        registerForActivityResult(contract.contract) { result ->
            contract.callback.onActivityResult(result)
            contactFlow.value = null
            finish()
        }.launch(contract.intent)
    }
}