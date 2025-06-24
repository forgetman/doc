package vector.ext

import android.os.Process
import kotlin.system.exitProcess

fun killProcess(pid: Int = Process.myPid()) = Process.killProcess(pid)

fun killAndExitProcess(pid: Int = Process.myPid(), status: Int = 1) {
    killProcess(pid)
    exitProcess(status)
}