package app;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class BoidRunner extends JPanel implements KeyListener, MouseListener, MouseMotionListener  {
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    static ArrayList<Boid> flock = new ArrayList<Boid>();
    static int totalInfected = 1, deathCount = 0, healthyCount = 0, criticalCount = 0, aliveCount;

    static JLabel infectedDisplay, deathDisplay, healthyDisplay, criticalDisplay, aliveDisplay;
    private Sound music;
    
    public boolean addedNewBoid = false;
    int mouseXPosition = (int)(WIDTH/2), mouseYPosition = (int)(HEIGHT/2);

    public BoidRunner() {
        this.setLayout(null);
        this.setBackground(Color.BLACK);
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setFocusable(true);

        this.addKeyListener(this);
        this.addMouseListener(this);

        createLabels();

        for(int i = 0; i < 1200; i++) //1200
            flock.add(new Boid());

        music = new Sound("plague.wav");
        //music = new Sound("ambience.wav"); //uncomment this for music!
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
            int more = (int)(Math.random()*((flock.size()>=900) ? 1000 : 500));
            if(more == 0)
                flock.add(new Boid());
            if(addedNewBoid) {
                flock.add(new Boid(mouseXPosition, mouseYPosition));
                addedNewBoid = false;
            }   
            updateHealthy();
            //updateAlive();
            this.repaint();
            try {
                Thread.sleep(10);
            } catch( InterruptedException ex ){}
        }
    }

    void createLabels() {
        //Healthy
        healthyDisplay = new JLabel("Healthy: "+ healthyCount);
        this.setLayout(new FlowLayout());
        this.add(healthyDisplay);
        healthyDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        healthyDisplay.setForeground(Color.GREEN);
        healthyDisplay.setVisible(true);
        healthyDisplay.setLocation((int)WIDTH/2-400, 200);
        //Infected
        infectedDisplay = new JLabel("|Infected: "+ totalInfected);
        this.setLayout(new FlowLayout());
        this.add(infectedDisplay);
        infectedDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        infectedDisplay.setForeground(Color.RED);
        infectedDisplay.setVisible(true);
        infectedDisplay.setLocation((int)WIDTH/2, 200);
        /*/Critical
        criticalDisplay = new JLabel("|Critical: "+ criticalCount);
        this.setLayout(new FlowLayout());
        this.add(criticalDisplay);
        criticalDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        criticalDisplay.setForeground(Color.RED);
        criticalDisplay.setVisible(true);
        criticalDisplay.setLocation((int)WIDTH/2+400, 200);
        //Alive
        aliveDisplay = new JLabel("|Alive: "+ aliveCount);
        this.setLayout(new FlowLayout());
        this.add(aliveDisplay);
        aliveDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        aliveDisplay.setForeground(Color.BLUE.brighter().brighter());
        aliveDisplay.setVisible(true);
        aliveDisplay.setLocation((int)WIDTH/2-200, 300);
        //Death*/
        deathDisplay = new JLabel("|Dead: "+ deathCount);
        this.setLayout(new FlowLayout());
        this.add(deathDisplay);
        deathDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        deathDisplay.setForeground(Color.WHITE);
        deathDisplay.setVisible(true);
        deathDisplay.setLocation((int)WIDTH/2+200, 300);
    }

    static void toggleCounts(boolean setting) {
        healthyDisplay.setVisible(setting);
        infectedDisplay.setVisible(setting);
    }

    static void updateHealthy() {
        healthyCount = flock.size()-totalInfected-deathCount;
        healthyDisplay.setText("Healthy: " + healthyCount);
    }

    static void updateInfected() {
        totalInfected++;
        healthyCount--;
        infectedDisplay.setText(" Infected: " + totalInfected);
        new Sound("newpatient.wav");
    }

    static void updateDead() {
        deathCount++;
        totalInfected--;
        infectedDisplay.setText(" Infected: " + totalInfected);
        deathDisplay.setText("|Dead: " + deathCount);
        new Sound("death.wav");
    }

    static void updateCritical() {
        criticalCount++;
        criticalDisplay.setText("|Critical: " + criticalCount);
    }

    static void updateAlive() {
        aliveCount = flock.size()-deathCount;
        aliveDisplay.setText("|Alive: " + aliveCount);
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
        if(event.getKeyCode() == KeyEvent.VK_Q)
            toggleCounts(true);
        if(event.getKeyCode() == KeyEvent.VK_E)
            toggleCounts(false);
    }

    public void keyTyped(KeyEvent event) {}

    public void mousePressed(MouseEvent event) {
        mouseXPosition = event.getX();   
        mouseYPosition = event.getY();
        addedNewBoid = true;
    }

    //required for compiling; do not use
    public void mouseClicked( MouseEvent event ) {}

    public void mouseReleased( MouseEvent event ) {}

    public void mouseEntered( MouseEvent event ) {}

    public void mouseExited( MouseEvent event ) {}
    // MouseMotionListener: constantly update whenever mouse is moved
    public void mouseMoved(MouseEvent event) {}

    public void mouseDragged(MouseEvent event) {}
}

