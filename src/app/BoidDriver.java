package app;

import javax.swing.*;
import java.awt.*;
public class BoidDriver {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Flocking Simulation");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setLocation(0, 0);
        frame.setPreferredSize(new Dimension(1920, 1080));
        BoidRunner simulation = new BoidRunner();
        frame.setResizable(false);
        frame.add(simulation);
        frame.pack();
        frame.setVisible(true);
        simulation.run();
    }
}
