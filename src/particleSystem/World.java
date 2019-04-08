package particleSystem;

import transforms.Vec3D;

public class World {

    private long timeStart;
    private long timeNow;
    private Vec3D gravity;

    public World() {
        timeStart = System.currentTimeMillis()/1000;
    }

    public long getTimeNow() {
        timeNow = (System.currentTimeMillis()-timeStart)/1000;
        return timeNow;
    }

    public long getTimeStart() {
        return timeStart;
    }
}
