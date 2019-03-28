package jogl03transforms;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * transformace v prostoru, FPS, perspektiva, viditelnost, pohled
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLUT glut;
	GLU glu;
	
	int width, height, dx, dy;
	int ox, oy;
	long oldmils;
	long oldFPSmils;
	double	fps;
	
	float uhel = 0, uhelX, uhelY;
	int mode = 0;
	float m[] = new float[16];
	
	boolean per = true, depth = true;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
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
		float speed = 60; // pocet stupnu rotace za vterinu
		float step = speed * (mils - oldmils) / 1000.0f; // krok za jedno
															// prekresleni
															// (frame)
		oldmils = mils;
		
		// zapnuti nebo vypnuti viditelnosti
		if (depth)
			gl.glEnable(GL2.GL_DEPTH_TEST);
		else
			gl.glDisable(GL2.GL_DEPTH_TEST);

		// mazeme image buffer i z-buffer
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glMatrixMode(GL2.GL_MODELVIEW);

		mode = mode % 7;

		switch (mode) {
		case 0:
			// rotace postupnou upravou matice
			gl.glTranslatef(5,5,0);//posun do pocatku
			gl.glRotatef(1, 0, 0, 1);//otoc
			gl.glTranslatef(-5,-5,0);//vrat zpatky

			break;
		case 1:

			// rotace mazanim matice a zvetsovanim uhlu
			gl.glLoadIdentity();
			uhel++;
			gl.glRotatef(uhel, 0, 1, 1);

			break;
		case 2:
			// rotace podle zmeny pozice mysi
			gl.glRotatef(dx, 1, 0, 0);
			gl.glRotatef(dy, 0, 1, 0);

			break;
		case 3:
			// rotace podle fps
			gl.glRotatef(step, 1, 0, 0);
			gl.glRotatef(step, 0, 1, 0);

			break;
		case 4:
			// rotace mazanim matice a vypocet uhlu na zaklade fps
			gl.glLoadIdentity();
			uhel = (uhel + step) % 360;
			gl.glRotatef(uhel, 0, 1, 1);

			break;
		case 5:
			// rotace podle zmeny pozice mysi, osy rotace rotuji s telesem s telesem
			gl.glLoadIdentity();
			gl.glMultMatrixf(m, 0);

			if (Math.abs(dx)>Math.abs(dy)){
				gl.glRotatef(dx, 0, 1, 0);
				dx = 0;
			}	
			else{ 
				gl.glRotatef(dy, 1, 0, 0);
				dy = 0;
			}
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
			
			break;

		case 6:
			// rotace podle zmeny pozice mysi, osy rotace zustavaji svisle a vodorovne
			gl.glLoadIdentity();
			gl.glRotatef(dx, 0, 0, 1);
			gl.glRotatef(dy, 0, 1, 0);
			gl.glMultMatrixf(m, 0);
			gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, m, 0);
			dx = 0;
			dy = 0;

			break;
		}

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		// nastaveni transformace zobrazovaciho objemu
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 100.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 100.0f);

		// pohledova transformace
		// divame se do sceny z kladne osy x, osa z je svisla
		glu.gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);

		gl.glBegin(GL2.GL_TRIANGLE_FAN);
		gl.glColor3f(1.0f, 1.0f, 1.0f);
		gl.glVertex3f(5.0f, 5.0f, 10.0f);
		gl.glColor3f(1.0f, 0.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glVertex3f(10.0f, 0.0f, 0.0f);
		gl.glColor3f(0.0f, 0.0f, 1.0f);
		gl.glVertex3f(10.0f, 10.0f, 0.0f);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glVertex3f(0.0f, 10.0f, 0.0f);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1.0f, 0f, 0f);
		gl.glVertex3f(0f, 0f, 0f);
		gl.glVertex3f(100.0f, 0.0f, 0.0f);
		gl.glColor3f(0f, 1f, 0f);
		gl.glVertex3f(0f, 0f, 0f);
		gl.glVertex3f(0.0f, 100.0f, 0.0f);
		gl.glColor3f(0f, 0f, 1f);
		gl.glVertex3f(0f, 0f, 0f);
		gl.glVertex3f(0.0f, 0.0f, 100.0f);
		gl.glEnd();

		// glut.glutWireCube(10);

		float color[] = { 1.0f, 1.0f, 1.0f };
		gl.glColor3fv(color, 0);
		gl.glDisable(GL2.GL_DEPTH_TEST);


		String text = new String(this.getClass().getName() + ": [Mouse] [M]ode: "
				+ mode + " ");
		if (per)
			text = new String(text + "[P]ersp, ");
		else
			text = new String(text + "[p]ersp, ");
		if (depth)
			text = new String(text + "[D]epth ");
		else
			text = new String(text + "[d]epth ");

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
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
		case KeyEvent.VK_D:
			depth = !depth;
			break;
		case KeyEvent.VK_M:
			mode++;
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