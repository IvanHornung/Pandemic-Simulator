package app;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class BoidRunner extends JPanel implements KeyListener {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    ArrayList<Boid> flock = new ArrayList<Boid>();
    static int totalInfected = 1;
    static int deathCount = 0;

    static JLabel infectedDisplay, deathDisplay;
    private Music music;

    public BoidRunner() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        this.addKeyListener(this);
        
        createLabels();

        for(int i = 0; i < 500; i++)
            flock.add(new Boid());

        
        //music = new Music(); //uncomment this for music!
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
            int more = (int)(Math.random()*((flock.size()>=700) ? 100 : 10));
            if(more == 0)
                flock.add(new Boid());
            this.repaint();
            try {
                Thread.sleep(10);
            } catch( InterruptedException ex ){}
        }
    }
    void createLabels() {
        //Infected
        infectedDisplay = new JLabel("Infected: "+ totalInfected);
        this.setLayout(new FlowLayout());
        this.add(infectedDisplay);
        infectedDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        infectedDisplay.setForeground(Color.GREEN.darker());
        infectedDisplay.setVisible(true);
        infectedDisplay.setLocation((int)WIDTH/2-200, 200);
        //Death
        deathDisplay = new JLabel("Dead: "+ deathCount);
        this.setLayout(new FlowLayout());
        this.add(deathDisplay);
        deathDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        deathDisplay.setForeground(Color.WHITE);
        deathDisplay.setVisible(true);
        deathDisplay.setLocation((int)WIDTH/2+200, 200);
    }

    static void updateInfected() {
        totalInfected++;
        infectedDisplay.setText("Infected: " + totalInfected);
    }

    static void updateDead() {
        deathCount++;
        infectedDisplay.setText("Dead: " + deathCount);
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
