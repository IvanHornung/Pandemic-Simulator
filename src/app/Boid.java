package app;
 

import java.awt.*;
import java.util.ArrayList;
import java.awt.geom.*;

public class Boid {
    Vector position;
    Vector velocity;
    Vector acceleration;

    static int fieldOfView = 120;

    static int size = 3;
    static Path2D shape = new Path2D.Double();
    static {
        shape.moveTo(0,-size*2);
        shape.lineTo(-size, size*2);
        shape.lineTo(size,size*2);
        shape.closePath();
    }
    
    static boolean hasInfected = false;
    boolean hasDisease = false;
    Color healthStatus = HEALTHY;
    double immunity = (Math.random()*10+5);
    double immunityCap = immunity, initialImmunity = immunity;
    double lifeSpan = (Math.random()*300+500)*2;
    double initialLifeSpan = lifeSpan;
    boolean dead = false;
    double deathAngle = 0;
    static int mortalityRate = 14;
    static Color RECOVERED = new Color(101,194,255), DEAD = new Color(154, 74, 178), 
                HEALTHY = Color.WHITE, INFECTED = Color.RED, DIAGNOSED = new Color(134, 0 , 0);
    double immunityLife;
    boolean isImmune = false;

    public Boid() {
        if(!hasInfected) {
            healthStatus = INFECTED;
            hasInfected = true;
            hasDisease = true;
            lifeSpan = 2000;
        }
        this.position = new Vector((double)(Math.random()*BoidRunner.WIDTH),(double)(Math.random()*BoidRunner.HEIGHT));
        double angle = Math.random()*360;
        double radius = Math.random()*2+2; //2-4
        this.velocity = new Vector((radius * Math.cos(angle)), (radius * Math.sin(angle)));
        this.acceleration = new Vector(0,0);
    }
    
    public Boid(int mouseXPosition, int mouseYPosition, boolean addedInfected) {
        if(addedInfected) {
            healthStatus = INFECTED;
            hasInfected = true;
            hasDisease = true;
        }
        this.position = new Vector(mouseXPosition, mouseYPosition);
        double angle = Math.random()*360;
        double radius = Math.random()*2+2; //2-4
        this.velocity = new Vector((radius * Math.cos(angle)), (radius * Math.sin(angle)));
        this.acceleration = new Vector(0,0);
        if(BoidRunner.totalInfected == 1)
            this.lifeSpan = 12000;
    }

    Vector align(ArrayList<Boid> flock) {
        int perceptionRadius = (int)alignmentPerceptionRadius; //(alignmentPerceptionRadius == 50) ? 50 : (int)alignmentPerceptionRadius;
        int total = 0;
        Vector steering = new Vector(0,0);
        //!Part 2: Lifespans
        if(this.hasDisease && !this.dead && !this.isImmune) {
            lifeSpan--;
            if(lifeSpan <= 0) {
                if((int)(Math.random()*100) < mortalityRate) {
                    this.dead = true; //!Death
                    BoidRunner.updateDead();
                    //new Sound("death.wav");
                    this.healthStatus = DEAD;
                } else {
                    this.hasDisease = false; //!Recovery
                    this.isImmune = true;
                    //BoidRunner.updateRecovered();
                    new Sound("recovery.wav");
                    this.healthStatus = RECOVERED;
                    this.immunity = this.immunityCap * (Math.random()*50+100);
                    this.immunityCap = this.immunity;
                    this.immunityLife = initialLifeSpan*(6*(Math.random()*0.8+0.5)); //12*..
                }
            }
        } else if(this.isImmune && !this.hasDisease) { //!Immunity loss
            this.immunityLife--;
            if(this.immunityLife < 0) {
                this.isImmune = false;
                this.healthStatus = HEALTHY;
                this.immunity = this.initialImmunity*(Math.random()*0.8+0.4);
                this.immunityCap = this.immunity;
                this.immunityLife = initialLifeSpan*(6*(Math.random()*0.8+0.5));
                //BoidRunner.lostImmunity();
                new Sound("immunitylost.wav");
            }
        }
        for(int i = 0; i < flock.size(); i++) {
            double dist = distance(this.position.xvalue, this.position.yvalue, flock.get(i).position.xvalue, flock.get(i).position.yvalue);
            if(flock.get(i) != this && dist < perceptionRadius) {
                steering.add(flock.get(i).velocity);
                total++;
                //!Viral transmission
                if(this.hasDisease && !flock.get(i).hasDisease && (!this.isImmune || flock.get(i).dead)) {
                    if(flock.get(i).immunity <= 0) {
                        flock.get(i).healthStatus = INFECTED; //!Infection
                        //BoidRunner.updateInfected();
                        new Sound("newpatient.wav");
                        flock.get(i).hasDisease = true;
                    }
                    else {//!Immunity loss
                        if((int)(Math.random()*40000)==0) {
                            this.healthStatus = DIAGNOSED; //!Diagnosis
                            new Sound("diagnosis.wav");
                        }
                        flock.get(i).immunity -= (1/dist)*( );
                    }
                } else if(!this.hasDisease && !flock.get(i).hasDisease && flock.get(i).immunity < flock.get(i).immunityCap && !flock.get(i).isImmune) {
                    flock.get(i).immunity += (Math.random()*5+1)/((BoidRunner.totalInfected > 35) ? 10000 : 100);
                    if(flock.get(i).immunity > flock.get(i).immunityCap)
                       flock.get(i).immunity = flock.get(i).immunityCap; //!Immunity gain
                }
            }
            }
        if(total > 0) {
            steering.divide((double)total);
            steering.setMagnitude(((alignmentMaxSpeed != maxSpeed) ? alignmentMaxSpeed : maxSpeed));
            steering.subtract(this.velocity);
            steering.limit(((alignmentMaxForce != maxForce) ? alignmentMaxForce : maxForce));
        }
        return steering;
    }

    Vector cohesion(ArrayList<Boid> flock) {
        int perceptionRadius = (int)cohesionPerceptionRadius;
        int total = 0;
        Vector steering = new Vector(0,0);
        for(Boid boid : flock) {
            double dist = distance(this.position.xvalue, this.position.yvalue, boid.position.xvalue, boid.position.yvalue);
            if(boid != this && dist < perceptionRadius) {
                steering.add(boid.position);
                total++;
            }
        }
        if(total > 0) {
            steering.divide((double)total);
            steering.subtract(this.position);
            steering.setMagnitude(((cohesionMaxSpeed != maxSpeed) ? cohesionMaxSpeed : maxSpeed));
            steering.subtract(this.velocity);
            steering.limit(((cohesionMaxForce != maxForce) ? cohesionMaxForce : maxForce));
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
                difference.divide(dist*dist); //or *1/x; inverselly proportional
                if(boid.dead || boid.healthStatus == DIAGNOSED) {
                    difference.multiply(Math.random()*5+20);
                }
                steering.add(difference);
                //Implementing FOV would go here, check Git history to access
                total++;
            }
        }
        if(total > 0) {
            steering.divide((double)total);
            steering.setMagnitude(((total > 40) ? separationMaxSpeed*2 : separationMaxSpeed));
            steering.subtract(this.velocity);
            steering.limit(((total > 40) ? separationMaxForce*2 : separationMaxForce));
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
        this.acceleration.add(cohesion);
        this.acceleration.add(separation);
    }

    void update() {
        if(!this.dead) 
            this.position.add(this.velocity);
        this.velocity.add(this.acceleration);
        this.velocity.limit(maxSpeed);
        if(this.dead && deathAngle == 0) {
            deathAngle = this.velocity.dir() + Math.PI/2;
        }
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
        g.fill(shape);
        g.draw(shape);
        g.setTransform(save);
    }

    //!MODIFICATIONS///////////////////////////////
    static double maxForce = 0.2;
    static double maxSpeed = 2;

    static final double speedChangeValue = 10; //0.1
    static final double forceChangeValue = 1; //0.05
    static final double perceptionRadiusChangeValue = 100; //1

    static double alignmentPerceptionRadius = 50;
    static double alignmentMaxSpeed = maxSpeed;
    static double alignmentMaxForce = maxForce;
    static double cohesionPerceptionRadius = 100;
    static double cohesionMaxSpeed = maxSpeed;
    static double cohesionMaxForce = maxForce;
    static double separationPerceptionRadius = 100;
    static double separationMaxSpeed = maxSpeed;
    static double separationMaxForce = maxForce;

    //!General modifications
    static void incrementMaxSpeed() { Boid.maxSpeed += speedChangeValue; }
    static void decrementMaxSpeed() { Boid.maxSpeed -= speedChangeValue; }
    static void incrementMaxForce() { Boid.maxForce += forceChangeValue; }
    static void decrementMaxForce() { Boid.maxForce -= forceChangeValue; }
    //!Alignment modifications
    static void incremementAlignmentPerceptionRadius() { Boid.alignmentPerceptionRadius -= perceptionRadiusChangeValue; }
    static void decrementAlignmentPerceptionRadius() { Boid.alignmentPerceptionRadius -= perceptionRadiusChangeValue; }
    static void incrementAlignmentMaxSpeed() { Boid.alignmentMaxSpeed += speedChangeValue; }
    static void decrementAlignmentMaxSpeed() { Boid.alignmentMaxSpeed -= speedChangeValue; }
    static void incrementAlignmentMaxForce() { Boid.alignmentMaxForce += forceChangeValue; }
    static void decrementAlignmentMaxForce() { Boid.alignmentMaxForce -= forceChangeValue; }
    //!Cohesion modifications
    static void incremementCohesionPerceptionRadius() { Boid.cohesionPerceptionRadius -= perceptionRadiusChangeValue; }
    static void decrementCohesionPerceptionRadius() { Boid.cohesionPerceptionRadius -= perceptionRadiusChangeValue; }
    static void incrementCohesionMaxSpeed() { Boid.cohesionMaxSpeed += speedChangeValue; }
    static void decrementCohesionMaxSpeed() { Boid.cohesionMaxSpeed -= speedChangeValue; }
    static void incrementCohesionMaxForce() { Boid.cohesionMaxForce += forceChangeValue; }
    static void decrementCohesionMaxForce() { Boid.cohesionMaxForce -= forceChangeValue; }
    //!Separation modifications
    static void incremementSeparationPerceptionRadius() { Boid.separationPerceptionRadius -= perceptionRadiusChangeValue; }
    static void decrementSeparationPerceptionRadius() { Boid.separationPerceptionRadius -= perceptionRadiusChangeValue; }
    static void incrementSeparationMaxSpeed() { Boid.separationMaxSpeed += speedChangeValue; }
    static void decrementSeparationMaxSpeed() { Boid.separationMaxSpeed -= speedChangeValue; }
    static void incrementSeparationMaxForce() { Boid.separationMaxForce += forceChangeValue; }
    static void decrementSeparationMaxForce() { Boid.separationMaxForce -= forceChangeValue; }
}