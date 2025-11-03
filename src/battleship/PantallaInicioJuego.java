package battleship;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PantallaInicioJuego extends JFrame {
	private static final long serialVersionUID = 1L;
	
	public PantallaInicioJuego() {
		setTitle("Hundir la Flota 🚢");
       setDefaultCloseOperation(EXIT_ON_CLOSE);
       setSize(420, 420);
       setLocationRelativeTo(null);
       setResizable(false);
      
       // Panel principal
       JPanel panel = new JPanel(new BorderLayout(20,20));
       panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
      
       // Mensaje de "Todo preparado"
       JLabel mensaje = new JLabel("¡Ambos jugadores han colocado sus barcos!", SwingConstants.CENTER);
       mensaje.setFont(new Font("Arial", Font.BOLD, 16));
      
       // Boton para empezar el juego
       JButton empezarButton = new JButton("Empezar Juego");
       empezarButton.setFont(new Font("Arial", Font.BOLD, 16));
      
       // Accion del boton
       empezarButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Ventana con inicio del juego
				
			}
		});
       panel.add(mensaje, BorderLayout.CENTER );
       panel.add(empezarButton, BorderLayout.SOUTH);
       add(panel);
      
	}
}
