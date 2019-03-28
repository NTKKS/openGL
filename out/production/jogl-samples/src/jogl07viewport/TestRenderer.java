package jogl07viewport;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * viewport, vice pohledu v okne 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;
	Texture texture;
	GLUquadric quadratic;

	int width, height, dx = 0, dy = 0;
	int ox, oy;
	long oldmils;
	
	double azimut = 180, zenit = 0;
	float uhel = 0;

	boolean per = true;
	boolean anim = true, flat = false, light = true;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		OglUtils.printOGLparameters(gl);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL); 
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);
		//orezani odvracenych ploch
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		
		quadratic = glu.gluNewQuadric(); 
		glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); 
		glu.gluQuadricTexture(quadratic, true); 
		
		System.out.println("Loading texture...");
		try {
			InputStream is = getClass().getResourceAsStream("/ea.gif"); // vzhledem k adresari res v projektu 
			texture = TextureIO.newTexture(is, true, "gif");
		} catch (IOException e) {
			System.err.println("Chyba cteni souboru s texturou");
		}

		float[] mat_dif = new float[] {0,0,0,1};// nastaveni materialu
		float[] mat_spec = new float[] {0.2f,0.2f,0.2f,1};// nastaveni materialu
		float[] mat_amb = new float[] {0.1f,0.1f,0.1f,1};// nastaveni materialu
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_amb, 0); 
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0); 
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_spec, 0); 
		
		float[] light_amb = new float[] {1,1,1,1};// nastaveni ambientni slozky
		float[] light_dif = new float[] {1,1,1,1};// nastaveni difusni slozky
		float[] light_spec = new float[] {1,1,1,1};// nastaveni zrcadlove slozky
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_amb,0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_dif,0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_spec,0);
	}

	public void displayOneView(GL2 gl) {
		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glColor3f(0f, 0f, 0f);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		//nastaveni pozice svetla
		float[] light_position=  new float[] {1,1,1,0.0f};//smer
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position,0);
		
		gl.glPushMatrix();
		gl.glRotatef(uhel, 0, 0, 1);
		gl.glRotatef(180, 0, 1, 0);
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_ADD);
		//gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		//gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER,
				GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER,
				GL2.GL_LINEAR_MIPMAP_NEAREST);
		if (light) gl.glEnable(GL2.GL_LIGHTING);
		
		glu.gluSphere(quadratic, 10f, 64, 64);// Zemekoule
		gl.glDisable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_LIGHTING);
		
		gl.glPushMatrix();
		gl.glTranslatef(11f, 0f, 0f);
		gl.glColor3f(1f, 0f, 0f);
		glut.glutSolidSphere(0.5, 10, 10);// geostacionarni
		gl.glPopMatrix();

		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glColor3f(1f, 0f, 1f);
		gl.glRotatef(uhel * 5, 0, 1, 1);
		gl.glTranslatef(11f, 0f, 0f);
		glut.glutSolidSphere(0.5, 10, 10);// nezavisla
		gl.glPopMatrix();

		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glPopMatrix();
	}

	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		long mils = System.currentTimeMillis();
		float speed = 60; // pocet stupnu rotace za vterinu
		// krok za jedno prekresleni (frame)
		float step = speed * (mils - oldmils) / 1000.0f; 
		oldmils = mils;

		if (anim)
			uhel = (uhel+step)%(3600);
		
		zenit += dy;
		dy = 0;
		if (zenit > 90)
			zenit = 90;
		if (zenit < -90)
			zenit = -90;
		azimut += dx;
		dx = 0;
		azimut = azimut % 360;

		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 200.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 200.0f);

		
		
			
		gl.glEnable(GL2.GL_LIGHT0);
		if (flat)
			gl.glShadeModel(GL2.GL_FLAT);
		else
			gl.glShadeModel(GL2.GL_SMOOTH);
		
		
		// Pohled kamerou
		gl.glViewport(width / 2, 0, width / 2, height / 2);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslatef(0f, 0f, -50f);
		gl.glRotated(zenit - 90, 1, 0, 0);
		gl.glRotated(azimut, 0, 0, 1);
		
		displayOneView(gl);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();

		// Narys
		gl.glViewport(0, height / 2, width / 2, height / 2);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);
		displayOneView(gl);

		// pudorys
		gl.glViewport(0, 0, width / 2, height / 2);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0, 0, 50, 0, 0, 0, -1, 0, 0);
		displayOneView(gl);

		// bokorys
		gl.glViewport(width / 2, height / 2, width / 2, height / 2);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		glu.gluLookAt(0, 50, 0, 0, 0, 0, 0, 0, 1);
		displayOneView(gl);

		gl.glViewport(0, 0, width, height);
		gl.glColor3f(1f, 1f, 1f);

		String text = new String(this.getClass().getName() + ": [lmb] camera");
		if (per)
			text = new String(text + ", [P]ersp ");
		else
			text = new String(text + ", [p]ersp ");

		if (flat)
			text = new String(text + ", [F]lat ");
		else
			text = new String(text + ", [f]lat ");

		if (light)
			text = new String(text + ", [L]ight ");
		else
			text = new String(text + ", [l]ight ");

		if (anim)
			text = new String(text + ", [A]nimation");
		else
			text = new String(text + ", [a]nimation");

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
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
	public void keyPressed(KeyEvent e) {
		// switch (e.getKeyCode()) {
		// }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_P:
			per = !per;
			break;
		case KeyEvent.VK_F:
			flat = !flat;
			break;
		case KeyEvent.VK_L:
			light = !light;
			break;
		case KeyEvent.VK_A:
			anim = !anim;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void dispose(GLAutoDrawable arg0) 
	{
	}
}