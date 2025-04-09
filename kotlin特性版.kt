interface Lock {
    fun lock()
    fun unlock()
}

interface Door {
    fun open()
    fun close()
}

interface LockableDoor : Door, Lock

fun Door(): Door {
    return DoorImpl()
}

fun LockableDoor(): LockableDoor {
    return LockableDoorImpl()
}

private class DoorImpl : Door {
    override fun open() {
        println("Door is opened")
    }

    override fun close() {
        println("Door is closed")
    }
}

private class LockableDoorImpl : LockableDoor {
    override fun open() {
        println("Door is opened")
    }

    override fun close() {
        println("Door is closed")
    }

    override fun lock() {
        println("Door is locked")
    }

    override fun unlock() {
        println("Door is unlocked")
    }
}

fun <T : Door> T.openDoor2() {
    this.open()
}

fun <T> T.openDoor3() where T : Door, T : Lock {
    this.open()
    this.lock()
}

class Test {
    init {
        val lockDoor = LockableDoor()
        lockDoor.openDoor2()
        lockDoor.openDoor3()

        val door = Door()
        door.openDoor2()
    }
}

