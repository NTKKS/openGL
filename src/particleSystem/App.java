package particleSystem;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

public class App {

    private static final int FPS = 60;

    public void start() {
        try {
            Frame testFrame = new Frame("Particle System");
            testFrame.setSize(512, 384);

            // setup OpenGL v2
            GLProfile profile = GLProfile.get(GLProfile.GL2);
            GLCapabilities capabilities = new GLCapabilities(profile);
            capabilities.setRedBits(8);
            capabilities.setBlueBits(8);
            capabilities.setGreenBits(8);
            capabilities.setAlphaBits(8);
            capabilities.setDepthBits(24);

            // setup canvas - widget drawn in the JFrame
            GLCanvas canvas = new GLCanvas(capabilities);
            Renderer ren = new Renderer();
            canvas.addGLEventListener(ren);
            canvas.addMouseListener(ren);
            canvas.addMouseWheelListener(ren);
            canvas.addMouseMotionListener(ren);
            canvas.addKeyListener(ren);
            canvas.setSize(512, 384);
            canvas.setFocusable(true);

            testFrame.add(canvas);

            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);

            testFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    new Thread() {
                        @Override
                        public void run() {
                            if (animator.isStarted()) animator.stop();
                            System.exit(0);
                        }
                    }.start();
                }
            });
            testFrame.setTitle(ren.getClass().getName());
            testFrame.pack();
            testFrame.setVisible(true);
            animator.start(); // start the animation loop

            JFrame descr = new JFrame("Projekt: Částicový systém");
            JOptionPane.showMessageDialog(descr,
                    "LMB - camera rotation\n" +
                            "8,9 - emitter size\n" +
                            "5,6 - emitter velocity\n" +
                            "2,3 - particle die\n" +
                            "I,O - time scale\n" +
                            "K,L - central force\n" +
                            "M,N - wind force\n\n" +
                            "Jan Janás, PGRF2, Duben 2019",
                    "Projekt: Částicový systém",JOptionPane.PLAIN_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().start());
    }
}
