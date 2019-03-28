import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;



public class JOGLApp {
	private static final int FPS = 60; // animator's target frames per second

	private GLCanvas canvas = null;

	private Frame testFrame;
	
	private int demoId = 1;
	private KeyAdapter keyAdapter;

	static String[] names = {"start", "elements", "transforms", "light", "texture", "pushPopClip",
		"viewport", "cameraSky", "objectArrays", "directionLight"};

	public void start() {
		try {
			testFrame = new Frame("TestFrame");
			testFrame.setSize(512, 384);

			makeGUI(testFrame);

			setApp(testFrame, 1);

			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void makeGUI(Frame testFrame) {
		ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				setApp(testFrame, Integer.valueOf(ae.getActionCommand().substring(0,ae.getActionCommand().lastIndexOf('-')-1).trim()));
			}
		};

		MenuBar menuBar = new MenuBar();
		Menu menu = new Menu("Menu");
		MenuItem m;
		for (int i = 1; i <= 10; i++) {
			m = new MenuItem(new Integer(i).toString()+" - "+names[i-1]);
			m.addActionListener(actionListener);
			menu.add(m);
		}
		
		keyAdapter = new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_HOME:
					demoId = 1;
					setApp(testFrame, demoId);

					break;
				case KeyEvent.VK_END:
					demoId = names.length;
					setApp(testFrame, demoId);
					break;
				case KeyEvent.VK_LEFT:
					if (demoId > 1)
						demoId--;
					setApp(testFrame, demoId);
					break;
				case KeyEvent.VK_RIGHT:
					if (demoId < names.length)
						demoId++;
					setApp(testFrame, demoId);
					break;
				}
			}

		};

		menuBar.add(menu);
		testFrame.setMenuBar(menuBar);
	}

	private void setApp(Frame testFrame, int type) {
		Dimension dim;
		if (canvas != null){
			testFrame.remove(canvas);
			dim = canvas.getSize();
		} else {
			dim = new Dimension(600, 400);
		}
		
		// setup OpenGL Version 2
		GLProfile profile = GLProfile.get(GLProfile.GL2);
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setRedBits(8);
		capabilities.setBlueBits(8);
		capabilities.setGreenBits(8);
		capabilities.setAlphaBits(8);
		capabilities.setDepthBits(24);

		canvas = new GLCanvas(capabilities);
		canvas.setSize(dim);
		testFrame.add(canvas);
		
		Object ren=null;
		switch (type) {
		case 1: 
			ren = (Object) new jogl01start.TestRenderer();
			break;
		case 2: 
			ren = (Object) new jogl02elements.TestRenderer();
			break;		
		case 3: 
			ren = (Object) new jogl03transforms.TestRenderer();
			break;
		case 4: 
			ren = (Object) new jogl04light.TestRenderer();
			break;		
		case 5: 
			ren = (Object) new jogl05texture.TestRenderer();
			break;
		case 6: 
			ren = (Object) new jogl06pushpopclip.TestRenderer();
			break;		
		case 7: 
			ren = (Object) new jogl07viewport.TestRenderer();
			break;
		case 8: 
			ren = (Object) new jogl08camerasky.TestRenderer();
			break;		
		case 9: 
			ren = (Object) new jogl09objectarrays.TestRenderer();
			break;
		case 10: 
			ren = (Object) new jogl10directionlight.TestRenderer();
			break;		
		}

		canvas.addGLEventListener((GLEventListener)ren);
		canvas.addKeyListener((KeyListener)ren);
		canvas.addKeyListener(keyAdapter);
		canvas.addMouseListener((MouseListener)ren);
		canvas.addMouseMotionListener((MouseMotionListener)ren);
		canvas.requestFocus();

		final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

		testFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				new Thread() {
					@Override
					public void run() {
						if (animator.isStarted())
							animator.stop();
						System.exit(0);
					}
				}.start();
			}
		});
		testFrame.setTitle(ren.getClass().getName());
		testFrame.pack();
		testFrame.setVisible(true);
		animator.start(); // start the animation loop

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new JOGLApp().start());
	}

}