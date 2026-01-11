package gui;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class ReproductorAudio {
    private Clip clip;

    public void reproducir(String ruta, float volumenDecibelios) {
        try {
            // 1. Intentamos obtener el stream de audio de forma robusta para JAR
            InputStream audioSrc = obtenerStreamRecurso(ruta);

            if (audioSrc == null) {
                System.err.println("❌ ERROR: No se pudo encontrar el audio en ninguna ruta: " + ruta);
                return;
            }

           
            // IMPORTANTE: Los archivos dentro de un JAR a veces no soportan mark/reset sin esto.
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            // 3. Control de volumen
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(volumenDecibelios);

            clip.loop(Clip.LOOP_CONTINUOUSLY); 
            clip.start();
            
        } catch (Exception e) {
            System.err.println("Error reproduciendo audio: " + ruta);
            e.printStackTrace();
        }
    }

    public void detener() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    /**
     * Busca el recurso en varias rutas posibles para evitar errores de "Source Folder".
     */
    private InputStream obtenerStreamRecurso(String rutaOriginal) {
        // Lista de posibles prefijos para probar
        String[] intentos = {
            rutaOriginal,                          // Tal cual se pasó (ej: "resources/sounds/...")
            "/" + rutaOriginal,                    // Con barra inicial
            rutaOriginal.replace("resources/", ""), // Sin la carpeta resources (si es Source Folder)
            "/" + rutaOriginal.replace("resources/", "") // Sin resources y con barra
        };

        for (String ruta : intentos) {
            // Intentamos cargar
            InputStream is = getClass().getClassLoader().getResourceAsStream(ruta);
            if (is == null) {
                // Intento alternativo con la clase
                is = getClass().getResourceAsStream(ruta);
            }
            
            // Si encontramos algo, devolvemos eso
            if (is != null) {
                // System.out.println("✅ Audio encontrado en: " + ruta); // Descomentar para depurar
                return is;
            }
        }
        return null;
    }
}