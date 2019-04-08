package particleSystem;

import transforms.Point3D;
import transforms.Vec3D;

public class Particle {

    private Point3D position;
    private double age;
    private Vec3D speed;
    private String shape;
    private double size;

    public Particle(Point3D position, Vec3D speed, String shape, double size) {
        this.position = position;
        age = 0;
        this.speed = speed;
        this.shape = shape;
        this.size = size;
    }

    public Point3D getPosition() {
        return position;
    }

    public double getAge() {
        return age;
    }

    public Vec3D getSpeed() {
        return speed;
    }

    public String getShape() {
        return shape;
    }

    public double getSize() {
        return size;
    }

    public void setAge(double age) {
        this.age = age;
    }

    public void setSpeed(Vec3D speed) {
        this.speed = speed;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }
}
