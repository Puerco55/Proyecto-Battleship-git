package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField; // Importante
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PantallaInicioJuego extends JFrame {
	private static final long serialVersionUID = 1L;
	private int[][] tableroJugador1;
	private int[][] tableroJugador2;
	private final Color COLOR_BOTON = new Color(30, 136, 229);
	private final Color COLOR_TEXTO = Color.WHITE;
	
	// Campos para los nombres
	private JTextField txtNombreJ1;
	private JTextField txtNombreJ2;

	public PantallaInicioJuego(int superDisparos, int megaDisparos, int escudos, int[][] tableroJ1, int[][] tableroJ2,
			String equipoJ1, String equipoJ2) {
		this.tableroJugador1 = tableroJ1;
		this.tableroJugador2 = tableroJ2;
		setTitle("Hundir la Flota 🚢");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(450, 400); // Aumentado un poco el alto para que quepan los inputs
		setLocationRelativeTo(null);
		setResizable(false);

		// Panel principal con gradiente
		GradientPanel panel = new GradientPanel();
		panel.setLayout(new BorderLayout(20, 20));
		panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
		setContentPane(panel);

		// Título
		JLabel titulo = new JLabel("¡TODO LISTO!", SwingConstants.CENTER);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
		titulo.setForeground(COLOR_TEXTO);
		panel.add(titulo, BorderLayout.NORTH);

		// --- PANEL CENTRAL (Mensaje e Inputs) ---
		JPanel centroPanel = new JPanel();
		centroPanel.setLayout(new BoxLayout(centroPanel, BoxLayout.Y_AXIS));
		centroPanel.setOpaque(false);
		
		JLabel mensaje = new JLabel("Ambos jugadores han colocado sus barcos");
		mensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		mensaje.setForeground(COLOR_TEXTO);
		mensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
		centroPanel.add(mensaje);
		
		centroPanel.add(Box.createVerticalStrut(20)); // Separación

		// Input Jugador 1
		JLabel lblJ1 = new JLabel("Nombre Jugador 1:");
		lblJ1.setForeground(Color.CYAN);
		lblJ1.setAlignmentX(Component.CENTER_ALIGNMENT);
		centroPanel.add(lblJ1);
		
		txtNombreJ1 = crearInput("Jugador 1");
		centroPanel.add(txtNombreJ1);
		
		centroPanel.add(Box.createVerticalStrut(10));

		// Input Jugador 2
		JLabel lblJ2 = new JLabel("Nombre Jugador 2:");
		lblJ2.setForeground(Color.ORANGE);
		lblJ2.setAlignmentX(Component.CENTER_ALIGNMENT);
		centroPanel.add(lblJ2);
		
		txtNombreJ2 = crearInput("Jugador 2");
		centroPanel.add(txtNombreJ2);

		panel.add(centroPanel, BorderLayout.CENTER);

		// Boton para empezar el juego
		JButton empezarButton = crearBotonEstilizado("¡EMPEZAR BATALLA!");
		empezarButton.setPreferredSize(new Dimension(250, 50));

		JPanel panelBoton = new JPanel();
		panelBoton.setOpaque(false);
		panelBoton.add(empezarButton);
		panel.add(panelBoton, BorderLayout.SOUTH);

		// Accion del boton
		empezarButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Obtener nombres o usar defecto si están vacíos
				String nombre1 = txtNombreJ1.getText().trim().isEmpty() ? "Jugador 1" : txtNombreJ1.getText().trim();
				String nombre2 = txtNombreJ2.getText().trim().isEmpty() ? "Jugador 2" : txtNombreJ2.getText().trim();

				// Pasamos los nombres al nuevo constructor de VentanaJuego
				VentanaJuego ventanaJuego = new VentanaJuego(1, superDisparos, megaDisparos, escudos, tableroJugador1,
						tableroJugador2, equipoJ1, equipoJ2, nombre1, nombre2);
				ventanaJuego.setVisible(true);
				dispose();
			}
		});
	}
	
	private JTextField crearInput(String textoDefecto) {
		JTextField tf = new JTextField(textoDefecto);
		tf.setMaximumSize(new Dimension(200, 30));
		tf.setHorizontalAlignment(SwingConstants.CENTER);
		tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		return tf;
	}

	private JButton crearBotonEstilizado(String texto) {
		JButton btn = new JButton(texto);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
		btn.setForeground(COLOR_TEXTO);
		btn.setBackground(COLOR_BOTON);
		btn.setFocusPainted(false);
		btn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1), new EmptyBorder(10, 40, 10, 40)));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(300, 60));
		btn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(COLOR_BOTON.brighter());
			}

			public void mouseExited(MouseEvent e) {
				btn.setBackground(COLOR_BOTON);
			}
		});
		return btn;
	}

	class GradientPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setPaint(new GradientPaint(0, 0, new Color(10, 25, 50), 0, getHeight(), new Color(0, 50, 100)));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}