package particleSystem;

import com.jogamp.opengl.GL2;
import transforms.Point3D;
import transforms.Vec3D;

public class Emitter {

    private Point3D position;
    private Vec3D speed;
    private int count;
    private float size;
    private int particleDie;
    private String shape;

    public Emitter(Point3D position, Vec3D speed, int count, float size, int particleDie) {
        this.position = position;
        this.speed = speed;
        this.count = count;
        this.size = size;
        this.particleDie = particleDie;
    }

    public Emitter() {
        position = new Point3D(0,0,0);
        speed = new Vec3D(0,0,0.5);
        count = 20;
        size = 5;
        particleDie = 2;
        shape = "Point";
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Vec3D getSpeed() {
        return speed;
    }

    public float getSize() {
        return size;
    }

    public int getCount() {
        return count;
    }

    public int getParticleDie() {
        return particleDie;
    }

    public void setSpeed(Vec3D speed) {
        this.speed = speed;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void setParticleDie(int particleDie) {
        this.particleDie = particleDie;
    }

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

}
