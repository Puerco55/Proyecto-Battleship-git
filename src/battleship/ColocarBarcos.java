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

public class ColocarBarcos extends JFrame {

	private static final long serialVersionUID = 1L;

	// Componentes de la interfaz
	private JButton[][] celdas = new JButton[10][10];
	private JRadioButton orientacionHorizontal;
	private JRadioButton orientacionVertical;
	private JButton guardarButton;

	public ColocarBarcos(int numeroJugador, Map<Integer, Integer> configBarcos, Runnable onGuardar) {

		// Configuracion de la ventana
		setTitle("Hundir la Flota");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 420);
		setLocationRelativeTo(null);
		setResizable(false);

		// Panel superior con el titulo
		JPanel panelSuperior = new JPanel();
		panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		panelSuperior.add(new JLabel("Coloca tus barcos, Jugador " + numeroJugador));
		add(panelSuperior, BorderLayout.NORTH);

		// Panel central con la cuadricula
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

		// Panel derecho con los controles
		JPanel panelDerecho = new JPanel();
		panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
		panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

		// Seleccion de tipo de barco
		panelDerecho.add(new JLabel("1. Elige un barco:"));
		ButtonGroup grupoBarcos = new ButtonGroup();

		configBarcos.forEach((tamano, cantidad) -> {
			String nombreBarco = "Barco de " + tamano;
			JRadioButton radioBarco = new JRadioButton(nombreBarco);
			grupoBarcos.add(radioBarco);
			panelDerecho.add(radioBarco);

			JLabel cantidadLabel = new JLabel("Disponibles: " + cantidad);
			cantidadLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 0));
			panelDerecho.add(cantidadLabel);
		});

		panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));

		// Seleccion de orientacion
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

		// Listener para el boton de guardar
		guardarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Aqui hay que asegurarse de las posiciones y guardarlas para la partida
				System.out.println("Guardando configuración del Jugador " + numeroJugador);

				dispose();

				if (onGuardar != null) {
					onGuardar.run();
				}
			}
		});

		setVisible(true);
	}
}