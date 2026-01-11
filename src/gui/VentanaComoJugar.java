package gui;

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VentanaComoJugar extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Color COLOR_BOTON = new Color(30, 136, 229);
	private final Color COLOR_TEXTO = Color.WHITE;
	private final ImageIcon ICONO_INFO;

	public VentanaComoJugar() {

		// imagen de icono de información
		ICONO_INFO = new ImageIcon(
				new ImageIcon("resources/images/info.png").getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

		setTitle("Cómo Jugar");
		setSize(750, 800);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		GradientPanel fondo = new GradientPanel();
		fondo.setLayout(new BorderLayout());
		fondo.setBorder(new EmptyBorder(25, 25, 25, 25));
		setContentPane(fondo);

		JLabel titulo = new JLabel("CÓMO JUGAR", SwingConstants.CENTER);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
		titulo.setForeground(COLOR_TEXTO);
		titulo.setBorder(new EmptyBorder(0, 0, 20, 0));
		fondo.add(titulo, BorderLayout.NORTH);

		JPanel contenido = new JPanel();
		contenido.setOpaque(false);
		contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
		fondo.add(contenido, BorderLayout.CENTER);

		// Normas
		JPanel pNormas = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pNormas.setOpaque(false);
		pNormas.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel lblNormas = new JLabel("NORMAS");
		lblNormas.setFont(new Font("Segoe UI", Font.BOLD, 20));
		lblNormas.setForeground(COLOR_TEXTO);
		pNormas.add(lblNormas);
		contenido.add(pNormas);

		JTextArea normas = new JTextArea("• Cada jugador tiene un temporizador, como en el ajedrez.\n"
				+ "• Al pasar turno se añaden X segundos al temporizador.\n"
				+ "• Si el temporizador llega a 0, el jugador pierde.\n\n" + "• Cada turno permite un disparo.\n"
				+ "• Disparo normal: si impacta en un barco, se puede seguir disparando hasta fallar.\n"
				+ "• Super Disparo y Mega Disparo: finalizan el turno automáticamente.\n"
				+ "• Escudo: cancela el siguiente disparo del rival, incluso Super o Mega Disparo.");
		normas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		normas.setForeground(COLOR_TEXTO);
		normas.setOpaque(false);
		normas.setEditable(false);
		normas.setFocusable(false);
		normas.setBorder(new EmptyBorder(10, 0, 20, 0));
		normas.setAlignmentX(Component.LEFT_ALIGNMENT);
		contenido.add(normas);

		contenido.add(Box.createVerticalStrut(15));

		// Equipos
		JPanel pEquipos = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pEquipos.setOpaque(false);
		pEquipos.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel lblEquipos = new JLabel("🚢 EQUIPOS");
		lblEquipos.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		lblEquipos.setForeground(COLOR_TEXTO);
		pEquipos.add(lblEquipos);
		contenido.add(pEquipos);

		String[] equipos = { "Cruiser", "Dreadnought", "Destroyer", "Submarino" };
		for (String equipo : equipos) {
			contenido.add(crearFilaConIcono(equipo, "Este es el equipo " + equipo));
		}

		contenido.add(Box.createVerticalStrut(15));

		// Habilidades
		JPanel pHabilidades = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pHabilidades.setOpaque(false);
		pHabilidades.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel lblHabilidades = new JLabel("⚡ HABILIDADES");
		lblHabilidades.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
		lblHabilidades.setForeground(COLOR_TEXTO);
		pHabilidades.add(lblHabilidades);
		contenido.add(pHabilidades);

		contenido.add(crearFilaConIcono("Super Disparo", "resources/gifs/super_disparo.gif"));
		contenido.add(crearFilaConIcono("Mega Disparo", "resources/gifs/mega_disparo.gif"));
		contenido.add(crearFilaConIcono("Escudo", "Cancela el siguiente disparo enemigo, incluso Super/Mega Disparo."));

		contenido.add(Box.createVerticalStrut(30));

		// Boton continuar
		JButton continuar = new JButton("Continuar");
		continuar.setFont(new Font("Segoe UI", Font.BOLD, 16));
		continuar.setForeground(COLOR_TEXTO);
		continuar.setBackground(COLOR_BOTON);
		continuar.setFocusPainted(false);
		continuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
		continuar.setBorder(new EmptyBorder(10, 40, 10, 40));
		continuar.setAlignmentX(Component.CENTER_ALIGNMENT);

		continuar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				continuar.setBackground(COLOR_BOTON.brighter());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				continuar.setBackground(COLOR_BOTON);
			}
		});

		continuar.addActionListener(e -> {
			new SeleccionPerfil().setVisible(true);
			dispose();
		});

		JPanel pContinuar = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pContinuar.setOpaque(false);
		pContinuar.setAlignmentX(Component.CENTER_ALIGNMENT);
		pContinuar.add(continuar);
		contenido.add(pContinuar);
	}

	private JPanel crearFilaConIcono(String texto, String recurso) {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		panel.setOpaque(false);
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lbl = new JLabel("• " + texto);
		lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
		lbl.setForeground(COLOR_TEXTO);

		JLabel icono = new JLabel(ICONO_INFO);

		// Aqui miramos si acaba en .gif o no, para poner GIF o texto
		if (recurso != null && recurso.toLowerCase().endsWith(".gif")) {
			instalarTooltipGif(icono, recurso); // GIF animado
		} else {
			icono.setToolTipText(recurso); // solo texto
		}

		panel.add(lbl);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(icono);

		return panel;
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
			g2.setPaint(new GradientPaint(0, 0, new Color(10, 25, 50), 0, getHeight(), new Color(0, 50, 100)));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}

	private void instalarTooltipGif(JLabel icono, String gifPath) {
		JWindow tooltip = new JWindow();
		JLabel gifLabel = new JLabel(new ImageIcon(gifPath));

		// Borde blanco
		gifLabel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
		tooltip.add(gifLabel);
		tooltip.pack();

		icono.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				Point p = e.getLocationOnScreen();

				// Posición para que no se salga de la pantalla
				Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
				int x = p.x + 15;
				int y = p.y + 15;
				if (x + tooltip.getWidth() > screenSize.width)
					x = p.x - tooltip.getWidth() - 15;
				if (y + tooltip.getHeight() > screenSize.height)
					y = p.y - tooltip.getHeight() - 15;

				tooltip.setLocation(x, y);
				tooltip.setVisible(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				tooltip.setVisible(false);
			}
		});
	}

}