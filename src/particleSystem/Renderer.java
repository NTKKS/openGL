package particleSystem;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL2ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import transforms.Point3D;
import transforms.Vec3D;
import utils.OglUtils;

public class Renderer implements GLEventListener, MouseListener, MouseMotionListener, KeyListener {

    GLUT glut;
    GLU glu;

    long oldmils;
    long oldFPSmils;
    double	fps;

    float m[] = new float[16];
    boolean per = true;
    int width, height, dx, dy, ox, oy;

    float uhel = 0;

    Emitter emitter;
    Particle particle;
    float time = 0;
    int sec = 0;


    @Override
    public void init(GLAutoDrawable glDrawable) {
        GL2 gl = glDrawable.getGL().getGL2();

        System.out.println("Init GL is " + gl.getClass().getName());
        System.out.println("GL_VENDOR " + gl.glGetString(GL2.GL_VENDOR)); // vyrobce
        System.out.println("GL_RENDERER " + gl.glGetString(GL2.GL_RENDERER)); // graficka karta
        System.out.println("GL_VERSION " + gl.glGetString(GL2.GL_VERSION)); // verze OpenGL
        System.out.println("GL_EXTENSIONS " + gl.glGetString(GL2.GL_EXTENSIONS)); // implementovana rozsireni

        glut = new GLUT();
        glu = new GLU();

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

        emitter = new Emitter(new Point3D(0,0,0),new Vec3D(0,0,1),5,5, 4);
        particle = new Particle(emitter.getPosition(),emitter.getSpeed(),"Point",1);

    }

    @Override
    public void display(GLAutoDrawable glDrawable) {

        GL2 gl = glDrawable.getGL().getGL2();

        // vypocet fps, nastaveni rychlosti otaceni podle rychlosti prekresleni
        long mils = System.currentTimeMillis();
        if ((mils - oldFPSmils)>300){
            fps = 1000 / (double)(mils - oldmils + 1);
            oldFPSmils=mils;
        }
        //System.out.println(fps);
        float speed = 5; // pocet stupnu rotace za vterinu
        float step = speed * (mils - oldmils) / 1000.0f; // krok za jedno
        // prekresleni
        // (frame)
        oldmils = mils;

        time += fps*0.001;
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

        /*
        // nastavení pohybu particlu
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        uhel = (uhel + step)% 20;
        System.out.println(uhel);
        gl.glTranslatef(0.0f,0.0f,uhel);
        */

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
        glu.gluLookAt(50, 25, 20, 0, 0, 0, 0, 0, 1);


        // GEOMETRY ............................................

        // emitter
        gl.glColor3f(1.0f,1.0f,0.0f);
        gl.glBegin(GL2.GL_LINE_LOOP);
        float sz = emitter.getSize()/2;
        Point3D pos = emitter.getPosition();
        gl.glVertex3f(-1.0f*sz+(float) pos.getX(), 1.0f*sz+(float)pos.getY(), 0.0f);
        gl.glVertex3f(-1.0f*sz+(float) pos.getX(), -1.0f*sz+(float)pos.getY(), 0.0f);
        gl.glVertex3f(1.0f*sz+(float) pos.getX(), -1.0f*sz+(float)pos.getY(), 0.0f);
        gl.glVertex3f(1.0f*sz+(float) pos.getX(), 1.0f*sz+(float)pos.getY(), 0.0f);
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

        // DUMMY

        Point3D ptPos = particle.getPosition();

        gl.glColor3f(1.0f,0.0f,0.0f);
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3f(-2.5f, 2.5f, (float)ptPos.getZ());
        gl.glVertex3f(-2.5f, -2.5f, (float)ptPos.getZ());
        gl.glVertex3f(2.5f, -2.5f, (float)ptPos.getZ());
        gl.glVertex3f(2.5f, 2.5f, (float)ptPos.getZ());
        gl.glEnd();

        particle.setPosition(new Point3D(ptPos.getX(),ptPos.getY(),ptPos.getZ()+0.2));
        particle.setAge(particle.getAge()+fps*0.001);
        if (particle.getAge()>emitter.getParticleDie()){
            particle.setAge(0);
            particle.setPosition(emitter.getPosition());
        }

        if (sec!=(int)time&&emitter.getCount()>0){
            System.out.println(emitter.getCount());
            emitter.setCount(emitter.getCount()-1);
        }

        gl.glColor3f(1.0f,1.0f,0.0f);
        gl.glPointSize(5.0f);
        gl.glBegin(GL2.GL_POINTS);
        gl.glVertex3f(3.0f, 3.0f,(float)ptPos.getZ());
        gl.glEnd();

        sec = (int)time;

        //.........................................................
    }

    @Override
    public void reshape(GLAutoDrawable glDrawable, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        glDrawable.getGL().getGL2().glViewport(0, 0, width , height);
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
}
