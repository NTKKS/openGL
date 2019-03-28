package jogl05texture;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

//import com.jogamp.opengl.util.awt.TextRenderer;
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
 * textura mapovani, transformace textury, kvadrika
 * @author PGRF FIM UHK
 * @version 2015
 */

public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;
	Texture texture;

	int width, height, dx, dy;
	int ox, oy;
	float uhel = 0;
	boolean per = true;
	boolean move = true;
	int tex = 1;
	int texApp = 1;

	GLUquadric quadratic;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		OglUtils.printOGLparameters(gl);

		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glFrontFace(GL2.GL_CCW);
		gl.glPolygonMode(GL2.GL_FRONT, GL2.GL_FILL); 
		gl.glPolygonMode(GL2.GL_BACK, GL2.GL_FILL);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		quadratic = glu.gluNewQuadric(); // nova kvadrika
		glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); // normaly pro stinovani
		glu.gluQuadricTexture(quadratic, true); // souradnice do textury

		System.out.println("Loading texture...");
		InputStream is = getClass().getResourceAsStream("/test_texture.jpg"); // vzhledem k adresari res v projektu 
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
		
		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glClearColor(0f, 0f, 0f, 1f);
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 200.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 200.0f);

		glu.gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);

		gl.glEnable(GL2.GL_TEXTURE_2D);
		gl.glDisable(GL2.GL_LIGHTING);
		
		int paramTex = GL2.GL_REPEAT;
		String textureMode="";
		switch (tex % 6) {
		case 0:
			gl.glDisable(GL2.GL_TEXTURE_2D);
			textureMode="Disable";
			break;
		case 1:
			paramTex = GL2.GL_REPEAT;
			textureMode="REPEAT";
			break;
		case 2:
			paramTex = GL2.GL_MIRRORED_REPEAT;
			textureMode="MIRRORED_REPEAT";
			break;
		case 3:
			paramTex = GL2.GL_CLAMP_TO_EDGE;
			textureMode="CLAMP_TO_EDGE";
			break;
		case 4:
			paramTex = GL2.GL_CLAMP_TO_BORDER;
			textureMode="CLAMP_TO_BORDER";
			break;
		case 5:
			paramTex = GL2.GL_CLAMP;
			textureMode="CLAMP";
			break;
		}

		int paramTexApp = GL2.GL_REPLACE;
		String textureApp="";
		switch (texApp % 3) {
		case 1:
			paramTexApp = GL2.GL_REPLACE;
			textureApp="REPLACE";
			break;
		case 2:
			paramTexApp = GL2.GL_MODULATE;
			textureApp="MODULATE";
			break;
		case 0:
			paramTexApp = GL2.GL_ADD;
			textureApp="ADD ";
			break;
		}

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, paramTex);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, paramTex);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, paramTexApp);
		// gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MAG_FILTER,GL2.GL_LINEAR);
		// gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR_MIPMAP_LINEAR);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		if (move){
			uhel++;
			gl.glRotatef(1, 0, 0, 1);
			gl.glRotatef(dx, 1, 0, 0);
			gl.glRotatef(dy, 0, 1, 0);
		}
		
		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glRotatef(uhel / 2, 0, 0, 1);
		gl.glTranslatef(-1f, -1f, 0);
		gl.glScalef(2f, 2f, 0);

		gl.glColor3f(0.1f, 0.9f,0.1f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0f, 0f);
		gl.glVertex3f(0.0f, 10.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1f);
		gl.glVertex3f(0.0f, 10.0f, 10.0f);
		gl.glTexCoord2f(1f, 1f);
		gl.glVertex3f(0.0f, 0.0f, 10.0f);
		gl.glTexCoord2f(1f, 0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glEnd();

		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPushMatrix();
		gl.glTranslatef(0, -20, 0);
		gl.glScalef(0.6f, 0.8f, 1.2f);
		gl.glRotatef(2 * uhel, 0, 0, 1);
		gl.glColor3f(0.9f,0.1f, 0.1f);
		glu.gluSphere(quadratic, 10f, 32, 32);// Koule
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslatef(10f, 0f, 0f);
		gl.glMatrixMode(GL2.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glScalef(2f, 0.5f, 1f);

		gl.glColor3f(0.1f,0.1f, 0.9f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0.1f, 0.1f);
		gl.glVertex3f(0.0f, 10.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 0.9f);
		gl.glVertex3f(0.0f, 10.0f, 10.0f);
		gl.glTexCoord2f(1.1f, 0.8f);
		gl.glVertex3f(0.0f, 0.0f, 10.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glEnd();

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glPopMatrix();
		gl.glDisable(GL2.GL_TEXTURE_2D);

		float color[] = { 1.0f, 1.0f, 1.0f };
		gl.glColor3fv(color, 0);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		String text = new String(this.getClass().getName() + ": [lmb] move");
		if (per)
			text = new String(text + ", [P]ersp ");
		else
			text = new String(text + ", [p]ersp ");

		if (move)
			text = new String(text + ", Ani[M] ");
		else
			text = new String(text + ", Ani[m] ");

		text = new String(text + ", [T]exture map: " + textureMode);
		text = new String(text + ", [A]pplication: " + textureApp);
		
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
		case KeyEvent.VK_T:
			tex++;
			break;
		case KeyEvent.VK_A:
			texApp++;
			break;
		case KeyEvent.VK_M:
			move = !move;
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