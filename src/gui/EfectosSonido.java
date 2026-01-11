package gui;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;

public class EfectosSonido {
	
	public static void reproducir(String ruta, float volumen) {
        new Thread(() -> {
            try {
                InputStream audioSrc = obtenerStreamRecurso(ruta);
                
                if (audioSrc != null) {
                    // BufferedInputStream es vital para JARs
                    InputStream bufferedIn = new BufferedInputStream(audioSrc);
                    AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
                    
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioInputStream);

                    FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    gain.setValue(volumen);

                    clip.start();
                } else {
                    System.err.println("❌ Efecto no encontrado: " + ruta);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
	
    // Método auxiliar reutilizado para buscar en múltiples rutas
    private static InputStream obtenerStreamRecurso(String rutaOriginal) {
        String[] intentos = {
            rutaOriginal,
            "/" + rutaOriginal,
            rutaOriginal.replace("resources/", ""),
            "/" + rutaOriginal.replace("resources/", "")
        };

        for (String ruta : intentos) {
            // Nota: en métodos estáticos usamos EfectosSonido.class
            InputStream is = EfectosSonido.class.getResourceAsStream(ruta);
            if (is == null) {
                 is = EfectosSonido.class.getClassLoader().getResourceAsStream(ruta);
            }
            
            if (is != null) return is;
        }
        return null;
    }
}

