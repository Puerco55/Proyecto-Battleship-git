package battleship;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class Ajustes_Partida extends JFrame {

	private static final long serialVersionUID = 1L;

	// Componentes de la interfaz
	private JSpinner[] spinners;
	private JButton guardarButton;
	private String[] nombresBarcos = { "Barco de 5", "Barco de 4", "Barco de 3", "Barco de 2", "Super Disparo", "Mega Disparo"};
	private int[] tamanosBarcos = { 5, 4, 3, 2 };

	public Ajustes_Partida() {
		// Configuracion de la ventana
		setTitle("Ajustes de la Partida");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);

		spinners = new JSpinner[nombresBarcos.length];

		// Crear etiquetas y spinners para cada tipo de barco
		for (int i = 0; i < nombresBarcos.length; i++) {
			gbc.gridx = 0;
			gbc.gridy = i;
			add(new JLabel(nombresBarcos[i]), gbc);

			gbc.gridx = 1;
			
			if (i == 0) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1)); // Barcos de 5 (min = 0, max = 1)
			} else if (i == 1) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1)); // Barcos de 4 (min = 0, max = 2)
			} else if (i == 2) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 2, 1)); // Barcos de 3 (min = 0, max = 2)
			} else if (i == 3) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1)); // Barcos de 2 (min = 0, max = 5)
			} else if (i == 4) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
			} else if (i == 5) {
				spinners[i] = new JSpinner(new SpinnerNumberModel(0, 0, 1, 1));
			}
			
			spinners[i].setPreferredSize(new Dimension(50, 25));
			add(spinners[i], gbc);
			
		}

		// Boton para guardar la configuracion
		guardarButton = new JButton("Guardar y Continuar");
		gbc.gridx = 0;
		gbc.gridy = nombresBarcos.length;
		gbc.gridwidth = 2; // Ocupa dos columnas
		gbc.anchor = GridBagConstraints.CENTER;
		add(guardarButton, gbc);

		pack(); // Ajusta el tamaño de la ventana al contenido
		setLocationRelativeTo(null); // Centra la ventana
		setResizable(false);
		setVisible(true);

		guardarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Recoge la configuracion de los spinners
				Map<Integer, Integer> configBarcos = new HashMap<>();
				for (int i = 0; i < tamanosBarcos.length; i++) {
					int cantidad = (int) spinners[i].getValue();
					if (cantidad > 0) {
						configBarcos.put(tamanosBarcos[i], cantidad);
					}
				}

				dispose();

				// Secuencia de configuracion de jugadores

				Runnable iniciarPartida = () -> {
					System.out.println("Ambos jugadores han colocado sus barcos.");
					PantallaInicioJuego inicioJuego = new PantallaInicioJuego();
					inicioJuego.setVisible(true);

					dispose();
				};

				Runnable configurarJugador2 = () -> {
					new ColocarBarcos(2, configBarcos, iniciarPartida);
				};

				new ColocarBarcos(1, configBarcos, configurarJugador2);
			}
		});
	}
}