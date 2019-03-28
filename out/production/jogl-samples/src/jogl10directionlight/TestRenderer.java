package jogl10directionlight;


import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import utils.OglUtils;

/**
 * trida pro zobrazeni sceny v OpenGL:
 * osvetleni, material, pozice svetla, smer osvetleni 
 * @author PGRF FIM UHK
 * @version 2015
 */
public class TestRenderer implements GLEventListener, MouseListener,
		MouseMotionListener, KeyListener {

	GLU glu;
	GLUT glut;
	
	int width,height,x=width/2,y=0,lx=0,ly=0;
	
	boolean per=true,flat=false,light=false;
	boolean spot=true;


	@Override
	public void init(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		glu = new GLU();
		glut = new GLUT();
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		
		OglUtils.printOGLparameters(gl);
				
		float[] mat_dif = new float[] {0,1,0,1};// nastaveni materialu
		float[] mat_spec = new float[] {0.8f,0.0f,0.0f,1};// nastaveni materialu
		float[] mat_amb = new float[] {0.1f,0.1f,0.1f,1};// nastaveni materialu
//		float[] mat = new float[] {0.0f,0.1f,0.3f,1};// nastaveni materialu
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_amb, 0); 
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_dif, 0); 
		gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_spec, 0); 
		//gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, mat, 0); 
		
		float[] light_amb = new float[] {1,1,1,1};// nastaveni ambientni slozky
		float[] light_dif = new float[] {1,1,1,1};// nastaveni difusni slozky
		float[] light_spec = new float[] {1,1,1,1};// nastaveni zrcadlove slozky
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, light_amb,0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_dif,0);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light_spec,0);
		
	}

	@Override
	public void display(GLAutoDrawable glDrawable) {
		GL2 gl = glDrawable.getGL().getGL2();
		
		
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		
		gl.glEnable(GL2.GL_DEPTH_TEST);
		gl.glEnable(GL2.GL_NORMALIZE);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glPolygonMode(GL2.GL_FRONT_AND_BACK,
				   GL2.GL_FILL);		//GL2.GL_LINE,GL2.GL_POINT,GL2.GL_FILL
		gl.glLoadIdentity();
		if (per) 
			glu.gluPerspective(45, width /(float) height, 0.1f, 200.0f);
		else
			gl.glOrtho(-20*width /(float) height, 20*width /(float) height, -20, 20, 0.1f, 200.0f);

		glu.gluLookAt(50, 0, 0, 0, 0, 0, 0, 0, 1);
		
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();
		//nastaveni pozice svetla
		float[] light_position;
		float scale=1/15f;
		
		if (!light)
		light_position=  new float[] {25,(x-width/2)*scale,(height/2-y)*scale,1.0f};//bod v prostoru
		else
		light_position=  new float[] {25,(x-width/2)*scale,(height/2-y)*scale,0.0f};//smer - umisteni v nekonecnu
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position,0);
		
		float[] light_direction =  new float[] {-25,(lx)*scale,(ly)*scale,0.0f};
		//float[] light_direction =  new float[] {-50,(lx-width/2)*scale-light_position[1],(height/2-ly)*scale-light_position[2],0.0f};
		//float[] light_direction =  new float[] {-50,0f,0f,0.0f};
		//System.out.println("pos: x"+light_position[0]+"y "+light_position[1]+"z "+light_position[2]);
		//System.out.println("dir: x"+light_direction[0]+"y "+light_direction[1]+"z "+light_direction[2]);
		// uhel svetelneho kuzele
		if (spot)
			gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF,20);
		else
			gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF,180);
		// exponent pri vypoctu ubytku osvetleni
		gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_EXPONENT, 0.8f);
		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPOT_DIRECTION, light_direction,0);
		
		gl.glPushMatrix();
		gl.glLoadIdentity();
		// koule znazornujici bodovy zdroj svetla
		gl.glTranslatef(light_position[0],light_position[1],light_position[2]);
		gl.glColor3f(1.0f, 1.0f, 0.0f);
		glut.glutSolidSphere(1, 10, 10);
		
		//zapnuti svetla a nastaveni modelu stinovani 
		gl.glEnable(GL2.GL_LIGHTING);
		gl.glEnable(GL2.GL_LIGHT0);
		if (flat)
			gl.glShadeModel(GL2.GL_FLAT);
		else
			gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glLoadIdentity();
		gl.glTranslatef(-50,0,0);
		gl.glBegin(GL2.GL_QUADS);
		for (int i = -100; i < 100; i++) {
			for (int j = -100; j < 100; j++) {
				gl.glNormal3f(1, 0, 0);
				gl.glVertex3f(0, i, j);
				gl.glVertex3f(0, i+1, j);
				gl.glVertex3f(0, i+1, j+1);
				gl.glVertex3f(0, i, j+1);
			}	
		}
		gl.glEnd();
		
		gl.glLoadIdentity();
		gl.glTranslatef(0,20,0);
		glut.glutSolidSphere(5, 30, 30);
		gl.glLoadIdentity();
		gl.glTranslatef(0,0,0);
		glut.glutSolidSphere(5, 30, 30); 
		gl.glLoadIdentity();
		gl.glTranslatef(0,-20,0);
		glut.glutSolidSphere(5, 30, 30);
		gl.glLoadIdentity();
		gl.glTranslatef(0,0,-15);
		gl.glRotatef(90,1,0,0);
		glut.glutSolidTeapot(5, true);
		gl.glDisable(GL2.GL_LIGHTING);
		gl.glDisable(GL2.GL_LIGHT0);
		
		gl.glLoadIdentity();
		
		
		//zobrazeni telesa bez osvetleni
		float r=1.0f, g=0.0f, b=1.0f;
		gl.glColor3f(r,g,b);
		gl.glTranslatef(0,0,15);
		gl.glRotatef(90,1,0,0);
		glut.glutSolidTeapot(5, true);
		gl.glPopMatrix();
		
		float color[]={1.0f, 1.0f, 1.0f};
		gl.glColor3fv(color,0);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		gl.glLightf(GL2.GL_LIGHT0, GL2.GL_SPOT_CUTOFF,180);
		
		
		String text = new String(this.getClass().getName() + ": [lmb] light position, [rmb] light direction");
		if (per)
			text = new String(text + ", [P]ersp ");
		else
			text = new String(text + ", [p]ersp ");
		
		if (flat)
			text = new String(text + ", [F]lat ");
		else
			text = new String(text + ", [f]lat ");

		if (light)
			text = new String(text + ", [I]nfinity light position ");
		else
			text = new String(text + ", [i]nfinity light position ");

		if (spot)
			text = new String(text + ", spot [L]ight");
		else
			text = new String(text + ", spot [l]ight");

		OglUtils.drawStr2D(glDrawable, 3, height-20, text);
		OglUtils.drawStr2D(glDrawable, width-90, 3, " (c) PGRF UHK");
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		 this.width=width;
	 	 this.height=height;
	 	x=width/2;
	 	y=height/2;
	 	lx=0;
	 	ly=0;
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
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK)!=0) {
			x = e.getX();
			y = e.getY();
		}
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK)!=0)  {
			lx = e.getX()-x;
			ly = -e.getY()+y;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK)!=0) {
			x = e.getX();
			y = e.getY();
		}
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK)!=0) {
			lx = e.getX()-x;
			ly = -e.getY()+y;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		//switch (e.getKeyCode()) {
		//	}
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
		case KeyEvent.VK_I:
			light = !light;
			break;
		case KeyEvent.VK_L:
			spot = !spot;
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