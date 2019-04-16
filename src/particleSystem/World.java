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
        wind = new Vec3D(0, 0, 0);
    }

    public Vec3D getGRAVITY() {
        return GRAVITY;
    }

    public Vec3D getCentralForce(Particle p, float mulCF) {
        Point3D pos = p.getPosition();
        float x;
        if (pos.getX() > emitter.getPosition().getX() + 2.5f && pos.getZ() > 1) {
            x = -0.005f * mulCF;
        } else if (pos.getX() < emitter.getPosition().getX() - 2.5f && pos.getZ() > 1) {
            x = 0.005f * mulCF;
        } else {
            x = 0;
        }
        float y;
        if (pos.getY() > emitter.getPosition().getY() + 2.5f && pos.getZ() > 1) {
            y = -0.005f * mulCF;
        } else if (pos.getY() < emitter.getPosition().getY() - 2.5f && pos.getZ() > 1) {
            y = 0.005f * mulCF;
        } else {
            y = 0;
        }
        centralForce = new Vec3D(x, y, 0);
        return centralForce;
    }

    public Vec3D getWind(float mul) {
        rand = new Random();
        double x = (rand.nextDouble() * 0.01*mul) - 0.005*mul;
        rand = new Random();
        double y = (rand.nextDouble() * 0.01*mul) - 0.005*mul;
        double my = 0;
        double mx = 0;
        if (wind.getX() > 0) {
            mx = (float) Math.min(0.01*mul, wind.getX() + x);
        } else if (wind.getX() < 0) {
            mx = (float) Math.max(-0.01*mul, wind.getX() + x);
        } else if (wind.getY() > 0) {
            my = (float) Math.min(0.01*mul, wind.getY() + y);
        } else if (wind.getY() < 0) {
            my = (float) Math.max(-0.01*mul, wind.getY() + y);
        } else {
            mx = wind.getX()+x;
            my = wind.getY()+y;
        }
        wind = new Vec3D(mx, my, 0);
        return wind;
    }

    public void setWind(Vec3D wind) {
        this.wind = wind;
    }
}
