package battleship;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class Menu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Menu() {
        
        setTitle("Battleship");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 420); 
        setLocationRelativeTo(null); 
        setResizable(false); 

        // --- PANEL PRINCIPAL ---
        // Usamos un panel principal con BorderLayout para organizar los elementos.
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Añade un margen interior
        setContentPane(mainPanel);

        // --- TÍTULO DEL JUEGO ---
        JLabel titleLabel = new JLabel("Hundir la Flota", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Fuente grande y en negrita
        mainPanel.add(titleLabel, BorderLayout.NORTH); // Añadir el título en la parte superior

        // --- PANEL DE BOTONES ---
        // Un panel para los botones con GridLayout para que se apilen verticalmente.
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 15)); // 3 filas, 1 columna, 15px de espacio vertical
        buttonPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Margen para que no estén pegados a los bordes
        mainPanel.add(buttonPanel, BorderLayout.CENTER); // Añadir el panel de botones en el centro

        // --- CREACIÓN DE BOTONES ---
        JButton playButton = new JButton("Jugar");
        JButton optionsButton = new JButton("Opciones");
        JButton exitButton = new JButton("Salir");
        
        // --- AÑADIR BOTONES AL PANEL ---
        buttonPanel.add(playButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(exitButton);

        // --- FUNCIONALIDAD DE LOS BOTONES (ACTION LISTENERS) ---

        // 1. Botón Jugar
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                System.out.println("Iniciar Juego");
                
                dispose(); // Cierra la ventana del menú
            }
        });

        // 2. Botón Opciones
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Muestra un cuadro de diálogo simple
                JOptionPane.showMessageDialog(Menu.this, 
                        "Las opciones aún no están implementadas.", 
                        "Opciones", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 3. Botón Salir
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la aplicación
                System.exit(0);
            }
        });

        // Hacemos visible la ventana al final, después de añadir todos los componentes.
        setVisible(true);
    }

    public static void main(String[] args) {
        // Es una buena práctica iniciar las aplicaciones Swing de esta manera.
        // Asegura que la GUI se cree en el hilo de despacho de eventos (EDT).
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Menu();
            }
        });
    }

}
