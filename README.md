

<img src="https://media.giphy.com/media/j2jrhbTLQfdd3wjJGG/giphy.gif" height="432" width="800">



# Pandemic Simulator
##### by Ivan Hornung
###### A student at Los Gatos High School
* * *
## About

With COVID-19 terrorizing the world, I decided to create a simulative environment that would demonstrate coronavirus-like conditions and traits. This simulator implements artificial life creatures, Boids, to realistically simulate disease spread. 

One may need to adjust the initial Boid count depending on the device. Found in the `BoidRunner.java` file:

```java
public class BoidRunner extends JPanel implements KeyListener, MouseListener, MouseMotionListener  {
        private static final long serialVersionUID = /**/;

        public static final int BOIDCOUNT = 1200; //CHANGE THIS VALUE

```

>Comments/explanations to the source code to be released soon.

- - -
## Boids
Boids are artificial life objects first developed by [Craig Reynolds in 1986](https://www.red3d.com/cwr/boids/ "Craig Reynolds Boids article") that collectively mimic the behavior of bird flocks or fish schools. They contain three basic steering mechanics:

* Alignment - steer to match the average velocity of its neighboring Boids.
* Cohesion - steer toward the average position of its neighboring Boids.
* Separation - keep a distance from each Boid to prevent crowding.


## Types of Boids
### **Healthy**
<img src="https://i.imgur.com/djAt08a.png" height="163" width="252"> <img src="https://media.giphy.com/media/UU2Y1ChqnALWbN7EtS/giphy.gif" height="163" width="252">

Marked with a **white** color.
* Almost all Boids begin the simulation as healthy. 
* Healthy Boids are susceptible to infection and demonstrate all three steering mechanics (alignment, cohesion, and separation).
* Each healthy Boid has its unique immunity value within a given range.
* If a Boid's immunity level reaches zero, the Boid becomes <span style="color:red">**infected**</span>.
* If a Boid is healthy, it gains immunity from each other healthy Boid within its perception radius by a value that is inversely proportional to the distance between them. This is one of the herd immunity principles in this simulation.
* Altogether, they behave as a peaceful and undisturbed flock.
* Once in a while, a new, healthy, community member joins the grid. New healthy community members are also summoned when a dead Boid's body is cleared.
* Boids do not want to be clustered in groups of larger than 40.
* Can be artificially summoned by pressing the `R` key.



### **<span style="color:red">Infected</span>**
<img src="https://i.imgur.com/4V94NzT.png" height="163" width="252"> <img src="https://media.giphy.com/media/gguLCFZUlUxsWl7BGt/giphy.gif" height="163" width="252">

Marked with a **<span style="color:red">red</span>** color.

* There is always at least one infectant in the simulation.
* Decreases the immunity value of healthy Boids in its perception radius by a value that is inversely proportional to the distance between them.
* An infected Boid behaves just like a healthy Boid steering-wise (infection may be interpreted as a contagious incubation period).
* Healthy Boids cannot tell if a Boid is infected or not.
* Each infected Boid has its lifespan. If the lifespan falls to zero over time, the Boid's fate is decided using real-world COVID-19 statistical death rates.
* If the fate decides death, the Boid undergoes <span style="color:rgb(154, 74, 178);">**death**</span>, else the Boid <span style="color:rgb(101,194,255);">**recovers**</span>.
* An infected Boid has the chance to undergo <span style="color:rgb(134,0,0);">**diagnosis**</span>.
* Can be artificially summoned by pressing the `F` key.


### **<span style="color:rgb(101,194,255);">Recovered</span>**
<img src="https://i.imgur.com/phsxP8P.png" height="163" width="252"> <img src="https://media.giphy.com/media/L2O5XlBb68E4FEnbnc/giphy.gif" height="163" width="252">

Marked with a **<span style="color:rgb(101,194,255);">light blue</span>** color.


* A Boid recovers if there previously were an **<span style="color:red">infected</span>** Boid that survived the 14% mortality when their lifespan reached zero.
* A Boid can also become recovered if they received **<span style="color:blue">paramedic</span>** treatment in time. <!--(given that they were <span style="color:rgb(134,0,0);">diagnosed</span>).-->
* A recovered Boid shares the same steering mechanics as a healthy boid.
* A recovered Boid is immune to the pandemic disease for a relatively long length of time.
* A recovered Boid is not considered to be healthy count-wise.
* Can be artificially summoned by pressing the `T` key.

### **<span style="color:rgb(154, 74, 178);">Dead</span>**
<img src="https://i.imgur.com/DrPGzOc.png" height="163" width="252"> <img src="https://media.giphy.com/media/fZ8tL6AcqlyYkG3Xgh/giphy.gif" height="163" width="252"> <img src="https://media.giphy.com/media/L11x1x4tUj7oPTLJaM/giphy.gif" height="163" width="252">

Marked with a **<span style="color:rgb(154, 74, 178);">purple</span>** color.

* If an **<span style="color:red">**infected**</span>** Boid's infection lifetime reaches zero and undergoes the 14% mortality chance, the Boid then  **<span style="color:rgb(154, 74, 178);">dies</span>**.
* A dead Boid is left to rot on the ground for a calculated period of time.
* A dead Boid is contagious.
* All Boids try to avoid dead Boids (except for on-duty **<span style="color:blue">paramedics</span>**).
* Can be artificially summoned by pressing the `G` key.

### **<span style="color:rgb(134,0,0);">Diagnosed</span>**
<img src="https://i.imgur.com/JuS4vik.png" height="163" width="252"> <img src="https://media.giphy.com/media/iJmMYBWi0HX3P6CxTJ/giphy.gif" height="163" width="252"> <img src="https://media.giphy.com/media/WsM1ZoPUjZTeI73c78/giphy.gif" height="163" width="252"> 

Marked with a <span style="color:rgb(134,0,0);">**dark red**</span> color.
* There is a chance that an **<span style="color:red">**infected**</span>** Boid becomes <span style="color:rgb(134,0,0);">**diagnosed**</span> within its infection lifetime.
* If a Boid is diagnosed, it is publicized to the entire Boid community and all Boids but on-duty **<span style="color:blue">paramedics</span>** avoid the Boid, even **<span style="color:red">**infected**</span>** Boids.
* There is a chance (dependent how large depending on the total current amount of diagnosed Boids in the simulation) that a diagnosed Boid becomes the current patient.
* If the Boid is the current patient, **<span style="color:blue">paramedics</span>** rush to treat it.
* If the Boid is the current patient, it signals a **blinking** effect.
* If the diagnosed Boid is treated by **<span style="color:blue">paramedics</span>**, it becomes a <span style="color:rgb(101,194,255);">**recovered**</span> Boid.
* A diagnosed Boid tries to reach toward any rushing paramedics, even if the Boid is not the patient.
* Can be artificially summoned by pressing the `H` key.

### **<span style="color:blue;">Paramedic</span>**
<img src="https://i.imgur.com/PJMrO2o.png" height="163" width="252"> <img src="https://media.giphy.com/media/J2hBYVHwAasgX7Mghy/giphy.gif" height="163" width="252"> <img src="https://media.giphy.com/media/WsM1ZoPUjZTeI73c78/giphy.gif" height="163" width="252"> <img src="https://media.giphy.com/media/UriDtbghES4E4AGCvI/giphy.gif" height="163" width="252"> 

Marked with a **<span style="color:blue">dark blue</span>** color.

* There are always at least 3 paramedics on the grid while there a diagnosed Boid present.
* Paramedics behave like normal Boids until one person in the community becomes diagnosed.
* If one person is diagnosed, the paramedics change to an emergency state, where they turn on their sirens and rush to the position of the patient in need.
* When paramedics are in an emergency state, they do not undergo any form of separational steering from other Boids (including dead).
* The longer the distance the patient is from the paramedics, the more acceleration they give to reach the patient in time.
* Paramedics decelerate significantly when in close contact with the patient.
* Boids surrounding a paramedic in an emergency state attempt to make way for the paramedic to pass.
* If the paramedic is in close contact with the patient, the patient undergoes the curing process, and will most likely be treated and recover. The more paramedics in immediate contact with the patient, the faster the curing process takes. 
* If the patient gets cured, a new patient is chosen, assuming there are more diagnosed Boids on the grid.
* While paramedics have significantly high immunity values relative to normal Boids, they are never immune to the disease and can end up becoming infected.
* Paramedics create a "pop effect" after curing a patient.
* More and more paramedics are called depending on the infection/noninfection ratio.
* Paramedics are slowly removed if the proportion to infected/noninfected is less.
* Can be artificially summoned by pressing the `Y` key.

### **<span style="color:rgb(174,243,177);">Paranoid</span>**
<img src="https://i.imgur.com/XpRqPeT.png" height="163" width="252"> <img src="https://media.giphy.com/media/fWBX99ymYMuewi2M8x/giphy.gif" height="163" width="252">

Marked with a **<span style="color:rgb(174,243,177);">pastel green</span>** color.

* A Boid becomes **<span style="color:rgb(174,243,177);">paranoid</span>** by a random chance that increases as more total boids are infected relative to the total community size.
* Once a Boid becomes paranoid, the only way it can lose the paranoia is to undergo infection and recovery
* If a Boid is **<span style="color:rgb(174,243,177);">paranoid</span>**, it loses all trust in everybody and attempts to distance itself from every Boid as if they were all dead bodies.
* aka *the social distancing Boid*.
* Can be artificially summoned by pressing the `U` key.

---
## Additional Features

### **Keybindings**

| Key/Button | Action |
|:---:|:---:|
|*`Click`*|adds a new *healthy* Boid to the click location|
|`;`|decreases the separation force between Boids <sup>1</sup>|
|`P`|increases the separation force between Boids <sup>2</sup>|
|`\`|clears the grid of all Boids<sup>3</sup>|
|`E`|hides the counter|
|`Q`|shows the counter|
|`R`|artificially summons a *healthy* Boid|
|`F`|artificially summons an *infected* Boid|
|`T`|artificially summons a *recovered* Boid|
|`G`|artificially summons a *dead* Boid|
|`H`|artificially summons a *diagnosed* Boid|
|`Y`|artificially summons a *paramedic* Boid|
|`U`|artificially summons a *paranoid* Boid|

###### **1**: can be used to simulate public gatherings.
###### **2**: can be used to simulate social distancing.
###### **3**: made to be a demonstration/testing environment. (Note: there is always at least one infectant, and Boid spawn naturally).

### **Music and Sounds**
| Key/Button | Action |
|:---:|:---:|
|`/`| stops the main music (`plague.wav`)|
|`.`| restarts the main music <sup>1</sup> (`plague.wav`)|
|`W`|turns off sound effects listed below|
|`1`|triggers `newpatient.wav` to play|
|`2`| triggers `recovery.wav` to play|
|`4`| triggers `death.wav` to play|
|`3`| triggers `immunitylost.wav` to play|
|`5`| triggers `diagnosis.wav` to play|
|`6`| triggers `paranoia.wav` to play|
|`7`| triggers `paranoiaEnded.wav` to play|
|`8`| triggers `treatment.wav` to play|
|`9`| triggers `deathmilestone.wav` to play|
|`0`| triggers `intensity.wav` to play|
|`B`| triggers `bell.wav` to play|
|`N`| triggers `ambulance.wav` to play|


###### **1**: *only do this if you have turned off the music already, otherwise there will be two tracks playing simultaneously*

### **Sound Meanings**

`plague.wav` plays when the simulation initiates or when the  ` . ` key is pushed.

`newpatient.wav` plays when a healthy Boid becomes *infected*.

`recovery.wav` plays when an *infected* Boid *recovers*.

`immunitylost.wav` plays when a *recovered* Boid's immunity wears off, thus turning into a *healthy* boid.

`death.wav` plays when an *infected* Boid *dies*.

`diagnosis.wav` plays when an *infected* Boid becomes *diagnosed*.

`paranoia.wav` plays when a *healthy* Boid becomes *paranoid*.

`paranoiaEnded.wav` plays when a *paranoid* Boid becomes *infected*.

`treatment.wav` plays when a *diagnosed* Boid receives paramedic treatment and becomes *recovered*.

`deathmilestone.wav` plays whenever the death count reaches a multiple of 100.

`intensity.wav` plays when more than 80% of the Boids are *infected*.

`bell.wav` plays when a *paramedic* gets *infected*, or when all Boids on the grid are removed via the `\` key.

`ambulance.wav`, `ambulance2.wav`, `ambulance3.wav` play whenever *paramedics* turn on their sirens to treat a *diagnosed* Boid.
