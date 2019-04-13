package particleSystem;

import com.jogamp.opengl.*;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import transforms.Point3D;
import transforms.Vec3D;
import utils.OglUtils;

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener, MouseWheelListener {

    GLUT glut;
    GLU glu;

    long oldmils;
    long oldFPSmils;
    double fps;
    float radius, pSize;

    float m[] = new float[16];
    boolean per = true;
    int width, height, dx, dy, ox, oy, zoom;

    float uhel = 0;

    Emitter emitter;
    List<Particle> particles;
    World world;
    Vec3D gravity;
    Vec3D wind;
    float time = 0;
    int sec = 0;

    Texture texture;


    @Override
    public void init(GLAutoDrawable glDrawable) {
        GL2 gl = glDrawable.getGL().getGL2();
        glut = new GLUT();
        glu = new GLU();

        System.out.println("Init GL is " + gl.getClass().getName());
        System.out.println("GL_VENDOR " + gl.glGetString(GL2.GL_VENDOR)); // vyrobce
        System.out.println("GL_RENDERER " + gl.glGetString(GL2.GL_RENDERER)); // graficka karta
        System.out.println("GL_VERSION " + gl.glGetString(GL2.GL_VERSION)); // verze OpenGL
        System.out.println("GL_EXTENSIONS " + gl.glGetString(GL2.GL_EXTENSIONS)); // implementovana rozsireni

        OglUtils.printOGLparameters(gl);

        gl.glEnable(GL2.GL_DEPTH_TEST);

        gl.glFrontFace(GL2.GL_CCW);
        gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
        gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
        gl.glDisable(GL2.GL_CULL_FACE);
        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glLoadIdentity();
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);


        //emitter = new Emitter(new Point3D(0,0,0),new Vec3D(0,0,1),5,5, 4);
        emitter = new Emitter();
        pSize = (int) emitter.getSize();
        world = new World(emitter);
        gravity = world.getGRAVITY();
        wind = world.getWind();
        radius = emitter.getSize();
        particles = new ArrayList<>();
        //particle = new Particle(emitter.getPosition(),emitter.getSpeed(),"Point",1);

        System.out.println("Loading texture...");
        InputStream is = getClass().getResourceAsStream("/flame_grad.jpg"); // vzhledem k adresari res v projektu
        if (is == null)
            System.out.println("File not found");
        else
            try {
                texture = TextureIO.newTexture(is, true, "jpg");
            } catch (GLException | IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void display(GLAutoDrawable glDrawable) {

        GL2 gl = glDrawable.getGL().getGL2();


        // vypocet fps, nastaveni rychlosti otaceni podle rychlosti prekresleni
        long mils = System.currentTimeMillis();
        if ((mils - oldFPSmils) > 300) {
            fps = 1000 / (double) (mils - oldmils + 1);
            oldFPSmils = mils;
        }
        //System.out.println(fps);
        float speed = 5; // pocet stupnu rotace za vterinu
        float step = speed * (mils - oldmils) / 1000.0f; // krok za jedno
        // prekresleni
        // (frame)
        oldmils = mils;

        time += fps * 0.005;
        //System.out.println(time);

        gl.glEnable(gl.GL_DEPTH_TEST);

        // nulujeme misto pro kresleni
        gl.glClearColor(0f, 0f, 0f, 1f);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // nastaveni modelovaci transformace
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); // inicializace na jednotkovou matici

        gl.glRotatef(dx, 0, 0, 1);
        gl.glRotatef(dy, 0, 1, 0);
        gl.glMultMatrixf(m, 0);
        gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
        dx = 0;
        dy = 0;

        // zoom setup
        if (zoom == -1) {
            gl.glLoadIdentity();
            gl.glScalef(0.9f, 0.9f, 0.9f);
            gl.glMultMatrixf(m, 0);
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
            pSize *= 0.9f;
            zoom = 0;
        } else if (zoom == 1) {
            gl.glLoadIdentity();
            gl.glScalef(1.1f, 1.1f, 1.1f);
            gl.glMultMatrixf(m, 0);
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
            pSize *= 1.1f;
            zoom = 0;
        }

        // nastaveni projekce
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); // inicializace na jednotkovou matici
        // nastaveni transformace zobrazovaciho objemu
        if (per)
            glu.gluPerspective(45, width / (float) height, 0.1f, 100.0f);
        else
            gl.glOrtho(-20 * width / (float) height, 20 * width
                    / (float) height, -20, 20, 0.1f, 100.0f);

        // pohledova transformace
        // divame se do sceny z kladne osy x, osa z je svisla
        glu.gluLookAt(50, 25, 20, 0, 0, 5, 0, 0, 1);

        // GEOMETRY ............................................

        // emitter
        gl.glColor3f(1.0f, 1.0f, 0.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
        float sz = emitter.getSize() / 2;
        Point3D pos = emitter.getPosition();
        gl.glVertex3f(-1.0f * sz + (float) pos.getX(), 1.0f * sz + (float) pos.getY(), 0.0f);
        gl.glVertex3f(-1.0f * sz + (float) pos.getX(), -1.0f * sz + (float) pos.getY(), 0.0f);
        gl.glVertex3f(1.0f * sz + (float) pos.getX(), -1.0f * sz + (float) pos.getY(), 0.0f);
        gl.glVertex3f(1.0f * sz + (float) pos.getX(), 1.0f * sz + (float) pos.getY(), 0.0f);
        gl.glEnd();

        // XYZ axis
        gl.glBegin(GL2.GL_LINES);
        gl.glColor3f(1.0f, 0f, 0f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(50.0f, 0.0f, 0.0f);
        gl.glColor3f(0f, 1f, 0f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(0.0f, 50.0f, 0.0f);
        gl.glColor3f(0f, 0f, 1f);
        gl.glVertex3f(0f, 0f, 0f);
        gl.glVertex3f(0.0f, 0.0f, 50.0f);
        gl.glEnd();

        // PARTICLES

        //create particle every second
        if (sec != (int) time && particles.size() < emitter.getCount()) {
            for (int i = 0; i <= 10; i++) {
                addParticle();
            }
            //System.out.println(particles.size());
        }

        if ((int) time % 5 == sec % 5) {
            wind = world.getWind();
        }

        //for every particle do
        for (int i = 0; i < particles.size(); i++) {

            //draw particle
            Particle p = particles.get(i);

            //Texture setup
            gl.glEnable(GL2.GL_TEXTURE_2D);
            gl.glDisable(GL2.GL_LIGHTING);
            gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
            gl.glTexCoord2f((float) (p.getAge() / p.getPtDie()), 1.0f);

            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
            gl.glDisable(GL.GL_DEPTH_TEST);
            //.................................

            //gl.glColor3d(p.getColor(texture).getR(),p.getColor(texture).getG(),p.getColor(texture).getB());
            gl.glPointSize(pSize);
            //gl.glPointSize(20);
            gl.glEnable(GL2.GL_POINT_SMOOTH);
            gl.glBegin(GL2.GL_POINTS);
            Point3D pPos = p.getPosition();
            gl.glVertex3f((float) pPos.getX(), (float) pPos.getY(), (float) pPos.getZ());
            gl.glEnd();
            //change speed, apply gravity, central force
            Vec3D newSpeed = p.getSpeed().add(wind).add(world.getCentralForce(p));
            if (p.getPosition().getZ() > 0) {
                newSpeed.add(gravity);
            }
            p.setSpeed(newSpeed);
            Point3D actual = p.getPosition();
            p.setPosition(new Point3D(actual.getX() + newSpeed.getX(), actual.getY() + newSpeed.getY(), actual.getZ() + newSpeed.getZ()));

            //particle die
            p.setAge(p.getAge() + fps * 0.001);
            if (p.getAge() > p.getPtDie()) {
                p.setAge(0);
                p.setPtDie(emitter.getRndPDie());
                p.setPosition(emitter.getRndPos((int) radius));
                p.setSpeed(emitter.getRndSpeed());
            }

            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glDisable(GL.GL_BLEND);
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDisable(GL2.GL_TEXTURE_2D);
        }

        sec = (int) time;

        //.........................................................

        //draw statistics
        String text = new String(this.getClass().getName() + ": [Mouse] [M]ode: "
                + " ");
        String fpsStat = new String("FPS: " + String.valueOf((int) fps));
        String ptStat = new String("pCount: " + String.valueOf(particles.size()));
        //OglUtils.drawStr2D(glDrawable, 3, height-20, text);
        OglUtils.drawStr2D(glDrawable, width - 50, 10, fpsStat);
        OglUtils.drawStr2D(glDrawable, width - 130, 10, ptStat);
    }

    private void addParticle() {
        particles.add(new Particle(emitter.getRndPos((int) radius), emitter.getRndPDie(), emitter.getRndSpeed(), emitter.getShape(), emitter.getSize()));
    }

    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        glDrawable.getGL().getGL2().glViewport(0, 0, width, height);
    }

    @Override
    public void dispose(GLAutoDrawable glDrawable) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
        }
        ox = e.getX();
        oy = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
            dx = e.getX() - ox;
            dy = e.getY() - oy;
            ox = e.getX();
            oy = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getUnitsToScroll() > 0) zoom = 1;
        else zoom = -1;

    }
}
