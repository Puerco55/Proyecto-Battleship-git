package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

import gui.PantallaInicioJuego;

public class AjustesPartida extends JFrame {

	private static final long serialVersionUID = 1L;
	private JSpinner[] spinners;

	private String[] nombresBarcos = { "Barco de 5", "Barco de 4", "Barco de 3", "Barco de 2", "Super Disparo",
			"Mega Disparo", "Escudos" };
	private int[] tamanosBarcos = { 5, 4, 3, 2 };

	private String equipoJugador1;
	private String equipoJugador2;

	public AjustesPartida(String equipoJugador1, String equipoJugador2) {
		this.equipoJugador1 = equipoJugador1;
		this.equipoJugador2 = equipoJugador2;

		setTitle("Ajustes de la Partida");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 600);
		setLocationRelativeTo(null);
		setResizable(false);

		GradientPanel mainPanel = new GradientPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		setContentPane(mainPanel);

		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setOpaque(false);
		mainPanel.add(formPanel, BorderLayout.CENTER);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 15, 10, 15);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Título
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		JLabel titulo = new JLabel("CONFIGURACIÓN DE FLOTA", JLabel.CENTER);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
		titulo.setForeground(new Color(100, 200, 255));
		formPanel.add(titulo, gbc);

		gbc.gridwidth = 1;
		spinners = new JSpinner[nombresBarcos.length];

		// Configuración de Spinners con los LÍMITES SOLICITADOS
		for (int i = 0; i < nombresBarcos.length; i++) {
			gbc.gridx = 0;
			gbc.gridy = i + 1;
			gbc.weightx = 0.6;

			JLabel label = new JLabel(nombresBarcos[i] + ":");
			label.setFont(new Font("Segoe UI", Font.BOLD, 14));
			label.setForeground(Color.WHITE);
			formPanel.add(label, gbc);

			gbc.gridx = 1;
			gbc.weightx = 0.4;

			// LÍMITES ACTUALIZADOS AQUÍ: (Valor Inicial, Min, Max, Paso)
			if (i == 0)
				spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 1, 1)); // Barco 5: Max 1
			else if (i == 1)
				spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 1, 1)); // Barco 4: Max 1
			else if (i == 2)
				spinners[i] = new JSpinner(new SpinnerNumberModel(2, 0, 2, 1)); // Barco 3: Max 2
			else if (i == 3)
				spinners[i] = new JSpinner(new SpinnerNumberModel(3, 0, 3, 1)); // Barco 2: Max 3 (Inicial 3)
			else if (i == 4)
				spinners[i] = new JSpinner(new SpinnerNumberModel(2, 0, 2, 1)); // Super: Max 2
			else if (i == 5)
				spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 1, 1)); // Mega: Max 1
			else if (i == 6)
				spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 2, 1)); // Escudos: Max 2

			spinners[i].setPreferredSize(new Dimension(80, 30));
			spinners[i].setBorder(BorderFactory.createLineBorder(Color.WHITE));
			formPanel.add(spinners[i], gbc);
		}

		JButton guardarButton = new JButton("COMENZAR PARTIDA");
		estilizarBoton(guardarButton);

		gbc.gridx = 0;
		gbc.gridy = nombresBarcos.length + 1;
		gbc.gridwidth = 2;
		gbc.insets = new Insets(30, 10, 10, 10);
		formPanel.add(guardarButton, gbc);

		guardarButton.addActionListener(e -> {
			Map<Integer, Integer> configBarcos = new HashMap<>();
			int totalCasillas = 0;

			for (int i = 0; i < tamanosBarcos.length; i++) {
				int cant = (int) spinners[i].getValue();
				if (cant > 0) {
					configBarcos.put(tamanosBarcos[i], cant);
					totalCasillas += tamanosBarcos[i] * cant;
				}
			}

			if (configBarcos.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Selecciona al menos un barco.", "Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (totalCasillas > 50) {
				JOptionPane.showMessageDialog(this, "Demasiados barcos. Máximo 50 casillas ocupadas.", "Error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			final int superD = (int) spinners[4].getValue();
			final int megaD = (int) spinners[5].getValue();
			final int escudos = (int) spinners[6].getValue();
			dispose();

			final int[][][] tableros = new int[2][][];

			Consumer<int[][]> configurarJugador2 = (tableroJ1) -> {
				tableros[0] = tableroJ1;
				JOptionPane.showMessageDialog(null, "Jugador 1 listo.\nPasar a Jugador 2.", "Cambio",
						JOptionPane.INFORMATION_MESSAGE);
				new ColocarBarcos(2, configBarcos, this.equipoJugador2, (tableroJ2) -> {
					tableros[1] = tableroJ2;
					PantallaInicioJuego inicio = new PantallaInicioJuego(superD, megaD, escudos, tableros[0],
							tableros[1], equipoJugador2, equipoJugador2);
					inicio.setVisible(true);
				});
			};

			new ColocarBarcos(1, configBarcos, this.equipoJugador1, configurarJugador2);
		});
	}

	private void estilizarBoton(JButton btn) {
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setForeground(Color.WHITE);
		btn.setBackground(new Color(55, 71, 79));
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(200, 45));
	}

	class GradientPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setPaint(new GradientPaint(0, 0, new Color(60, 80, 90), 0, getHeight(), new Color(20, 25, 30)));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}