package battleship;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Es una buena práctica iniciar las aplicaciones Swing de esta manera.
        // Asegura que la GUI se cree en el hilo de despacho de eventos (EDT).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
                
                      
         
            }
        });
    }

}
