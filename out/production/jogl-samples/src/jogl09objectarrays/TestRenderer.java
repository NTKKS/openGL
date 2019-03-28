package jogl09objectarrays;


import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.common.nio.Buffers;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * objekty GLU a GLUT, arrays 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;
	long oldmils;
	float cuhel = 0;
	int width, height, x, y;
	Texture texture;

	IntBuffer tmpVerticesBuf;
	FloatBuffer tmpColorsBuf;
	IntBuffer tmpIndicesBuf;
	FloatBuffer tmpTextcoordBuf;

	boolean per = true, flat = false, light = true, wire = false, tex = false;
	boolean group1 = false, group2 = false, group3 = true, group4 = false, anim=true;

	ByteBuffer vertices, colors, indicies;
	GLUquadric quadratic;

	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();

		gl.glEnable(GL2.GL_DEPTH_TEST);

		OglUtils.printOGLparameters(gl);
		
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

		texture.enable(gl);
		texture.bind(gl);

		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MAG_FILTER,GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL2.GL_TEXTURE_MIN_FILTER,GL2.GL_LINEAR_MIPMAP_LINEAR);

		/*gl.glActiveTexture(GL2.GL_TEXTURE0);
	    glGenTextures(1, &texId);
	    glBindTexture(GL_TEXTURE_2D, texId);
	    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
	    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 2, 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, tex);
	    glBindTexture(GL_TEXTURE_2D, 0);
	    */
		oldmils = System.currentTimeMillis();

		float[] mat_dif = new float[] { 0, 1, 1, 1 };// nastaveni materialu
		float[] mat_spec = new float[] { 0.3f, 0.0f, 0.0f, 1 };// nastaveni
																// materialu
		float[] mat_amb = new float[] { 0.1f, 0.1f, 0.0f, 1 };// nastaveni
																// materialu
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_amb, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_spec, 0);
		// gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, mat, 0);

		float[] light_amb = new float[] { 1, 1, 1, 1 };// nastaveni ambientni
														// slozky
		float[] light_dif = new float[] { 1, 1, 1, 1 };// nastaveni difusni
														// slozky
		float[] light_spec = new float[] { 0.3f, 0, 0, 1 };// nastaveni
															// zrcadlove slozky
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_amb, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_dif, 0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_spec, 0);

		// gl.glEnable(GL2.GL_CULL_FACE);

		gl.glNewList(1, GL2.GL_COMPILE);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0f, 0f);
		gl.glNormal3f(1, 0, 0);
		gl.glVertex3f(0.0f, 10.0f, 0.0f);
		gl.glTexCoord2f(0.0f, 1f);
		gl.glVertex3f(0.0f, 10.0f, 10.0f);
		gl.glTexCoord2f(1f, 1f);
		gl.glVertex3f(0.0f, 0.0f, 10.0f);
		gl.glTexCoord2f(1f, 0f);
		gl.glVertex3f(0.0f, 0.0f, 0.0f);
		gl.glEnd();

		gl.glEndList();
		
		setupVertexArrays(gl);

		quadratic = glu.gluNewQuadric(); // Vytvori novou kvadriku

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	private void setupVertexArrays(GL2 gl) {
		int vertices[] = new int[] // 6 vertices;
		{ 0, 15, 0, 10, 0, 10, -10, 0, 10, -10, 0, -10, 10, 0, -10, 0, -15, 0 };//3 values per vertex
		float colors[] = new float[] { 1f, 1f, 1f, 1f, 0f, 0f, 0f, 1f, 0f, 0f,
				0f, 1f, 1f, 1f, 0f, 0.5f, 0.5f, 0.5f }; //3 values per vertex
		int indices[] = new int[] { 0, 1, 2, 0, 2, 3, 0, 3, 4, 0, 4, 1, 1, 2,
				5, 2, 3, 5, 3, 4, 5, 4, 1, 5 }; //3 values per triangle
		float textcoord[] = new float[] { 0.4f, 0, 0.2f, 0.5f, 0, 0.9f, 0.5f, 0, 1f, 1f }; //2 values per vertex
		tmpVerticesBuf = Buffers.newDirectIntBuffer(vertices);
		tmpColorsBuf = Buffers.newDirectFloatBuffer(colors);
		tmpIndicesBuf = Buffers.newDirectIntBuffer(indices);
		tmpTextcoordBuf = Buffers.newDirectFloatBuffer(textcoord);
		
		gl.glVertexPointer(3, GL2.GL_INT, 0, tmpVerticesBuf);
		gl.glColorPointer(3, GL2.GL_FLOAT, 0, tmpColorsBuf);
		gl.glTexCoordPointer( 2, GL2.GL_FLOAT, 0, tmpTextcoordBuf);
	
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		long mils = System.currentTimeMillis();
		float uhel = 10 * (mils - oldmils) / 1000.0f; // rotace 10stupnu za
														// sekundu
		oldmils = mils;
		if (anim) cuhel = cuhel + uhel;

		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		gl.glEnable(GL2.GL_DEPTH_TEST);

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();
		if (per)
			glu.gluPerspective(45, width / (float) height, 0.1f, 200.0f);
		else
			gl.glOrtho(-20 * width / (float) height, 20 * width
					/ (float) height, -20, 20, 0.1f, 200.0f);

		glu.gluLookAt(100, 0, 0, 0, 0, 0, 0, 0, 1);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		if (anim) gl.glRotatef(uhel, 0, 1, 1); // postupna rotace sceny

		gl.glPushMatrix();
		gl.glLoadIdentity();
		gl.glRotatef(-cuhel * 5, 0, 1, 0);// nezavisla rotace zdroje svetla

		float[] light_position = new float[] { 25, x - width / 2,
				height / 2 - y, 1.0f };// w=0:v nekonecnu
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position, 0);

		gl.glTranslatef(25, x - width / 2, height / 2 - y);
		glut.glutSolidSphere(1, 10, 10);
		gl.glPopMatrix();

		
		if (light){
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_LIGHT0);
		}
		else{
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_REPLACE);
		}
		
		if (flat)
			gl.glShadeModel(GL2.GL_FLAT);
		else
			gl.glShadeModel(GL2.GL_SMOOTH);

		gl.glFrontFace(GL2.GL_CW);

		if (tex)
			gl.glEnable(GL2.GL_TEXTURE_2D);
		else
			gl.glDisable(GL2.GL_TEXTURE_2D);

		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);

		if (wire)
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_LINE);
		else
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);

		// GLUT
		if (group1) {
			
			float[] mat_dif = new float[] { 0, 1, 1, 1 };// nastaveni materialu
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);

			gl.glColor3f(0, 1, 1);
			
			gl.glPushMatrix();
			gl.glTranslatef(0, -20, 0); //teapot
			if (wire)
				glut.glutWireTeapot(5);
			else
				glut.glutSolidTeapot(5);
			gl.glPopMatrix();

			gl.glEnable(GL2.GL_TEXTURE_GEN_S);
			gl.glEnable(GL2.GL_TEXTURE_GEN_T);
			gl.glTexGeni(GL2.GL_S, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
			gl.glTexGeni(GL2.GL_T, GL2.GL_TEXTURE_GEN_MODE, GL2.GL_SPHERE_MAP);
			
			gl.glPushMatrix();
			gl.glTranslatef(0, 20, 0); // koule
			if (wire)
				glut.glutWireSphere(5, 30, 30);
			else
				glut.glutSolidSphere(5, 30, 30);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 20);
			if (wire)
				glut.glutWireCone(5, 10, 30, 30);
			else
				glut.glutSolidCone(5, 10, 30, 30);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 0);
			if (wire)
				glut.glutWireCube(5);
			else
				glut.glutSolidCube(5);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(-10, 0, 0);
			if (wire)
				glut.glutWireCylinder(5, 10, 10, 10);
			else
				glut.glutSolidCylinder(5, 10, 10, 10);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(10, 0, -10);
			if (wire)
				glut.glutWireTorus(2, 4, 10, 10);
			else
				glut.glutSolidTorus(2, 4, 10, 10);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(10, 10, 10);
			gl.glScalef(3, 3, 3);
			if (wire)
				glut.glutWireRhombicDodecahedron();
			else
				glut.glutSolidRhombicDodecahedron();
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(10, -10, 10);
			gl.glScalef(3, 3, 3);
			if (wire)
				glut.glutWireDodecahedron();
			else
				glut.glutSolidDodecahedron();
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glTranslatef(-10, -10, -10);
			gl.glScalef(3, 3, 3);
			if (wire)
				glut.glutWireTetrahedron();
			else
				glut.glutSolidTetrahedron();
			gl.glPopMatrix();
			
			gl.glDisable(GL2.GL_TEXTURE_GEN_S);
			gl.glDisable(GL2.GL_TEXTURE_GEN_T);
			
		}

		// CallList
		if (group2) {
			float[] mat_dif = new float[] { 1, 0, 1, 1 };// nastaveni materialu
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);
			
			gl.glColor3f(1, 0, 1);
			
			gl.glPushMatrix(); //D-List 
			gl.glTranslatef(10, 0, 10);
			gl.glRotatef(cuhel , 1, 0, 0);
			gl.glCallList(1);
			gl.glPopMatrix();

			gl.glPushMatrix(); //D-List 
			gl.glTranslatef(0, 0, 10);
			gl.glRotatef(cuhel , 1, 0, 1);
			gl.glCallList(1);
			gl.glPopMatrix();
		}
		// Arrays
		if (group3) {
			gl.glEnable(GL2.GL_VERTEX_ARRAY);
			gl.glEnable(GL2.GL_COLOR_ARRAY);
			gl.glEnable( GL2.GL_TEXTURE_COORD_ARRAY);
			float[] mat_dif = new float[] { 0.5f, 0.5f, 1, 1 };// nastaveni materialu
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);

			gl.glColor3f(0.5f, 0.5f, 1);
			
			gl.glPushMatrix(); // VertexArray-ArrayElement
			gl.glTranslatef(0, 0, 10);
			gl.glRotatef(-cuhel * 5, 1, 0, 0);
			gl.glBegin(GL2.GL_TRIANGLES);
			gl.glArrayElement(0);
			gl.glArrayElement(2);
			gl.glArrayElement(5);
			gl.glEnd();
			gl.glPopMatrix();

			gl.glPushMatrix(); // VertexArray-DrawElements
			gl.glTranslatef(0, 0, -20);
			gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
			gl.glColor3f(1.0f, 0.0f, 1.0f);
			gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_INT,
					tmpIndicesBuf);
			gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl.glPopMatrix();

			gl.glPushMatrix(); // VertexArray-glDrawRangeElements
			gl.glTranslatef(0, 10, 0);
			gl.glRotatef(-cuhel * 5, 0, 0, 1);
			gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
			gl.glDrawRangeElements(GL2.GL_TRIANGLES, 0, 5, 24,
					GL2.GL_UNSIGNED_INT, tmpIndicesBuf);
			gl.glPopMatrix();

			gl.glPushMatrix(); // VertexArray-DrawArrays
			gl.glTranslatef(10, -10, 0);
			gl.glDrawArrays(GL2.GL_TRIANGLE_FAN, 0, 5);
			gl.glPopMatrix();
			gl.glDisable(GL2.GL_VERTEX_ARRAY);
			gl.glDisable(GL2.GL_COLOR_ARRAY);
			gl.glDisable( GL2.GL_TEXTURE_COORD_ARRAY);
			
		}

		//GLU
		if (group4) {
			float[] mat_dif = new float[] { 1, 0.5f, 0, 1 };// nastaveni materialu
			gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0);
			
			gl.glColor3f(1, 0.5f, 0);
			
			glu.gluQuadricNormals(quadratic, GLU.GLU_SMOOTH); // Urci normaly pro stinovani
			glu.gluQuadricTexture(quadratic, true); // Souradnice do textury
			
			gl.glPushMatrix();
			gl.glRotatef(cuhel * 2, 1, 1, 0);

			gl.glPushMatrix();
			gl.glTranslatef(0, -10, 0);
			glu.gluSphere(quadratic, 10f, 32, 32);// Koule
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(0, 20, 0);
			glu.gluCylinder(quadratic, 5f, 10, 10, 32, 32);// valec
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 20);
			glu.gluPartialDisk(quadratic, 5f, 10, 32, 32, 15, 205);// vysec
			gl.glPopMatrix();

			gl.glPopMatrix();
		}

		
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_LIGHT0);
		gl.glDisable(GL2.GL_TEXTURE_2D);

		float color[] = { 1.0f, 1.0f, 1.0f };
		gl.glColor3fv(color, 0);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		String text = new String(this.getClass().getName() + ": [lmb] move, ");
		if (per)
			text = new String(text + "[P]ersp, ");
		else
			text = new String(text + "[p]ersp, ");

		if (flat)
			text = new String(text + "[F]lat, ");
		else
			text = new String(text + "[f]lat, ");

		if (light)
			text = new String(text + "[L]ight, ");
		else
			text = new String(text + "[l]ight, ");

		if (tex)
			text = new String(text + "[T]exture, ");
		else
			text = new String(text + "[t]exture, ");

		if (anim)
			text = new String(text + "[A]nim, ");
		else
			text = new String(text + "[a]nim, ");

		if (wire)
			text = new String(text + "w[i]re, ");
		else
			text = new String(text + "sol[i]d, ");
		
		text = new String(text + "[");
		
		if (group1)
			text = new String(text + "1+");
		else
			text = new String(text + "1-");
		
		if (group2)
			text = new String(text + "2+");
		else
			text = new String(text + "2-");
		if (group3)
			text = new String(text + "3+");
		else
			text = new String(text + "3-");
		if (group4)
			text = new String(text + "4+");
		else
			text = new String(text + "4-");
		
		text = new String(text + "] objects");
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
		x = e.getX();
		y = e.getY();
		cuhel = 0;
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
		case KeyEvent.VK_A:
			anim = !anim;
			break;
		case KeyEvent.VK_F:
			flat = !flat;
			break;
		case KeyEvent.VK_T:
			tex = !tex;
			break;
		case KeyEvent.VK_L:
			light = !light;
			break;
		case KeyEvent.VK_I:
			wire = !wire;
			break;
		case KeyEvent.VK_1:
			group1 = !group1;
			break;
		case KeyEvent.VK_2:
			group2 = !group2;
			break;
		case KeyEvent.VK_3:
			group3 = !group3;
			break;
		case KeyEvent.VK_4:
			group4 = !group4;
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