package gui;

import javax.sound.sampled.*;
import java.io.File;

public class ReproductorAudio {
    private Clip clip;

    public void reproducir(String nombreArchivo, float volumenDecibelios) {
        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(archivo);
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Control de Volumen
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(volumenDecibelios);

                clip.loop(Clip.LOOP_CONTINUOUSLY); // Repetir música sin parar
                clip.start();
            } else {
                System.out.println("ERROR: No encuentro el archivo " + nombreArchivo);
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