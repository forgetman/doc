package logger

object L : Logger() {

    override val loggerClassName: String
        get() = Logger::class.java.name
}
