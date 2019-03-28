package jogl06pushpopclip;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;
//import com.jogamp.opengl.util.texture.Texture;
//import com.jogamp.opengl.util.texture.TextureIO;



import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * modelovani pomoci Push a Pop, orezani clipovou rovinou, zobrazovaci seznam 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;

	int width, height, dx = 0, dy = 0;
	int ox, oy;

	double azimut = 180, zenit = 0;

	boolean per = true, clip = true, anim = true;
	float alpha = 0;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_LINE);

		OglUtils.printOGLparameters(gl);
				
		//modelovani kola
		gl.glNewList(1, GL2.GL_COMPILE);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glRotatef(90, 0, 1, 0);
		gl.glColor3f(0.2f, 0.2f, 0f);
		glut.glutSolidTorus(4, 11, 20, 20);// pneu
		glut.glutSolidTorus(1, 2, 20, 20);// osa
		gl.glColor3f(0.8f, 0.6f, 0.4f);
		glut.glutSolidCylinder(8, 0.8, 20, 2);// disk
		gl.glColor3f(0.3f, 0.2f, 0.4f);
		for (int i = 0; i < 5; i++) // srouby
		{
			gl.glRotatef(75, 0, 0, 1);
			gl.glPushMatrix();
			gl.glTranslatef(3f, 3f, 0f);
			glut.glutSolidCylinder(1, 0.82, 6, 1);
			gl.glPopMatrix();
		}
		gl.glPopMatrix();
		gl.glEndList();
		
		//modelovani podvozku
		gl.glNewList(2, GL2.GL_COMPILE);
		gl.glPushMatrix();
		gl.glTranslatef(9f, 20f, 0f);
		gl.glCallList(1);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(9f, -20f, 0f);
		gl.glCallList(1);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(-9f, 20f, 0f);
		gl.glCallList(1);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(-9f, -20f, 0f);
		gl.glCallList(1);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glScalef(9f, 60f, 3f);
		glut.glutSolidCube(1);
		gl.glPopMatrix();
		gl.glEndList();

	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 300.0f);
		else
			gl.glOrtho(-40 * width / (float) height, 40 * width
					/ (float) height, -40, 40, 0.1f, 300.0f);

		double[] eqn = { 0, 1, 1, 0 };
		if (anim)
			alpha += 1;

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		zenit += dy;
		dy = 0;
		if (zenit > 90)
			zenit = 90;
		if (zenit < -90)
			zenit = -90;
		azimut += dx;
		dx = 0;
		azimut = azimut % 360;
		gl.glTranslatef(0f, 0f, -100f);
		gl.glRotated(zenit - 90, 1, 0, 0);
		gl.glRotated(azimut, 0, 0, 1);

		if (clip)
			gl.glEnable(GL2.GL_CLIP_PLANE0);
		else
			gl.glDisable(GL2.GL_CLIP_PLANE0);

		gl.glPushMatrix();
		gl.glRotated(alpha, 1, 1, 0);
		gl.glClipPlane(GL2.GL_CLIP_PLANE0, eqn, 0);
		gl.glPopMatrix();

		gl.glColor3f(0.2f, 0.9f, 0.9f);
		glut.glutWireCube(40);

		gl.glCallList(2);

		gl.glDisable(GL2.GL_CLIP_PLANE0);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		glut.glutWireCube(50);

		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		String text = new String(this.getClass().getName() + ": [lmb] move, ");
		if (per)
			text = new String(text + "[P]ersp ");
		else
			text = new String(text + "[p]ersp ");

		if (clip)
			text = new String(text + "[C]lip ");
		else
			text = new String(text + "[c]lip ");

		if (anim)
			text = new String(text + "[A]nimation");
		else
			text = new String(text + "[a]nimation");

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		GL2 gl = glDrawable.getGL().getGL2();
		this.width = width;
		this.height = height;
		gl.glViewport(0, 0, this.width, this.height);
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
		case KeyEvent.VK_A:
			anim = !anim;
			break;
		case KeyEvent.VK_C:
			clip = !clip;
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