# Pandemic Simulator
##### by Ivan Hornung
* * *
## About
>Work in progress
- - -
## Boids
Boids are artificial life objects first developed by [Craig Reynolds in 1986](https://www.red3d.com/cwr/boids/ "Craig Reynolds Boids article") that mimic the behavior of bird flocks or fish schools.
> Work in progress 

## Types of Boids
### **Healthy**
<img src="https://i.imgur.com/djAt08a.png" height="163" width="252">

Marked with a **white** color.
* Almost all Boids begin the simulation as healthy. 
* Healthy Boids are susceptible to infection and demonstrate all three steering mechanics (alignment, cohesion, and separation).
* Each healthy Boid has its own unique immunity value within a given range.
* If a Boid's immunity level reaches zero, the Boid becomes <span style="color:red">**infected**</span>.
* If a Boid is healthy, it gains immunity from each other healthy Boid within its perception radius by a value that is inversely proportional to the distance between them.
* All together, they behave as a peaceful and undisturbed flock.

<img src="https://media.giphy.com/media/UU2Y1ChqnALWbN7EtS/giphy.gif" height="163" width="252">




### **<span style="color:red">Infected</span>**
<img src="https://i.imgur.com/4V94NzT.png" height="163" width="252">

Marked with a **<span style="color:red">red</span>** color.

* There is always at least one infectant in the simulation.
* Decreases the immunity value of healthy Boids in its perception radius by a value that is inversely proportional to the distance between them.
* An infected Boid behaves just like a healthy Boid steering-wise.
* Healthy Boids cannot tell if a Boid is infected or not.
* Each infected Boid has its own lifespan. If the lifespan falls to zero over time, the Boid's fate is decided using real-world COVID-19 statistical death rates.
* If the fate decides death, the Boid undergoes <span style="color:rgb(154, 74, 178);">**death**</span>, else the Boid <span style="color:rgb(101,194,255);">**recovers**</span>.
* An infected Boid has the chance to undergo <span style="color:rgb(134,0,0);">**diagnosis**</span>.

<img src="https://media.giphy.com/media/gguLCFZUlUxsWl7BGt/giphy.gif" height="163" width="252">

### **<span style="color:rgb(101,194,255);">Recovered</span>**
<img src="https://i.imgur.com/phsxP8P.png" height="163" width="252">

Marked with a **<span style="color:rgb(101,194,255);">light blue</span>** color.


* A Boid recovers if there previously were an **<span style="color:red">infected</span>** Boid that survived the 14% mortality when their lifespan reached zero.
* A Boid can also become recovered if they received **<span style="color:blue">paramedic</span>** treatment in time. <!--(given that they were <span style="color:rgb(134,0,0);">diagnosed</span>).-->
* A recovered Boid shares the same steering mechanics as a healthy boid.