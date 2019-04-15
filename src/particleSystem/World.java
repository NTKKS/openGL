package particleSystem;

import transforms.Point3D;
import transforms.Vec3D;

import java.util.Random;

public class World {

    private final Vec3D GRAVITY = new Vec3D(0, 0, -0.003);
    private Vec3D centralForce;
    private Emitter emitter;
    private Vec3D wind;

    private Random rand;

    public World(Emitter emitter) {
        this.emitter = emitter;
        wind = getWind();
    }

    public Vec3D getGRAVITY() {
        return GRAVITY;
    }

    public Vec3D getCentralForce(Particle p, float mulCF) {
        Point3D pos = p.getPosition();
        float x;
        if (pos.getX() > emitter.getPosition().getX() + 2.5f && pos.getZ() > 1) {
            x = -0.005f*mulCF;
        } else if (pos.getX() < emitter.getPosition().getX() - 2.5f && pos.getZ() > 1) {
            x = 0.005f*mulCF;
        } else {
            x = 0;
        }
        float y;
        if (pos.getY() > emitter.getPosition().getY() + 2.5f && pos.getZ() > 1) {
            y = -0.005f*mulCF;
        } else if (pos.getY() < emitter.getPosition().getY() - 2.5f && pos.getZ() > 1) {
            y = 0.005f*mulCF;
        } else {
            y = 0;
        }
        centralForce = new Vec3D(x, y, 0);
        return centralForce;
    }

    public Vec3D getWind() {
        rand = new Random();
        float x = (rand.nextFloat()*0.02f)-0.01f;
        rand = new Random();
        float y = (rand.nextFloat()*0.02f)-0.01f;
        wind = new Vec3D(x,y,0);
        return wind;
    }

    public void setWind(Vec3D wind) {
        this.wind = wind;
    }
}
