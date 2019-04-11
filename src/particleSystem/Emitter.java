package particleSystem;

import transforms.Point3D;
import transforms.Vec3D;

import java.util.Random;

public class Emitter {

    private Point3D position;
    private Vec3D speed;
    private int count;
    private float size;
    private int particleDie;
    private String shape;

    private Random rand;

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
        count = 1000;
        size = 8;
        particleDie = 3;
        shape = "Point";
    }

    public Point3D getPosition() {
        return position;
    }

    public Point3D getRndPos(int radius){
        rand = new Random();
        float rndX = (rand.nextFloat()*radius)-radius/2;
        rand = new Random();
        float rndY = (rand.nextFloat()*radius)-radius/2;
        //System.out.println(new Point3D(getPosition().getX()+rndX,getPosition().getY()+rndY,getPosition().getZ()));
        return new Point3D(getPosition().getX()+rndX,getPosition().getY()+rndY,getPosition().getZ());
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public Vec3D getSpeed() {
        return speed;
    }

    public Vec3D getRndSpeed(){
        rand = new Random();
        float rndX = (rand.nextFloat()*0.1f)-0.05f;
        float rndY = (rand.nextFloat()*0.1f)-0.05f;
        float rndZ = (rand.nextFloat()*(-(float)getSpeed().getZ()))+(float) getSpeed().getZ();
        //System.out.println(new Vec3D(rndX,rndY,rndZ));
        return new Vec3D(rndX,rndY,rndZ);
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

    public int getRndPDie() {
        rand = new Random();
        int die = (int)(rand.nextFloat()*particleDie);
        return die;
    }
}
