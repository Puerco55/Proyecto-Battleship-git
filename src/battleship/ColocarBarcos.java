package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.HashMap;

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
	private int barcoSeleccionado = -1;
	private Map<Integer, Integer> barcosDisponibles;
	private boolean[][] tablero = new boolean[10][10];

	// Labels de disponibilidad
	private Map<Integer, JLabel> labelsDisponibilidad = new HashMap<>();

	public ColocarBarcos(int numeroJugador, Map<Integer, Integer> configBarcos, Runnable onGuardar) {

		// Clonamos el map para que cada jugador tenga el suyo
		this.barcosDisponibles = new HashMap<>(configBarcos);

		// Configuración de la ventana
		setTitle("Hundir la Flota");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(420, 420);
		setLocationRelativeTo(null);
		setResizable(false);

		// Panel superior
		JPanel panelSuperior = new JPanel();
		panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
		panelSuperior.add(new JLabel("Coloca tus barcos, Jugador " + numeroJugador));
		add(panelSuperior, BorderLayout.NORTH);

		// Panel cuadricula
		JPanel panelCuadricula = new JPanel(new GridLayout(10, 10));
		panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		for (int fila = 0; fila < 10; fila++) {
			for (int col = 0; col < 10; col++) {
				JButton celda = new JButton();
				celda.setBackground(Color.CYAN.darker());
				celdas[fila][col] = celda;

				int f = fila, c = col;
				celda.addActionListener(e -> colocarBarco(f, c));

				panelCuadricula.add(celda);
			}
		}
		add(panelCuadricula, BorderLayout.CENTER);

		// Panel derecho
		JPanel panelDerecho = new JPanel();
		panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
		panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

		panelDerecho.add(new JLabel("1. Elige un barco:"));
		ButtonGroup grupoBarcos = new ButtonGroup();

		// Crear botones de barcos + labels de disponibilidad
		configBarcos.forEach((tamano, cantidad) -> {
			String nombreBarco = "Barco de " + tamano;
			JRadioButton radioBarco = new JRadioButton(nombreBarco);
			grupoBarcos.add(radioBarco);
			panelDerecho.add(radioBarco);

			radioBarco.addActionListener(e -> barcoSeleccionado = tamano);

			JLabel cantidadLabel = new JLabel("Disponibles: " + cantidad);
			cantidadLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 0));
			panelDerecho.add(cantidadLabel);

			// Guardamos el label asociado al tamaño
			labelsDisponibilidad.put(tamano, cantidadLabel);
		});

		panelDerecho.add(Box.createRigidArea(new Dimension(0, 20)));

		// Orientación
		panelDerecho.add(new JLabel("2. Elige la orientación:"));
		ButtonGroup grupoOrientacion = new ButtonGroup();
		orientacionHorizontal = new JRadioButton("Horizontal", true);
		orientacionVertical = new JRadioButton("Vertical");
		grupoOrientacion.add(orientacionHorizontal);
		grupoOrientacion.add(orientacionVertical);
		panelDerecho.add(orientacionHorizontal);
		panelDerecho.add(orientacionVertical);

		panelDerecho.add(Box.createVerticalGlue());

		// Botón guardar
		guardarButton = new JButton("GUARDAR");
		panelDerecho.add(guardarButton);

		add(panelDerecho, BorderLayout.EAST);

		guardarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("Guardando configuración del Jugador " + numeroJugador);

				dispose();

				if (onGuardar != null) {
					onGuardar.run();
				}
			}
		});

		setVisible(true);
	}
	// Colocacion de Barcos
	private void colocarBarco(int fila, int col) {

		if (barcoSeleccionado == -1) {
			System.out.println("Selecciona un barco primero.");
			return;
		}

		if (barcosDisponibles.get(barcoSeleccionado) == 0) {
			System.out.println("No quedan barcos de tamaño " + barcoSeleccionado);
			return;
		}

		boolean horizontal = orientacionHorizontal.isSelected();

		// Verificar que cabe
		if (horizontal) {
			if (col + barcoSeleccionado > 10) {
				System.out.println("No cabe horizontalmente.");
				return;
			}
		} else {
			if (fila + barcoSeleccionado > 10) {
				System.out.println("No cabe verticalmente.");
				return;
			}
		}

		// Para que los barcos no se solapen
		for (int i = 0; i < barcoSeleccionado; i++) {
			int f = fila + (horizontal ? 0 : i);
			int c = col + (horizontal ? i : 0);
			if (tablero[f][c]) {
				System.out.println("Ya hay un barco en esa posición.");
				return;
			}
		}

		// Colocar barco
		for (int i = 0; i < barcoSeleccionado; i++) {
			int f = fila + (horizontal ? 0 : i);
			int c = col + (horizontal ? i : 0);

			tablero[f][c] = true;
			celdas[f][c].setBackground(Color.GRAY);
		}

		// Reducir disponibilidad
		int nuevaCantidad = barcosDisponibles.get(barcoSeleccionado) - 1;
		barcosDisponibles.put(barcoSeleccionado, nuevaCantidad);

		// Actualizar disponibilidad
		JLabel label = labelsDisponibilidad.get(barcoSeleccionado);
		label.setText("Disponibles: " + nuevaCantidad);

		System.out.println("Barco de tamaño " + barcoSeleccionado + " colocado.");
	}
}

