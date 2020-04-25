package app;
 

//Adopted from StackOverflow

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;
import javax.swing.*;

// To play sound using Clip, the process need to be alive.
// Hence, we use a Swing application.
public class Sound extends JFrame {
    private static final long serialVersionUID = 1L;
    private Clip clip;

    public Sound(String filename) {
        try {
            // Open an audio input stream.
            URL url;
            clip = AudioSystem.getClip();
            url = this.getClass().getClassLoader().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            clip.open(audioIn);
            clip.start();
            clip.loop((filename.equals("plague.wav") || filename.equals("ambience.wav") ? 10 : 0));
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void stopSong(){
        clip.stop();
    }
}