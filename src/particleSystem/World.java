package particleSystem;

import transforms.Vec3D;

public class World {

    private final Vec3D GRAVITY = new Vec3D(0,0,-0.003);

    public World() {
    }

    public Vec3D getGRAVITY() {
        return GRAVITY;
    }

}
