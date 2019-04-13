package particleSystem;

import com.jogamp.opengl.util.texture.Texture;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

public class Particle {

    private Point3D position;
    private double age;
    private int pDie;
    private Vec3D speed;
    private String shape;
    private Col color;

    public Particle(Point3D position, int pDie, Vec3D speed, String shape, double size) {
        this.position = position;
        age = 0;
        this.pDie = pDie;
        this.speed = speed;
        this.shape = shape;
        color = new Col(1.0f,1.0f,0.0f,1.0f);
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

    public void setAge(double age) {
        this.age = age;
    }

    public void setSpeed(Vec3D speed) {
        this.speed = speed;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Col getColor(Texture tex) {
        Col flame = new Col();
        return color;
    }

    public int getPtDie() {
        return pDie;
    }

    public void setPtDie(int pDie) {
        this.pDie = pDie;
    }

    public Col getColor() {
        return color;
    }

    public void setColor(Col color) {
        this.color = color;
    }
}
