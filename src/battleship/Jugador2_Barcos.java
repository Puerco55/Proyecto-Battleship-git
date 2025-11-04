package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class Jugador2_Barcos extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1697287710791087526L;

	// Componentes
    private JButton[][] celdas = new JButton[10][10];
    private JRadioButton orientacionHorizontal;
    private JRadioButton orientacionVertical;
    private JButton guardarButton;

	public Jugador2_Barcos(Map<Integer, Integer> configBarcos) {
		
		// Configuración básica de la ventana
        setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 420); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        // Panel Superior (donde va el título)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panelSuperior.add(new JLabel("Coloca tus barcos, jugador 2"));
        add(panelSuperior, BorderLayout.NORTH);

        // Panel Central (la cuadrícula del juego)
        JPanel panelCuadricula = new JPanel(new GridLayout(10, 10));
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // Crear las celdas de la cuadrícula
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JButton celda = new JButton();
                celda.setBackground(Color.CYAN.darker()); // Color del agua
                celdas[fila][col] = celda; // Guardar la celda en el array
                panelCuadricula.add(celda);
            }
        }
        add(panelCuadricula, BorderLayout.CENTER); // Colocar la cuadrícula en el centro

        // Panel Derecho (los controles para el jugador)
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS)); // Layout vertical
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // Sección para seleccionar el tipo de barco
        panelDerecho.add(new JLabel("1. Elige un barco:"));
        ButtonGroup grupoBarcos = new ButtonGroup(); // Agrupar los radio buttons

        // Botones para elegir un barco según la configuración
        configBarcos.forEach((tamano, cantidad) -> {
            String nombreBarco = "Barco de " + tamano;
            JRadioButton radioBarco = new JRadioButton(nombreBarco);
            grupoBarcos.add(radioBarco);
            panelDerecho.add(radioBarco);

            // Etiqueta para mostrar cuántos hay de cada tipo
            JLabel cantidadLabel = new JLabel("Disponibles: " + cantidad);
            cantidadLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 0));
            panelDerecho.add(cantidadLabel);
        });

        panelDerecho.add(Box.createRigidArea(new Dimension(0, 20))); // Un espacio en blanco para separar

        // Sección para elegir la orientación
        panelDerecho.add(new JLabel("2. Elige la orientación:"));
        ButtonGroup grupoOrientacion = new ButtonGroup();
        orientacionHorizontal = new JRadioButton("Horizontal", true); // Seleccionado por defecto
        orientacionVertical = new JRadioButton("Vertical");
        grupoOrientacion.add(orientacionHorizontal);
        grupoOrientacion.add(orientacionVertical);
        panelDerecho.add(orientacionHorizontal);
        panelDerecho.add(orientacionVertical);

        panelDerecho.add(Box.createVerticalGlue()); // Empuja el botón de guardar hacia abajo

        // Botón de Guardar
        guardarButton = new JButton("GUARDAR");
        panelDerecho.add(guardarButton);
        add(panelDerecho, BorderLayout.EAST); // Colocar el panel de controles a la derecha
        setVisible(true);
        guardarButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Para saltar a la pantalla de inicio
				PantallaInicioJuego inicioJuego = new PantallaInicioJuego();
		        inicioJuego.setVisible(true);
		        
		        dispose();
			}
		});
        
    }
	
	// Testing
	
//	public static void main(String[] args) {
//		Map<Integer, Integer> configBarcos = new HashMap<>();
//		configBarcos.put(1, 4); 
//		configBarcos.put(2, 3);
//		configBarcos.put(3, 2); 
//
//		new Jugador1_Barcos(configBarcos);
//	}
}

