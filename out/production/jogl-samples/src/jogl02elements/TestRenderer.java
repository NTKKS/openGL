package jogl02elements;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL: graficke elementy a jejich atributy,
 * points, lines, polygons
 * 
 * @author PGRF FIM UHK
 * @version 2016
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	int width = 300;
	int height = 200;
	int drawPointMode = 1;
	int drawLineMode = 1;
	int drawPolygonMode = 1;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		OglUtils.printOGLparameters(gl);
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();

		// vynulujeme misto pro kresleni
		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

		// nastaveni modelovaci transformace
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity(); // inicializace na jednotkovou matici

		// nastaveni projekce
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity(); // inicializace na jednotkovou matici

		gl.glBegin(GL2.GL_TRIANGLES); // vykreslime trojuhelnik
		gl.glColor3f(1.0f, 0.0f, 0.0f); // barva prvniho vrcholu CERVENA
		gl.glVertex2f(-1.0f, -1.0f); // souradnice v 2D (levy dolni roh)
		gl.glColor3f(0.0f, 1.0f, 0.0f); // barva prvniho vrcholu ZELENA
		gl.glVertex2f(1.0f, 0.0f); // souradnice v 2D (stred prave strany)
		gl.glColor3f(0.0f, 0.0f, 1.0f); // barva prvniho vrcholu MODRA
		gl.glVertex2f(0.0f, 1.0f); // souradnice v 2D (stred horni strany)
		gl.glEnd(); // ukoncime kresleni trojuhelniku

		drawPoints(gl);
		drawLines(gl);
		drawPolygons(gl);

		gl.glColor3f(1.0f, 1.0f, 1.0f);
		String text = new String(this.getClass().getName() + ": Mode: " + " [P]oints: "
				+ drawPointMode + " [L]ines: " + drawLineMode + " [F]aces: "
				+ drawPolygonMode);
		
		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
	}

	@Override
	public void reshape(GLAutoDrawable glDrawable, int x, int y, int width,
			int height) {
		GL2 gl = glDrawable.getGL().getGL2();
		this.width = width;
		this.height = height;
		// transformace okna nastavena na rozsah ([0,0]-[1,1]) s konstantnim
		// pomerem podle velikosti okna
		if (3 * height < 2 * width) {
			gl.glViewport(-height * 3 / 2, -height, height * 3, 2 * height);
		} else {
			gl.glViewport(-width, -width * 2 / 3, width * 2, width * 4 / 3);
		}
	}

	// key listener
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_P: // body - points
			drawPointMode++;
			;
			break;
		case KeyEvent.VK_L: // Cary - lines
			drawLineMode++;
			;
			break;
		case KeyEvent.VK_F: // Steny - faces
			drawPolygonMode++;
			;
			break;
		}
	}

	public void drawPoints(GL2 gl) {
		// nastaveni michani barev (blending) u 3 a 4 bodu
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE);
		
		if ((drawPointMode % 10) == 0) return;
		// nastaveni velikost bodu
		gl.glPointSize((drawPointMode % 10) * 5);

		// zadani geometrie a bravy
		gl.glBegin(GL2.GL_POINTS);
		gl.glColor3f(1, 0.2f, 0.1f);
		gl.glVertex2f(0.4f, 0.4f);
		gl.glColor4f(0.2f, 1.0f, 1.0f, 1.0f);
		gl.glVertex2f(0.7f, .9f);
		gl.glColor4f(0.2f, 1.0f, 1.0f, 0.5f);
		gl.glVertex2f(0.7f, 0.8f);
		gl.glColor4f(0.2f, 1.0f, 1.0f, 1.0f);
		gl.glVertex2f(0.2f, 0.1f);
		gl.glEnd();
		gl.glDisable(GL2.GL_BLEND);
	}

	public void drawLines(GL2 gl) {
		// nastaveni sirky cary
		gl.glLineWidth((drawLineMode%10+1));
		// povoleni nastaveni vzhledu cary
		gl.glEnable(GL2.GL_LINE_STIPPLE);
		// nastaveni vzhledu cary
		gl.glLineStipple(1, (short) (drawLineMode ^ 2));

		// nastaveni zpusobu vykresleni
		switch (drawLineMode % 4) {
		case 0:
			return;
		case 1:
			gl.glBegin(GL2.GL_LINES); // samostatne usecky, zadane kazdu novou
										// dvojici bodu
			break;
		case 2:
			gl.glBegin(GL2.GL_LINE_STRIP); // posloupnost usecek
			break;
		case 3:
			gl.glBegin(GL2.GL_LINE_LOOP); // uzavrena oblast
			break;
		}

		// zadani geometrie a bravy
		gl.glColor3f(1.0f, 0.5f, 0.0f);
		gl.glVertex2f(0.1f, 0.7f);
		gl.glColor3f(0.0f, 0.5f, 1.0f);
		gl.glVertex2f(0.6f, 0.9f);
		gl.glColor3f(0.1f, 1.0f, 0.5f);
		gl.glVertex2f(0.8f, 0.3f);
		gl.glColor3f(0f, 0.5f, 1.0f);
		gl.glVertex2f(0.1f, 0.2f);
		gl.glEnd();
		
		gl.glDisable(GL2.GL_LINE_STIPPLE);
		
	}

	public void drawPolygons(GL2 gl) {
		// volba poradi pro zadani vrcholu (counter clockwise CCW)
		gl.glFrontFace(GL2.GL_CCW);
		// gl.glFrontFace(GL2.GL_CW);

		// zpusob vykresleni privracenych a odvracenych ploch
		gl.glPolygonMode(GL2.GL_FRONT, // GL2.GL_FRONT_AND_BACK,GL2.GL_FRONT,GL2.GL_BACK
				GL2.GL_FILL); // GL2.GL_LINE,GL2.GL_POINT,GL2.GL_FILL
		gl.glPolygonMode(GL2.GL_BACK, // GL2.GL_FRONT_AND_BACK,GL2.GL_FRONT,GL2.GL_BACK
				GL2.GL_LINE); // GL2.GL_LINE,GL2.GL_POINT,GL2.GL_FILL

		// povoleni a nastaveni odstraneni odvracenych ploch
		// gl.glEnable(GL2.GL_CULL_FACE);
		// gl.glCullFace(GL2.GL_BACK);
		// gl.glCullFace(GL2.GL_FRONT);
		// gl.glCullFace(GL2.GL_FRONT_AND_BACK);

		// nastaveni zpusobu vykresleni polygonu
		switch (drawPolygonMode % 7) {
		case 0:
			return;
		case 1:
			gl.glBegin(GL2.GL_QUADS);
			break;
		case 2:
			gl.glBegin(GL2.GL_QUAD_STRIP);
			break;
		case 3:
			gl.glBegin(GL2.GL_TRIANGLES);
			break;
		case 4:
			gl.glBegin(GL2.GL_TRIANGLE_STRIP);
			break;
		case 5:
			gl.glBegin(GL2.GL_TRIANGLE_FAN);
			break;
		case 6:
			gl.glBegin(GL2.GL_POLYGON);
			break;
		}
		// zadani geometrie a bravy
		gl.glColor3f(1, 1, 1);
		gl.glVertex2f(0.8f, 0.5f);
		gl.glColor3f(0, 0, 0);
		gl.glVertex2f(0.7f, 0.8f);
		gl.glColor3f(1, 1, 1);
		gl.glVertex2f(0.5f, 0.7f);
		gl.glColor3f(0, 1, 1);
		gl.glVertex2f(0.9f, 0.1f);
		gl.glColor3f(0, 1, 0);
		gl.glVertex2f(0.2f, 0.5f);
		gl.glColor3f(1, 0, 0);
		gl.glVertex2f(0.4f, 0.3f);
		gl.glColor3f(0.5f, 0.2f, 0.6f);
		gl.glVertex2f(0.3f, 0.1f);
		gl.glColor3f(1, 1, 0.5f);
		gl.glVertex2f(0.4f, 0.1f);
		gl.glEnd();
		
		gl.glDisable(GL2.GL_CULL_FACE);
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// switch (e.getKeyCode()) {}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
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
	public void dispose(GLAutoDrawable drawable) {
	}

}