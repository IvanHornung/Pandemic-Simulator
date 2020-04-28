package app;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;

public class BoidRunner extends JPanel implements KeyListener, MouseListener, MouseMotionListener  {
    private static final long serialVersionUID = -8716187417647724411L;
    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    static ArrayList<Boid> flock = new ArrayList<Boid>();
    static int totalInfected = 1, deathCount = 0, healthyCount = 0, criticalCount = 0, 
            aliveCount, recoveryCount = 0, visiblyDead = 0, diagnosedCount = 0, paramedicCount = 0, paranoidCount = 0;

    static JLabel infectedDisplay, deathDisplay, healthyDisplay, criticalDisplay, aliveDisplay, recoveredDisplay;
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
        //music = new Sound("ambience.wav");
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

    boolean intensityPlayed = false, milestonePlayed = false;// decremented = false;

    public void run() {
        while(true) {
            int toAdd = 0;
            totalInfected = 0; healthyCount = 0; recoveryCount = 0; visiblyDead = 0; diagnosedCount = 0; paramedicCount = 0; paranoidCount = 0;
            for(int i = 0; i < flock.size(); i++){
                flock.get(i).edges();
                flock.get(i).flock(flock);
                flock.get(i).update();
                if(flock.get(i).isParamedic)
                    paramedicCount++;
                else if(flock.get(i).healthStatus == Boid.HEALTHY)
                    healthyCount++;
                else if(flock.get(i).healthStatus == Boid.INFECTED)
                    totalInfected++;
                else if(flock.get(i).healthStatus == Boid.RECOVERED)
                    recoveryCount++;
                else if(flock.get(i).healthStatus == flock.get(i).DIAGNOSED)
                    diagnosedCount++;
                else if(flock.get(i).healthStatus == Boid.PARANOID)
                    paranoidCount++;
                else
                    visiblyDead++;
                if(flock.get(i).dead && ((int)(Math.random()*(totalInfected*600+((totalInfected == 0)?1:0))) <= visiblyDead)) {
                    flock.remove(i);
                    i--;
                    toAdd++;
                } else if(flock.get(i).isParamedic && totalInfected <= flock.size()*0.5 && (int)(Math.random()*(flock.size()-totalInfected))*6000 == 0) {
                    flock.remove(i);
                    i--;
                    toAdd++;
                    new Sound("bell.wav");
                }
                else if(flock.get(i).isParamedic && Boid.lockedOn) {
                    flock.get(i).sirenCount++;
                    if(flock.get(i).sirenCount % 3 == 0) {
                        flock.get(i).sirens++;
                        if(flock.get(i).sirens==0)
                            flock.get(i).PARAMEDIC = Color.BLUE;
                        else if(flock.get(i).sirens==1)
                            flock.get(i).PARAMEDIC = Color.WHITE;
                        else if(flock.get(i).sirens == 2)
                            flock.get(i).PARAMEDIC = Color.RED;
                        flock.get(i).healthStatus = flock.get(i).PARAMEDIC;
                    } if(flock.get(i).sirens > 2) flock.get(i).sirens = -1;
                } else if(flock.get(i).isParamedic && flock.get(i).PARAMEDIC != Color.BLUE) {
                    flock.get(i).PARAMEDIC = Color.BLUE;
                    flock.get(i).healthStatus = flock.get(i).PARAMEDIC;
                }
                if((int)(Math.random()*healthyCount*2000+((healthyCount == 0)?1:0)) == 0 && !flock.get(i).hasDisease && diagnosedCount >= 3 && flock.get(i).healthStatus != Boid.PARANOID && paranoidCount <= 15) {
                    flock.get(i).healthStatus = Boid.PARANOID;
                    new Sound("paranoia.wav");
                } if(recoveryCount >= 800 && flock.get(i).healthStatus == Boid.PARANOID && (int)(int)(Math.random()*totalInfected*200+((totalInfected == 0)?1:0)) == 0 ) {
                    flock.get(i).healthStatus = Boid.HEALTHY;
                    new Sound("paranoiaEnded.wav");
                }
            }
            if(clearGrid) {
                for(int i = 0; i < flock.size(); i++) {
                    flock.remove(i);
                    i--;
                }
                clearGrid = false;
            }
            if(addedBoids.size() != 0) {
                for(int i = 0; i < addedBoids.size(); i++) {
                    flock.add(addedBoids.get(i));
                    addedBoids.remove(i);
                    i--;
                }
            }
            if(paramedicCount <= 2 && diagnosedCount != 0) {
                flock.add(new Boid(true));
                new Sound("ambulance.wav");
            }
            if(!intensityPlayed && (flock.size()+1)%100 == 0) 
                intensityPlayed = true;
            if(totalInfected == 0)
                flock.add(new Boid((int)(Math.random()*WIDTH), (int)(Math.random()*HEIGHT), true));
            else if(totalInfected >= (int)(flock.size()*0.8) && !intensityPlayed) {
                new Sound("intensity.wav");
                intensityPlayed = !intensityPlayed;
            }
            if(totalInfected >= (int)(flock.size()*0.8) && (int)(Math.random()*(recoveryCount+healthyCount)*1000) == 0) {

            }
            if(deathCount >= 100) {
                if(!milestonePlayed && deathCount % 100 == 0) {
                    new Sound("deathmilestone.wav");
                    milestonePlayed = true;
                } else if((deathCount-1)%100 == 0)
                    milestonePlayed = false;
            }
            updateValues();
            for(int i = 0; i < toAdd; i++)
                flock.add(new Boid());
            int more = (int)(Math.random()*((flock.size()>=900) ? 1000 : 500));
            if(more == 0)
                flock.add(new Boid());
            if(addedNewBoid) {
                boolean addInfected = false;
                if(recoveryCount+healthyCount > (int)(flock.size()*0.75))
                    addInfected = true;
                flock.add(new Boid(mouseXPosition, mouseYPosition, addInfected));
                addedNewBoid = false;
            }   
            //updateHealthy();
            this.repaint();
            try {
                Thread.sleep(10);
            } catch( InterruptedException ex ){}
        }
    }

    //clean method available in git history; wipes out all dead at once

    void createLabels() {
        //Healthy
        healthyDisplay = new JLabel("Healthy: "+ healthyCount);
        this.setLayout(new FlowLayout());
        this.add(healthyDisplay);
        healthyDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        healthyDisplay.setForeground(Color.WHITE);
        healthyDisplay.setVisible(true);
        healthyDisplay.setLocation((int)WIDTH/2-400, 200);
        //Infected
        infectedDisplay = new JLabel(" Infected: "+ totalInfected);
        this.setLayout(new FlowLayout());
        this.add(infectedDisplay);
        infectedDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        infectedDisplay.setForeground(Color.RED);
        infectedDisplay.setVisible(true);
        infectedDisplay.setLocation((int)WIDTH/2, 200);
        //Recovered
        recoveredDisplay = new JLabel(" Recovered: "+ criticalCount);
        this.setLayout(new FlowLayout());
        this.add(recoveredDisplay);
        recoveredDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        recoveredDisplay.setForeground(Boid.RECOVERED);
        recoveredDisplay.setVisible(true);
        recoveredDisplay.setLocation((int)WIDTH/2+400, 200);
        //Death
        deathDisplay = new JLabel(" Dead: "+ deathCount);
        this.setLayout(new FlowLayout());
        this.add(deathDisplay);
        deathDisplay.setFont(new Font("Courier New", Font.PLAIN, 20));
        deathDisplay.setForeground(Boid.DEAD);
        deathDisplay.setVisible(true);
        deathDisplay.setLocation((int)WIDTH/2+200, 300);
    }

    static void toggleCounts(boolean setting) {
        healthyDisplay.setVisible(setting);
        infectedDisplay.setVisible(setting);
        recoveredDisplay.setVisible(setting);
        deathDisplay.setVisible(setting);
    }

    static void updateValues() {
        healthyDisplay.setText("Healthy: " + healthyCount);
        infectedDisplay.setText(" Infected: " + totalInfected);
        recoveredDisplay.setText(" Recovered: " + recoveryCount);
        deathDisplay.setText(" Dead: " + deathCount);
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

    static void updateRecovered() {
        recoveryCount++;
        healthyCount++;
        totalInfected--;
        infectedDisplay.setText(" Infected: " + totalInfected);
        recoveredDisplay.setText(" Recovered: " + recoveryCount);
        new Sound("recovery.wav");
    }

    static void updateDead() {
        deathCount++;
        totalInfected--;
        infectedDisplay.setText(" Infected: " + totalInfected);
        deathDisplay.setText(" Dead: " + deathCount);
        new Sound("death.wav");
    }

    static void updateCritical() {
        criticalCount++;
        criticalDisplay.setText(" Critical: " + criticalCount);
    }

    static void updateAlive() {
        aliveCount = flock.size()-deathCount;
        aliveDisplay.setText(" Alive: " + aliveCount);
    }

    static void lostImmunity() {
        recoveryCount--;
        recoveredDisplay.setText(" Recovered: " + recoveryCount);
        new Sound("immunitylost.wav");
    }

    public void keyReleased( KeyEvent event ) {}

    static ArrayList<Boid> addedBoids = new ArrayList<>();

    public void keyPressed( KeyEvent event ) {
        //!General
        if(event.getKeyCode()==KeyEvent.VK_UP) 
            Boid.incrementMaxSpeed();
        else if(event.getKeyCode()==KeyEvent.VK_DOWN)
            Boid.decrementMaxSpeed();
        //!Alignment
        else if(event.getKeyCode()==KeyEvent.VK_S)
            Boid.decrementAlignmentPerceptionRadius();
        else if(event.getKeyCode() == KeyEvent.VK_P)
            Boid.incrementSeparationMaxForce();
        else if(event.getKeyCode() == KeyEvent.VK_SEMICOLON)
            Boid.decrementSeparationMaxForce();
        //!Toggles
        else if(event.getKeyCode() == KeyEvent.VK_Q)
            toggleCounts(true);
        else if(event.getKeyCode() == KeyEvent.VK_E)
            toggleCounts(false);
        else if(event.getKeyCode() == KeyEvent.VK_W)
            Sound.tickOff = !Sound.tickOff;
        //!Sounds
        //Decorative
        else if(event.getKeyCode() == KeyEvent.VK_B)
            new Sound("bell.wav");
        else if(event.getKeyCode() == KeyEvent.VK_N)
            new Sound("ambulance.wav");
        else if(event.getKeyCode() == KeyEvent.VK_SLASH)
            music.stopSong();
        else if(event.getKeyCode() == KeyEvent.VK_PERIOD)
            music = new Sound("plague.wav");
        //Demonstrative
        else if(event.getKeyCode() == KeyEvent.VK_1)
            new Sound("newpatient.wav");
        else if(event.getKeyCode() == KeyEvent.VK_2)
            new Sound("recovery.wav");
        else if(event.getKeyCode() == KeyEvent.VK_3)
            new Sound("immunitylost.wav");
        else if(event.getKeyCode() == KeyEvent.VK_4)
            new Sound("death.wav");
        else if(event.getKeyCode() == KeyEvent.VK_5)
            new Sound("diagnosis.wav");
        else if(event.getKeyCode() == KeyEvent.VK_6)
            new Sound("paranoia.wav");
        else if(event.getKeyCode() == KeyEvent.VK_7)
            new Sound("paranoiaEnded.wav");
        else if(event.getKeyCode() == KeyEvent.VK_8)
            new Sound("treatment.wav");
        else if(event.getKeyCode() == KeyEvent.VK_9)
            new Sound("deathmilestone.wav");
        else if(event.getKeyCode() == KeyEvent.VK_0)
            new Sound("intensity.wav");
        //!Demonstations
        else if(event.getKeyCode() == KeyEvent.VK_BACK_SLASH) { //Clear grid
            new Sound("bell.wav");
            clearGrid = true;
        }
        else if(event.getKeyCode() == KeyEvent.VK_R) { //Add healthy
            new Sound("recovery.wav");
            addedBoids.add(new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), false));
        }
        else if(event.getKeyCode() == KeyEvent.VK_F) { //Add infected
            new Sound("recovery.wav");
            addedBoids.add(new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), true));
        }
        else if(event.getKeyCode() == KeyEvent.VK_T) { //Add recovered
            new Sound("recovery.wav");
            Boid recoveredBoid = new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), false);
            recoveredBoid.isImmune = true;
            recoveredBoid.healthStatus = Boid.RECOVERED;
            recoveredBoid.immunity = recoveredBoid.immunityCap * (Math.random()*50+100);
            recoveredBoid.immunityCap = recoveredBoid.immunity;
            recoveredBoid.immunityLife = recoveredBoid.initialLifeSpan*(6*(Math.random()*0.8+0.5));
            addedBoids.add(recoveredBoid);
        }
        else if(event.getKeyCode() == KeyEvent.VK_G) { //Add dead
            new Sound("recovery.wav");
            Boid deadBoid = new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), false);
            deadBoid.dead = true;
            deadBoid.healthStatus = Boid.DEAD;
            addedBoids.add(deadBoid);
        }
        else if(event.getKeyCode() == KeyEvent.VK_Y) { //Add paramedic
            new Sound("recovery.wav");
            addedBoids.add(new Boid(true));
        }
        else if(event.getKeyCode() == KeyEvent.VK_H) { //Add diagnosed
            new Sound("recovery.wav");
            new Sound("diagnosis.wav");
            Boid diagnosedBoid = new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), true);
            diagnosedBoid.diagnosed = true;
            diagnosedBoid.healthStatus = diagnosedBoid.DIAGNOSED;
            addedBoids.add(diagnosedBoid);
        }
        else if(event.getKeyCode() == KeyEvent.VK_U) { //Add paranoid
            new Sound("recovery.wav");
            Boid paranoidBoid = new Boid((int)((Math.random()*0.2+0.9)*WIDTH/2),(int)((Math.random()*0.2+0.9)*HEIGHT/2), false);
            paranoidBoid.healthStatus = Boid.PARANOID;
            addedBoids.add(paranoidBoid);
        }
    }
    boolean clearGrid = false;

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

    //!Discarded KeyListener statements
        //if(event.getKeyCode() == KeyEvent.VK_Q)
        //    Boid.incrementMaxForce();
        //if(event.getKeyCode() == KeyEvent.VK_A)
        //    Boid.decrementMaxForce();
        //if(event.getKeyCode()==KeyEvent.VK_W) 
        //    Boid.incremementAlignmentPerceptionRadius();
        /*if(event.getKeyCode() == KeyEvent.VK_E)
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
            Boid.decrementSeparationMaxSpeed();*/
}