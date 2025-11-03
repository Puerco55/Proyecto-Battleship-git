package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class Jugador1_Barcos extends JFrame{

	private static final long serialVersionUID = 1L;
	
	// Componentes
    private JButton[][] celdas = new JButton[10][10];
    private JRadioButton orientacionHorizontal;
    private JRadioButton orientacionVertical;
    private JButton guardarButton;

	public Jugador1_Barcos(Map<Integer, Integer> configBarcos) {
		
		// conf de la ventanas
        setTitle("Hundir la Flota");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 420); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        // Titulo
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        panelSuperior.add(new JLabel("Coloca tus barcos, jugador 1"));
        add(panelSuperior, BorderLayout.NORTH);

        // Cuadricula
        JPanel panelCuadricula = new JPanel(new GridLayout(10, 10));
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JButton celda = new JButton();
                celda.setBackground(Color.CYAN.darker());
                celdas[fila][col] = celda;
                panelCuadricula.add(celda);
            }
        }
        add(panelCuadricula, BorderLayout.CENTER);

        // Controles del juego
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        // Seleccionar barco
        panelDerecho.add(new JLabel("1. Elige un barco:"));
        ButtonGroup grupoBarcos = new ButtonGroup();

        // Botones para elegir un barco segun la configuracion
        configBarcos.forEach((tamano, cantidad) -> {
            String nombreBarco = "Barco de " + tamano;
            JRadioButton radioBarco = new JRadioButton(nombreBarco);
            grupoBarcos.add(radioBarco);
            panelDerecho.add(radioBarco);

            // Cuantos barcos disponibles
            JLabel cantidadLabel = new JLabel("Disponibles: " + cantidad);
            cantidadLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 0));
            panelDerecho.add(cantidadLabel);
        });

        panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));

        // Orientacion
        panelDerecho.add(new JLabel("2. Elige la orientación:"));
        ButtonGroup grupoOrientacion = new ButtonGroup();
        orientacionHorizontal = new JRadioButton("Horizontal", true);
        orientacionVertical = new JRadioButton("Vertical");
        grupoOrientacion.add(orientacionHorizontal);
        grupoOrientacion.add(orientacionVertical);
        panelDerecho.add(orientacionHorizontal);
        panelDerecho.add(orientacionVertical);

        panelDerecho.add(Box.createVerticalGlue());

        // Boton de Guardar
        guardarButton = new JButton("GUARDAR");
        panelDerecho.add(guardarButton);

        add(panelDerecho, BorderLayout.EAST);
        setVisible(true);
        guardarButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// Para pasar al siguiente jugador
		        Jugador2_Barcos barcosjug2 = new Jugador2_Barcos(configBarcos);
		        barcosjug2.setVisible(true);
		        
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
