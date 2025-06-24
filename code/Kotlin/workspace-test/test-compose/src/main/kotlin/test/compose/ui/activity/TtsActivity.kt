package test.compose.ui.activity

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import feature.tts.TextToSpeech
import feature.tts.engine.azure.AzureTextToSpeechEngine
import feature.tts.engine.azure.ext.Azure_Xiaoxiao
import feature.tts.ext.speak
import feature.tts.model.Params
import test.compose.ext.AppBar
import test.compose.ui.FlowButton
import test.compose.ui.FlowContent
import vector.app.compose.ui.activity.SimpleComposeActivityEx

/**
 * @author yuansui
 * @since 2025/6/21
 */
class TtsActivity : SimpleComposeActivityEx() {

    private val azureTts = TextToSpeech(0, AzureTextToSpeechEngine(this))
    private val independenceTts = TextToSpeech(1, AzureTextToSpeechEngine(this), false)

    private var utteranceIdIndex = 0
    private var utteranceIdInde2 = 0

    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                AppBar(title = "TTS")
            }
        ) { innerPadding ->
            FlowContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                FlowButton("azure") {
                    val id = utteranceIdIndex++.toString()
                    azureTts.speak(
                        "微软, 今天天气真好, 序号$id",
                        Params.Azure_Xiaoxiao,
                        id
                    )
                }
                FlowButton("azure独立") {
                    val id = utteranceIdInde2++.toString()
                    independenceTts.speak(
                        "独立, 今天天气真好, 序号$id",
                        Params.Azure_Xiaoxiao,
                        id
                    )
                }
            }
        }
    }
}