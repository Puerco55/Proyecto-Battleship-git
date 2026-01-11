package gui;

import java.net.URL;
import javax.sound.sampled.*;

public class EfectosSonido {
	
	public static void reproducir(String ruta, float volumen) {
        new Thread(() -> {
            try {
                // Adaptación para JAR
                URL url = EfectosSonido.class.getResource("/" + ruta);
                
                if (url == null) {
                     url = EfectosSonido.class.getResource(ruta);
                }

                if (url != null) {
                    AudioInputStream audioinpuStream = AudioSystem.getAudioInputStream(url);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioinpuStream);

                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(volumen);

                    clip.start();
                } else {
                    System.err.println("Audio no encontrado: " + ruta);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

