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
    static boolean tickOff = false;
    private Clip clip;

    public Sound(String filename) {
        if(!tickOff) {
            try {
                // Open an audio input stream.
                URL url;
                clip = AudioSystem.getClip();
                url = this.getClass().getClassLoader().getResource(filename);
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
                clip.open(audioIn);
                if(filename.startsWith("ambulance")) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // set the gain (between 0.0 and 1.0)
                    double gain = 0.1;   
                    float dB = (float)(Math.log(gain) / Math.log(10.0) * 20.0);
                    gainControl.setValue(dB);
                }
                clip.start();
                clip.loop((filename.equals("plague.wav") || filename.equals("ambience.wav") ||
                            filename.startsWith("ambulance")) ? 10 : 0);

            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopSong(){
        clip.stop();
        clip.flush();
    }
}