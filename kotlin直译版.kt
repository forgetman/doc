interface Lock {
    fun lock()
    fun unlock()
}

interface Door {
    fun open()
    fun close()
}

interface LockableDoor : Door, Lock

class DoorFactory {
    companion object {
        fun createDoor(): Door {
            return DoorImpl()
        }

        fun createLockableDoor(): LockableDoor {
            return LockableDoorImpl()
        }
    }
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

class DoorUtils {
    companion object {
        fun <T : Door> openDoor2(door: T) {
            door.open()
        }

        fun <T> openDoor3(door: T) where T : Door, T : Lock {
            door.open()
            door.lock()
        }
    }
}

class Test {
    init {
        val lockDoor = DoorFactory.createLockableDoor()
        DoorUtils.openDoor2(lockDoor)
        DoorUtils.openDoor3(lockDoor)

        val door = DoorFactory.createDoor()
        DoorUtils.openDoor2(door)
    }
}