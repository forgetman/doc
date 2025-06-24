package vector.app

import vector.CoreAppEx
import vector.app.configuration.Configurations
import vector.app.configuration.ResolutionConfig
import vector.app.configuration.UiConfig

abstract class ComposeAppEx : CoreAppEx() {

    override fun onCreate() {
        super.onCreate()

        Configurations.apply(configurations())
    }

    private fun configurations(): Configurations.Initializer {
        return object : Configurations.Initializer {
            override fun getResolution(): ResolutionConfig {
                return configResolution()
            }

            override fun getUI(): UiConfig {
                return configUi()
            }
        }
    }

    open fun configResolution(): ResolutionConfig = ResolutionConfig.build { }

    open fun configUi(): UiConfig = UiConfig.build { }
}