package jogl08camerasky;

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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import utils.OglUtils;


/**
 * trida pro zobrazeni sceny v OpenGL:
 * kamera, skybox 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;

	int width, height, dx, dy, x, y;
	int ox, oy;

	float zenit;
	float azimut;
	double ex, ey, ez, px, py, pz, cenx, ceny, cenz, ux, uy, uz;
	float step, rot = 0, trans = 0;
	boolean per = true, free = true, sky = false;
	double a_rad, z_rad;
	long oldmils = System.currentTimeMillis();

	File file;
	Texture texture;
	float m[] = new float[16];
	float m1[] = new float[16];

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL); 
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
				
		OglUtils.printOGLparameters(gl);
		
		System.out.println("Loading texture...");
		InputStream is = getClass().getResourceAsStream("/11.jpg"); // vzhledem k adresari res v projektu 
		if (is == null)
			System.out.println("File not found");
		else
			try {
				texture = TextureIO.newTexture(is, true, "jpg");
			} catch (IOException e) {
				System.err.println("Chyba cteni souboru s texturou");
			}

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MAG_FILTER,GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR_MIPMAP_LINEAR);

	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		long mils = System.currentTimeMillis();
		step = (mils - oldmils) / 1000.0f;
		//float fps = 1000 / (float) (mils - oldmils);
		oldmils = mils;
		trans = 50 * step;
		rot += 360 * step / 10f;

		//System.out.println(fps);

		// vymazani obrazovky a Z-bufferu
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 5000.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 5000.0f);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		//nenulujeme pohledovou matici, bude se nacitat do m1
		
		//transformace pro sky box
		gl.glPushMatrix();
		if (free) {
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m1, 0);
			gl.glLoadIdentity();
			gl.glRotatef(-zenit, 1.0f, 0, 0);
			gl.glRotatef(azimut, 0, 1.0f, 0);
			// nulujeme posunuti;
			m1[12]=0;
			m1[13]=0;
			m1[14]=0;
			gl.glMultMatrixf(m1, 0);

		} else {
			gl.glLoadIdentity();

			gl.glRotatef(-zenit, 1.0f, 0, 0);
			gl.glRotatef(azimut, 0, 1.0f, 0);
			
			//nebo  glu.gluLookAt(0, 0, 0, ex, ey, ez, ux, uy, uz);

		}
		// typ sky boxu
		if (sky) skyBox1(gl);
		else skyBox2(gl);
		
		gl.glPopMatrix();
		
		// transformace sceny
		if (free) {
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
			gl.glLoadIdentity();

			gl.glRotatef(-zenit, 1.0f, 0, 0);
			gl.glRotatef(azimut, 0, 1.0f, 0);
			gl.glTranslated(-px, -py, -pz);
			gl.glMultMatrixf(m, 0);

			zenit = 0;
			azimut = 0;
			px = 0;
			py = 0;
			pz = 0;
		} else {
			gl.glLoadIdentity();

			/*
			 gl.glRotatef(-zenit,1.0f,0,0); 
			 gl.glRotatef(azimut,0,1.0f,0);
			 gl.glTranslated(-px,-py,-pz);
			 nebo
			 */
			glu.gluLookAt(px, py, pz, ex + px, ey + py, ez + pz, ux, uy, uz);

		}

		gl.glPushMatrix();

		// objekty sceny
		gl.glPushMatrix();
		gl.glRotatef(rot, 0, 0, 0.1f);
		gl.glTranslated(10, 0, 0);
		gl.glColor3d(0.5, 1, 0.5);
		glut.glutSolidSphere(5, 10, 10);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(10, 0, 0);
		gl.glColor3d(1, 0, 0);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(10, 10, 0);
		gl.glColor3d(1, 1, 0);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(0, 10, 0);
		gl.glColor3d(0, 1, 0);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(10, 0, 10);
		gl.glColor3d(1, 0, 1);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(10, 10, 10);
		gl.glColor3d(1, 1, 1);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(0, 10, 10);
		gl.glColor3d(0, 1, 1);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(0, 0, 10);
		gl.glColor3d(0, 0, 1);
		glut.glutSolidCube(1);
		gl.glPopMatrix();

		gl.glPopMatrix();

		gl.glColor3f(1f, 1f, 1f);

		String text = new String(this.getClass().getName() + ": [WSAD][lmb] camera");
		if (per)
			text = new String(text + ", [P]ersp ");
		else
			text = new String(text + ", [p]ersp ");

		if (free)
			text = new String(text + ", [F]ree move ");
		else
			text = new String(text + ", [f]ree move");

		if (sky)
			text = new String(text + ", s[k]y box");
		else
			text = new String(text + ", s[k]y sphere");

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
	}

	public void skyBox1(GL2 gl) {
		gl.glColor3d(0.5, 0.5, 0.5);
		glut.glutWireCube(200); //neni nutne, pouze pro znazorneni tvaru skyboxu
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		texture.enable(gl);
		texture.bind(gl);

		gl.glBegin(GL2.GL_QUADS);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(-2500, -2500, -2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(-2500, 2500, -2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(-2500, 2500, 2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(-2500, -2500, 2500);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(2500, -2500, -2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(2500, 2500, -2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(2500, 2500, 2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(2500, -2500, 2500);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(-2500, -2500, -2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(2500, -2500, -2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(2500, -2500, 2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(-2500, -2500, 2500);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(-2500, 2500, -2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(2500, 2500, -2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(2500, 2500, 2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(-2500, 2500, 2500);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(-2500, 2500, -2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(-2500, -2500, -2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(2500, -2500, -2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(2500, 2500, -2500);

		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex3d(-2500, 2500, 2500);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3d(-2500, -2500, 2500);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex3d(2500, -2500, 2500);
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex3d(2500, 2500, 2500);
		gl.glEnd();

		gl.glDisable(GL2.GL_TEXTURE_2D);

	}

	public void skyBox2(GL2 gl) {
		gl.glColor3d(0.5, 0.5, 0.5);
		glut.glutWireSphere(200, 10, 10); //neni nutne, pouze pro znazorneni tvaru skyboxu
		
		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);

		texture.enable(gl);
		texture.bind(gl);

		GLUquadric quadric = glu.gluNewQuadric();
		glu.gluQuadricTexture(quadric, true);
		glu.gluQuadricNormals(quadric, GLU.GLU_SMOOTH);
		glu.gluSphere(quadric, 1000f, 20, 20);
		glu.gluDeleteQuadric(quadric);

		gl.glDisable(GL2.GL_TEXTURE_2D);
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
		if (e.getButton() == MouseEvent.BUTTON1) {
		}
		ox = e.getX();
		oy = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		dx = e.getX() - ox;
		dy = e.getY() - oy;
		ox = e.getX();
		oy = e.getY();

		zenit += dy;
		if (zenit > 90)
			zenit = 90;
		if (zenit <= -90)
			zenit = -90;
		azimut += dx;
		azimut = azimut % 360;
		a_rad = azimut * Math.PI / 180;
		z_rad = zenit * Math.PI / 180;
		ex = Math.sin(a_rad) * Math.cos(z_rad);
		ey = Math.sin(z_rad);
		ez = -Math.cos(a_rad) * Math.cos(z_rad);
		ux = Math.sin(a_rad) * Math.cos(z_rad + Math.PI / 2);
		uy = Math.sin(z_rad + Math.PI / 2);
		uz = -Math.cos(a_rad) * Math.cos(z_rad + Math.PI / 2);

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {

			if (free) {
				pz -= trans;
			} else {
				px += ex * trans;
				py += ey * trans;
				pz += ez * trans;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			if (free) {
				pz += trans;
			} else {
				px -= ex * trans;
				py -= ey * trans;
				pz -= ez * trans;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			if (free) {
				px -= trans;
			} else {

				pz -= Math.cos(a_rad - Math.PI / 2) * trans;
				px += Math.sin(a_rad - Math.PI / 2) * trans;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			if (free) {
				px += trans;
			} else {
				pz += Math.cos(a_rad - Math.PI / 2) * trans;
				px -= Math.sin(a_rad - Math.PI / 2) * trans;
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_F:
			free = !free;
			break;
		case KeyEvent.VK_P:
			per = !per;
			break;
		case KeyEvent.VK_K:
			sky = !sky;
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
	}
	
}