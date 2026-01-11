package gui;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class EfectosSonido {
	
	public static void reproducir(String ruta, float volumen) {
        new Thread(() -> {
            try {
                File archivo = new File(ruta);
                AudioInputStream audioinpuStream = AudioSystem.getAudioInputStream(archivo);
                Clip clip = AudioSystem.getClip();
                clip.open(audioinpuStream);

                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gain.setValue(volumen);

                clip.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}

