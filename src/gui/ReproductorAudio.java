package gui;

import javax.sound.sampled.*;
import java.net.URL;

public class ReproductorAudio {
    private Clip clip;

    public void reproducir(String rutaRelativa, float volumenDecibelios) {
        try {
           
            
            // Usamos getResource en lugar de File
            URL url = getClass().getResource("/" + rutaRelativa); 
            
            if (url == null) {
                // Intento alternativo por si la ruta ya venía con "/"
                url = getClass().getResource(rutaRelativa);
            }

            if (url != null) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volumenDecibelios);

                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } else {
                System.out.println("ERROR CRÍTICO: No encuentro el audio en el JAR: " + rutaRelativa);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}