package battleship;

import java.awt.BorderLayout;
import java.awt.Font;
import java. awt.event.ActionEvent;
import java.awt. event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax. swing.JLabel;
import javax.swing.JPanel;
import javax. swing.SwingConstants;

public class PantallaInicioJuego extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private int superDisparos;
    private int megaDisparos;
    private boolean[][] tableroJugador1;
    private boolean[][] tableroJugador2;
    
    public PantallaInicioJuego(int superDisparos, int megaDisparos, 
                               boolean[][] tableroJ1, boolean[][] tableroJ2) {
        this.superDisparos = superDisparos;
        this. megaDisparos = megaDisparos;
        this.tableroJugador1 = tableroJ1;
        this.tableroJugador2 = tableroJ2;
        
        setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setResizable(false);
       
        // Panel principal
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
       
        // Título
        JLabel titulo = new JLabel("¡TODO LISTO!", SwingConstants.CENTER);
        titulo. setFont(new Font("Arial", Font. BOLD, 28));
        panel. add(titulo, BorderLayout.NORTH);
       
        // Mensaje
        JLabel mensaje = new JLabel("<html><center>Ambos jugadores han colocado sus barcos. <br><br>" +
            "El Jugador 1 comenzará la partida.<br><br>" +
            "¡Que gane el mejor! </center></html>", SwingConstants.CENTER);
        mensaje.setFont(new Font("Arial", Font. PLAIN, 12));
        panel.add(mensaje, BorderLayout. CENTER);
       
        // Boton para empezar el juego
        JButton empezarButton = new JButton("¡EMPEZAR BATALLA!");
        empezarButton.setFont(new Font("Arial", Font.BOLD, 18));
        empezarButton.setPreferredSize(new java.awt. Dimension(250, 50));
       
        JPanel panelBoton = new JPanel();
        panelBoton. add(empezarButton);
        panel.add(panelBoton, BorderLayout.SOUTH);
       
       
        
        add(panel);
    }
}