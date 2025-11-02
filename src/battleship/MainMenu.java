package battleship;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MainMenu() {

        setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(420, 420); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        //Cerrar ventana
        addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				confirmarSalida();
			}
		});

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
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 15)); // 3 filas, 1 columna, 15px de espacio vertical
        buttonPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); // Margen para que no estén pegados a los bordes
        mainPanel.add(buttonPanel, BorderLayout.CENTER); // Añadir el panel de botones en el centro

        // --- CREACIÓN DE BOTONES ---
        JButton playButton = new JButton("Jugar");
        JButton optionsButton = new JButton("Opciones");
        JButton statsButton = new JButton("Estadísticas");
        JButton exitButton = new JButton("Salir");
        
        
        // --- AÑADIR BOTONES AL PANEL ---
        buttonPanel.add(playButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(exitButton);

        // --- FUNCIONALIDAD DE LOS BOTONES (ACTION LISTENERS) ---

        // 1. Botón Jugar
        playButton.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Iniciar Juego");
                Ajustes_Partida ajustesPartida = new Ajustes_Partida();
                ajustesPartida.setVisible(true);
                dispose(); 
            }
        });

        // 2. Botón Opciones
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	new MenuOpciones().setVisible(true);
            }
        });
        
        // 3. Botón Estadísticas
        statsButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Bases dou datoss
			}
		});

        // 4. Botón Salir
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la aplicación
                confirmarSalida();
            }
        });

        // Hacemos visible la ventana al final, después de añadir todos los componentes.
        setVisible(true);
    }
	
    //Función de cierre de ventana
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas salir de la aplicación?",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    
}
