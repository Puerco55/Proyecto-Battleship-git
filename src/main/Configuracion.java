package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuracion {
    private static Properties properties = new Properties();

    static {
        try {
            // Carga el archivo de configuración desde la carpeta 'conf'
            properties.load(new FileInputStream("conf/config.properties"));
        } catch (IOException e) {
            System.err.println("AVISO: No se encontró 'conf/config.properties'. Usando valores por defecto.");
        }
    }

    public static String getDbUrl() {
        // Por defecto usa la ruta resources/db si no encuentra el archivo config
        return properties.getProperty("db.url", "jdbc:sqlite:resources/db/battleship_db.sqlite");
    }
}