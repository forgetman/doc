interface Lock {
    void lock();
    void unlock();
}

interface Door {
    void open();
    void close();
}

interface LockableDoor extends Door, Lock {}

class DoorFactory {
    public static Door createDoor() {
        return new DoorImpl();
    }

    public static LockableDoor createLockableDoor() {
        return new LockableDoorImpl();
    }
}

private static class DoorImpl implements Door {
    @Override
    public void open() {
        System.out.println("Door is opened");
    }

    @Override
    public void close() {
        System.out.println("Door is closed");
    }
}

private static class LockableDoorImpl implements LockableDoor {
    @Override
    public void open() {
        System.out.println("Door is opened");
    }

    @Override
    public void close() {
        System.out.println("Door is closed");
    }

    @Override
    public void lock() {
        System.out.println("Door is locked");
    }

    @Override
    public void unlock() {
        System.out.println("Door is unlocked");
    }
}

class DoorUtils {
    public static <T extends Door> void openDoor2(T door) {
        door.open();
    }

    public static <T extends Door & Lock> void openDoor3(T door) {
        door.open();
        door.lock();
    }
}

class Test {
    public Test() {
        LockableDoor lockDoor = DoorFactory.createLockableDoor();
        DoorUtils.openDoor2(lockDoor);
        DoorUtils.openDoor3(lockDoor);

        Door door = DoorFactory.createDoor();
        DoorUtils.openDoor2(door);
    }
}