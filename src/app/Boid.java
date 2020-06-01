package app;
 

import java.awt.*;
import java.util.ArrayList;
import java.awt.geom.*;

public class Boid {
    Vector position;
    Vector velocity;
    Vector acceleration;

    static int fieldOfView = 120;

    static double size = .6;
    private Path2D shape = new Path2D.Double();
    /*static {
        shape.moveTo(0,-size*2);
        shape.lineTo(-size, size*2);
        shape.lineTo(size,size*2);
        shape.closePath();
    }*/
    private void reDraw(){
        shape = new Path2D.Double();
        shape.moveTo(0,-size*2);
        shape.lineTo(-size, size*2);
        shape.lineTo(size,size*2);
        shape.closePath();
    }
    
    boolean hasDisease = false;
    Color healthStatus = HEALTHY;
    double immunity = (Math.random()*5+2.5);
    double immunityCap = immunity, initialImmunity = immunity;
    double lifeSpan = (Math.random()*300+500)*2;
    double initialLifeSpan = lifeSpan;
    boolean dead = false;//, diagnosed = false;
    double deathAngle = 0;
    static int mortalityRate = 14;
    static Color /*RECOVERED = new Color(101,194,255),*/ DEAD = new Color(154, 74, 178), 
                HEALTHY = Color.WHITE, INFECTED = Color.RED;//,  PARANOID = new Color(174,243,177);
    //Color PARAMEDIC = Color.BLUE, DIAGNOSED = new Color(134, 0 , 0);
    double immunityLife;
    boolean isImmune = false;//, isParamedic = false;
    //static Boid patient = null; static boolean lockedOn = false;
    double healTime = this.initialImmunity;
    int sirens = 0, sirenCount = 0;
    static int travelTime = 0;
    static int patientBlink = 0, patientBlinkCount = 0;
    static Sound siren = null;

    public Boid() {
        reDraw();
        /*if(!hasInfected) {
            healthStatus = INFECTED;
            hasInfected = true;
            hasDisease = true;
            lifeSpan = 2000;
        }*/
        this.position = new Vector((double)(Math.random()*BoidRunner.WIDTH),(double)(Math.random()*BoidRunner.HEIGHT));
        double angle = Math.random()*360;
        double radius = Math.random()*2+2; //2-4
        this.velocity = new Vector((radius * Math.cos(angle)), (radius * Math.sin(angle)));
        this.acceleration = new Vector(0,0);
    }
    
    public Boid(int mouseXPosition, int mouseYPosition, boolean addedInfected) {
        reDraw();
        if(addedInfected) {
            healthStatus = INFECTED;
            hasDisease = true;
        }
        this.position = new Vector(mouseXPosition, mouseYPosition);
        double angle = Math.random()*360;
        double radius = Math.random()*2+2;
        this.velocity = new Vector((radius * Math.cos(angle)), (radius * Math.sin(angle)));
        this.acceleration = new Vector(0,0);
        if(BoidRunner.totalInfected == 1)
            this.lifeSpan = 12000;
    }
    public Boid(boolean addedParamedic) {
        reDraw();
        this.position = new Vector((int)(BoidRunner.WIDTH), (int)(BoidRunner.HEIGHT));
        double angle = Math.random()*360;
        double radius = Math.random()*2+2;
        this.velocity = new Vector((radius * Math.cos(angle)), (radius * Math.sin(angle)));
        this.acceleration = new Vector(0,0);
    }

    Vector align(ArrayList<Boid> flock) {
        int perceptionRadius = (int)(alignmentPerceptionRadius);
        int total = 0;
        Vector steering = new Vector(0,0);
        //Part 2: Lifespans
        if(this.hasDisease && !this.dead && !this.isImmune) {
            lifeSpan--;
            if(lifeSpan <= 0) {
                if((int)(Math.random()*100) < mortalityRate) {
                    this.dead = true; //Death
                    BoidRunner.updateDead();
                    this.healthStatus = DEAD;
                } else {
                    this.hasDisease = false; //Recovery
                    this.isImmune = true;
                    new Sound("recovery.wav");
                    this.immunity = this.immunityCap * (Math.random()*50+100);
                    this.immunityCap = this.immunity;
                    this.immunityLife = initialLifeSpan*(6*(Math.random()*0.8+0.5));
                }
            }
        } else if(this.isImmune) { //Immunity loss
            this.immunityLife--;
            if(this.immunityLife < 0) {
                this.isImmune = false;
                this.healthStatus = HEALTHY;
                this.immunity = this.initialImmunity*(Math.random()*0.8+0.4);
                this.immunityCap = this.immunity;
                this.immunityLife = initialLifeSpan*(6*(Math.random()*0.8+0.5));
                this.lifeSpan = this.initialLifeSpan;
                new Sound("immunitylost.wav");
            }
        } //Alignment
        for(int i = 0; i < flock.size(); i++) {
            double dist = distance(this.position.xvalue, this.position.yvalue, flock.get(i).position.xvalue, flock.get(i).position.yvalue);
            if(flock.get(i) != this && dist < perceptionRadius) {
                    steering.add(flock.get(i).velocity);
                    total++;
                //!Viral transmission
                if(this.hasDisease && !flock.get(i).hasDisease && (!this.isImmune || flock.get(i).dead)) {
                    if(flock.get(i).immunity <= 0) {
                        flock.get(i).healthStatus = INFECTED; //!Infection
                        new Sound("newpatient.wav");
                        flock.get(i).hasDisease = true;
                    }
                    else {//!Immunity loss
                        flock.get(i).immunity -= (1/dist)*((BoidRunner.totalInfected > 35) ? 1 : ((BoidRunner.totalInfected > 11) 
                                                 ? 2.5 : ((BoidRunner.totalInfected < 5) ? (BoidRunner.totalInfected < 2 ? 5: 4) : 3.5)));
                    }
                } else if(!this.hasDisease && !flock.get(i).hasDisease && flock.get(i).immunity < flock.get(i).immunityCap && !flock.get(i).isImmune) {
                    flock.get(i).immunity += (Math.random()*5+1)/((BoidRunner.totalInfected > 35) ? 10000 : 100);
                    if(flock.get(i).immunity > flock.get(i).immunityCap)
                       flock.get(i).immunity = flock.get(i).immunityCap; //!Immunity gain
                }
            }
        }
        if(total > 0) {
            if(total > 0)
                steering.divide((double)total);
            steering.setMagnitude(maxSpeed);
            steering.subtract(this.velocity);
            steering.limit(maxForce);
        }
        return steering;
    }

    Vector cohesion(ArrayList<Boid> flock) {
        int perceptionRadius = (int)(cohesionPerceptionRadius);
        int total = 0;
        Vector steering = new Vector(0,0);
            for(Boid boid : flock) {
                double dist = distance(this.position.xvalue, this.position.yvalue, boid.position.xvalue, boid.position.yvalue);
                if(boid != this && dist < perceptionRadius) {
                    steering.add(boid.position);
                    total++;
                }
            }
        if((total > 0 )) {
                steering.divide((double)total);
            steering.subtract(this.position);
            steering.setMagnitude(maxSpeed);
            steering.subtract(this.velocity);
            steering.limit(maxForce);
        }
        return steering;
    }

    Vector separation(ArrayList<Boid> flock) {
        int perceptionRadius = (int)separationPerceptionRadius;
        int total = 0;
        Vector steering = new Vector(0,0);
        for(Boid boid : flock) {
            double dist = distance(this.position.xvalue, this.position.yvalue, boid.position.xvalue, boid.position.yvalue);
            if(boid != this && dist < perceptionRadius) {
                Vector difference = new Vector(this.position.xvalue, this.position.yvalue);
                difference.subtract(boid.position);
                if(dist == 0.0) dist += 0.001;
                difference.divide(dist*dist);
                steering.add(difference);
                total++;
            }
        }
        if(total > 0) {
            steering.divide((double)total);
            steering.setMagnitude(((total > 40) ? separationMaxSpeed :separationMaxSpeed));
            steering.subtract(this.velocity);
            steering.setMagnitude(((total > 40) ? separationMaxForce*1.5 :separationMaxForce));
        }
        return steering;
    }

    void flock(ArrayList<Boid> flock) {
        this.acceleration.set(0, 0);
        Vector alignment = this.align(flock);
        Vector cohesion = this.cohesion(flock);
        Vector separation = this.separation(flock);
        //Force accumulation:
        this.acceleration.add(alignment);
        this.acceleration.add(separation);
        this.acceleration.add(cohesion);
    }

    double patientDistance;

    void update() {
        if(!this.dead) {
            /*if(this.isParamedic && lockedOn && patientDistance >= 10) {
                if((int)(Math.random()*BoidRunner.paramedicCount) == 0) //since travelTime is static and you only want to increase it by
                     Boid.travelTime++;       //about one every cycle, have it be a 1/paramedicCount chance for the traveltime to increase
                Vector emergencyVelocity = this.velocity.setMagnitude(
                    this.velocity.getMagnitude()*2+((Boid.travelTime > 20)?Boid.travelTime/200:1));
                this.position.add(emergencyVelocity); 
            }
            else*/
                this.position.add(this.velocity);
        }
        this.velocity.add(this.acceleration);
        this.velocity.limit(maxSpeed);
        if(this.dead && deathAngle == 0) {
            deathAngle = this.velocity.dir() + Math.PI/2;
        }
        /*if(patient == this && lockedOn) {
            patientBlinkCount++;
            if(patientBlinkCount % 4 == 0) {
                patientBlink++;
                switch(patientBlink) {
                    case 0 :
                        this.DIAGNOSED = new Color(252, 52, 52);
                        break;
                    case 1 :
                        this.DIAGNOSED = new Color(134, 0 , 0);
                        break;
                } patient.healthStatus = this.DIAGNOSED;
                if(patientBlink > 1) patientBlink = -1;
            }
        }*/
        //Ensures that paramedics do not treat a diagnosed Boid turned dead
        /*if(this.isParamedic && lockedOn && patient.dead) {
            patient.diagnosed = false;
            siren.stopSong();
            siren = null;
            lockedOn = false;
            patient = null;
            Boid.travelTime = 0;
        }*/
    }

    void edges() {
        if(this.position.xvalue > BoidRunner.WIDTH)
            this.position.xvalue = 0;
        else if(this.position.xvalue < 0)
            this.position.xvalue = BoidRunner.WIDTH;
        
        if(this.position.yvalue > BoidRunner.HEIGHT)
            this.position.yvalue = 0;
        else if(this.position.yvalue < 0)
            this.position.yvalue = BoidRunner.HEIGHT;
    }

    double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow((x2-x1), 2) + Math.pow((y2-y1), 2));
    }

    public void draw(Graphics2D g) {
        AffineTransform save = g.getTransform();
        g.translate((int)this.position.xvalue, (int)this.position.yvalue);
        if(!this.dead)
            g.rotate(this.velocity.dir() + Math.PI/2);
        else
            g.rotate(deathAngle);
        g.setColor(healthStatus);
        if(BoidRunner.timeElapse>=65000 && size < 3 )
            reDraw();
        g.fill(shape);
        g.draw(shape);
        g.setTransform(save);
    }

    public static void pause() {
        try{
            Thread.sleep(3000);
        } catch(InterruptedException e) {}
    }

    static double maxForce = 0.2;
    static double maxSpeed = 2;

    static final double forceChangeValue = 1;

    static double alignmentPerceptionRadius = 50;
    static double cohesionPerceptionRadius = 100;
    static double separationPerceptionRadius = 100;
    static double separationMaxSpeed = maxSpeed;
    static double separationMaxForce = 0.175;
    
    static void decrementMaxSpeed() { Boid.maxSpeed -= 0.05;}
    static void incrementMaxSpeed() { Boid.maxSpeed += 0.05;}
    static void incrementSeparationMaxForce() { Boid.separationMaxForce += forceChangeValue; }
    static void decrementSeparationMaxForce() { Boid.separationMaxForce -= forceChangeValue; }
}