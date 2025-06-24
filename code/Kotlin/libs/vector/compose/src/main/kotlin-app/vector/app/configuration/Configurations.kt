package vector.app.configuration

object Configurations {

    lateinit var resolution: ResolutionConfig
        private set

    lateinit var ui: UiConfig

    interface Initializer {
        fun getResolution(): ResolutionConfig
        fun getUI(): UiConfig
    }

    internal fun apply(initializer: Initializer) {
        resolution = initializer.getResolution()
        ui = initializer.getUI()
    }
}