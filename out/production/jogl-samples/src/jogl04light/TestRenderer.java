package jogl04light;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL: osvetleni, material, pozice svetla,
 * osvetlovaci model
 * 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;
	TextRenderer renderer;

	int width, height, x, y;

	boolean per = true, flat = false, light = false;
	int wire = 0;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		OglUtils.printOGLparameters(gl);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		// nastaveni materialu - difusni slozka
		float[] mat_dif = new float[] { 0, 1, 1, 1 };
		// nastaveni materialu - zrcadlova slozka
		float[] mat_spec = new float[] { 0.3f, 0.0f, 0.0f, 1 };
		// nastaveni materialu - ambientni slozka
		float[] mat_amb = new float[] { 0.1f, 0.1f, 0.0f, 1 };

		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_amb, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_spec, 0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, mat, 0);

		// nastaveni zdroje svetla - ambientni slozka
		float[] light_amb = new float[] { 1, 1, 1, 1 };
		// nastaveni zdroje svetla - difusni slozka
		float[] light_dif = new float[] { 1, 1, 1, 1 };
		// nastaveni zdroje svetla - zrcadlova slozka
		float[] light_spec = new float[] { 0.3f, 0, 0, 1 };

		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_amb, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_dif, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_spec, 0);

	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();

		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 200.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 200.0f);

		glu.gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		// nastaveni pozice svetla
		float[] light_position;
		if (!light)
			light_position = new float[] { 25, x - width / 2, height / 2 - y,
					1.0f };// bod v prostoru
		else
			light_position = new float[] { 25, x - width / 2, height / 2 - y,
					0.0f };// smer - umisteni v nekonecnu
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

		gl.glPushMatrix();
		gl.glLoadIdentity();
		// koule znazornujici bodovy zdroj svetla
		gl.glTranslatef(25, x - width / 2, height / 2 - y);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
		glut.glutSolidSphere(1, 10, 10);

				
		gl.glFrontFace(GL2.GL_CW);
		
		//orezani odvracenych ploch
		gl.glEnable(GL2.GL_CULL_FACE);
		gl.glCullFace(GL2.GL_BACK);
		
		// zapnuti svetla a nastaveni modelu stinovani
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		if (flat)
			gl.glShadeModel(GL2.GL_FLAT);
		else
			gl.glShadeModel(GL2.GL_SMOOTH);

		// gl.glEnable( GL2.GL_POLYGON_OFFSET_FILL );
		wire = wire % 2;
		switch (wire) {
		case 0:
			gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL);
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_NONE);
			drawScene(gl);
			break;
		case 1:
			gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_LINE);
			gl.glPolygonMode(GL2.GL_BACK, GL2.GL_NONE);
			drawScene(gl);
			break;
		}

		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_LIGHT0);

		
		// zobrazeni telesa bez osvetleni
		gl.glLoadIdentity();
		gl.glColor3f(1.0f, 0f, 1.0f);
		gl.glTranslatef(0, 10, 15);
		gl.glRotatef(90, 1, 0, 0);
		glut.glutSolidTeapot(5, true);
		gl.glLoadIdentity();
		gl.glColor3f(0.0f, 1.f, 1.0f);
		gl.glTranslatef(0, -10, 15);
		gl.glRotatef(90, 1, 0, 0);
		glut.glutWireTeapot(5, true);
		gl.glPopMatrix();

		float color[] = { 1.0f, 1.0f, 1.0f };
		gl.glColor3fv(color, 0);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		String text = new String(this.getClass().getName() + ": [lmb] move, ");

		if (per)
			text = new String(text + "[P]ersp ");
		else
			text = new String(text + "[p]ersp ");

		if (flat)
			text = new String(text + ", [F]lat ");
		else
			text = new String(text + ", [f]lat ");

		if (light)
			text = new String(text + ", [L]ight infinity position");
		else
			text = new String(text + ", [l]ight infinity position");

		switch (wire) {
		case 0:
			text = new String(text + ", sol[i]d");
			break;
		case 1:
			text = new String(text + ", w[i]re");
			break;
		}

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");	
	}

	private void drawScene(GL2 gl) {
		gl.glFrontFace(GL2.GL_CCW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 20, 0);
		glut.glutSolidSphere(5, 30, 30);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, 0);
		glut.glutSolidSphere(5, 30, 30);
		gl.glLoadIdentity();
		gl.glTranslatef(0, -20, 0);
		glut.glutSolidSphere(5, 30, 30);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -15);
		gl.glRotatef(90, 1, 0, 0);
		gl.glFrontFace(GL2.GL_CW);
		glut.glutSolidTeapot(5, true);
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		this.width = width;
		this.height = height;
		glDrawable.getGL().getGL2().glViewport(0, 0, width , height);
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
		x = e.getX();
		y = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		x = e.getX();
		y = e.getY();
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
		case KeyEvent.VK_I:
			wire = (wire + 1);
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void dispose(GLAutoDrawable glDrawable) {
	}
}