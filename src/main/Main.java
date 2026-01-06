package main;

import javax.swing.SwingUtilities;

import db.EstadisticasDAO;
import db.GestorBaseDatos;
import gui.MainMenu;

public class Main {
    public static void main(String[] args) {
        // 1. Inicializamos la base de datos primero
        GestorBaseDatos.inicializarTablas();

       
        // Zona de prueba
       
        try {
            EstadisticasDAO dao = new EstadisticasDAO();
            
            // Simulamos que el Jugador 1 ganó una partida en 25 turnos
            dao.guardarPartida("Jugador 1", 25, 5);
            
            // Simulamos que el Jugador 2 ganó una partida larga en 50 turnos
            dao.guardarPartida("Jugador 2", 50, 5);
            
            // Simulamos otra victoria del Jugador 1 rápida
            dao.guardarPartida("Jugador 1", 12, 5);
            
            System.out.println("Datos de prueba añadidos");
            
        } catch (Exception e) {
            System.out.println("Error insertando datos de prueba: " + e.getMessage());
            e.printStackTrace();
        } 
        // Fin de prueba
       

        // 2. Arrancar la interfaz gráfica
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainMenu menu = new MainMenu();
                menu.setVisible(true);
            }
        });
    }
}