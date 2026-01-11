package main;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.SwingUtilities;

import db.GestorBaseDatos;
import gui.MainMenu;

public class Main {
    
    // 1. Definimos el "Grabador" (Logger)
    private static final Logger LOGGER = Logger.getLogger("BattleshipApp");

    public static void main(String[] args) {
        
        // 2. Antes de nada, configuramos el archivo de log
        configurarLogger();
        
        // Escribimos la primera línea en el historial
        LOGGER.info("La aplicación ha comenzado");

        GestorBaseDatos.inicializarTablas();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    MainMenu menu = new MainMenu();
                    menu.setVisible(true);
                    LOGGER.info("Ventana principal cargada."); // Registro de éxito
                } catch (Exception e) {
                    // Registro de error si falla
                    LOGGER.severe("Fallo grave al iniciar: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // 3. Método mágico que crea la carpeta y el archivo
    private static void configurarLogger() {
        try {
            //Crear carpeta 'log' si no existe
            File logDir = new File("log");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            
            FileHandler fileHandler = new FileHandler("log/battleship.log", true);
            fileHandler.setFormatter(new SimpleFormatter()); 
            
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            
        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo de log: " + e.getMessage());
        }
    }
}