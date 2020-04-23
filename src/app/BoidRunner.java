package app;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class BoidRunner extends JPanel implements KeyListener {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    ArrayList<Boid> flock = new ArrayList<Boid>();
    private Music music;

    public BoidRunner() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        this.addKeyListener(this);
        
        for(int i = 0; i < 500; i++)
            flock.add(new Boid());

        //music = new Music();
    }

    @Override
    public void paintComponent(Graphics page) {
        super.paintComponent(page);
        Graphics2D g = (Graphics2D) page;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for(Boid boid: flock) {
            boid.draw(g);
        }
    }

    public void run() {
        while(true) {
            for(Boid boid : flock){
                boid.edges();
                boid.flock(flock);
                boid.update();
            }
            int more = (int)(Math.random()*5);
            if(more == 0)
                flock.add(new Boid());
            this.repaint();
            try {
                Thread.sleep(10);
            } catch( InterruptedException ex ){}
        }
    }

    void labelConfigure(JLabel label) {
        //labelConfigure(new JLabel("(" + boid.position.getXValue() + ", " + boid.position.getYValue() +")"));
        this.setLayout(new FlowLayout());
        this.add(label);
        label.setFont(new Font("Courier New", Font.PLAIN, 20));
        label.setForeground(Color.WHITE);
        label.setVisible(true);
        label.setVisible(false);
    }


    public void keyReleased( KeyEvent event ) {}

    public void keyPressed( KeyEvent event ) {
        //!General
        if(event.getKeyCode()==KeyEvent.VK_UP) 
            Boid.incrementMaxSpeed();
        if(event.getKeyCode()==KeyEvent.VK_DOWN)
            Boid.decrementMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_Q)
            Boid.incrementMaxForce();
        if(event.getKeyCode() == KeyEvent.VK_A)
            Boid.decrementMaxForce();
        
        //!Alignment
        if(event.getKeyCode()==KeyEvent.VK_W) 
            Boid.incremementAlignmentPerceptionRadius();
        if(event.getKeyCode()==KeyEvent.VK_S)
            Boid.decrementAlignmentPerceptionRadius();
        if(event.getKeyCode() == KeyEvent.VK_E)
            Boid.incrementAlignmentMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_D)
            Boid.decrementAlignmentMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_R)
            Boid.incrementAlignmentMaxForce();
        if(event.getKeyCode() == KeyEvent.VK_F)
            Boid.decrementAlignmentMaxForce();
        //!Cohesion
        if(event.getKeyCode()==KeyEvent.VK_T) 
            Boid.incremementCohesionPerceptionRadius();
        if(event.getKeyCode()==KeyEvent.VK_G)
            Boid.decrementCohesionPerceptionRadius();
        if(event.getKeyCode() == KeyEvent.VK_Y)
            Boid.incrementCohesionMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_H)
            Boid.decrementCohesionMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_U)
            Boid.incrementCohesionMaxForce();
        if(event.getKeyCode() == KeyEvent.VK_J)
            Boid.decrementCohesionMaxForce();
        //!Separation
        if(event.getKeyCode()==KeyEvent.VK_I) 
            Boid.incremementSeparationPerceptionRadius();
        if(event.getKeyCode()==KeyEvent.VK_K)
            Boid.decrementSeparationPerceptionRadius();
        if(event.getKeyCode() == KeyEvent.VK_O)
            Boid.incrementSeparationMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_L)
            Boid.decrementSeparationMaxSpeed();
        if(event.getKeyCode() == KeyEvent.VK_P)
            Boid.incrementSeparationMaxForce();
        if(event.getKeyCode() == KeyEvent.VK_SEMICOLON)
            Boid.decrementSeparationMaxForce();
    }
    public void keyTyped(KeyEvent event) {}
}
