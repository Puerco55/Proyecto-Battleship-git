package gui;

import java.awt.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class VentanaComoJugar extends JFrame{

	/**
	 * 
	 */
	 private static final long serialVersionUID = 1L;

	 private final Color COLOR_BOTON = new Color(30, 136, 229);
	 private final Color COLOR_TEXTO = Color.WHITE;
	 private final ImageIcon ICONO_INFO;

	 public VentanaComoJugar() {

	     // imagen de icono de información
	     ICONO_INFO = new ImageIcon(new ImageIcon("resources/images/info.png")
	         .getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

	     setTitle("Cómo Jugar");
	     setSize(720, 750);
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
	     JLabel lblNormas = new JLabel("NORMAS", SwingConstants.CENTER);
	     lblNormas.setFont(new Font("Segoe UI", Font.BOLD, 20));
	     lblNormas.setForeground(COLOR_TEXTO);
	     lblNormas.setAlignmentX(Component.CENTER_ALIGNMENT);
	     contenido.add(lblNormas);
	     
	     JTextArea normas = new JTextArea(
	         "• Cada jugador tiene un temporizador, como en el ajedrez.\n" +
	         "• Al pasar turno se añaden X segundos al temporizador.\n" +
	         "• Si el temporizador llega a 0, el jugador pierde.\n\n" +
	         "• Cada turno permite un disparo.\n" +
	         "• Disparo normal: si impacta en un barco, se puede seguir disparando hasta fallar.\n" +
	         "• Super Disparo y Mega Disparo: finalizan el turno automáticamente.\n" +
	         "• Escudo: cancela el siguiente disparo del rival, incluso Super o Mega Disparo."
	     );
	     normas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	     normas.setForeground(COLOR_TEXTO);
	     normas.setOpaque(false);
	     normas.setEditable(false);
	     normas.setFocusable(false);
	     normas.setBorder(new EmptyBorder(10, 20, 20, 20));
	     normas.setAlignmentX(Component.CENTER_ALIGNMENT);
	     contenido.add(normas);

	     contenido.add(Box.createVerticalStrut(15));

	     // Equipos
	     JLabel lblEquipos = new JLabel("EQUIPOS");
	     lblEquipos.setFont(new Font("Segoe UI", Font.BOLD, 18));
	     lblEquipos.setForeground(COLOR_TEXTO);
	     lblEquipos.setAlignmentX(Component.LEFT_ALIGNMENT);
	     contenido.add(lblEquipos);

	     String[] equipos = { "Cruiser", "Dreadnought", "Destroyer", "Submarino" };
	     for (String equipo : equipos) {
	         contenido.add(crearFilaConIcono(equipo, "Este es el equipo " + equipo));
	     }

	     contenido.add(Box.createVerticalStrut(15));

	     // Habilidades
	     JLabel lblHabilidades = new JLabel("HABILIDADES");
	     lblHabilidades.setFont(new Font("Segoe UI", Font.BOLD, 18));
	     lblHabilidades.setForeground(COLOR_TEXTO);
	     lblHabilidades.setAlignmentX(Component.LEFT_ALIGNMENT);
	     contenido.add(lblHabilidades);

	     contenido.add(crearFilaConIcono("Super Disparo", "Disparo en cruz. Finaliza el turno."));
	     contenido.add(crearFilaConIcono("Mega Disparo", "Disparo en área 3x3. Finaliza el turno."));
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

	     continuar.addActionListener(e -> {
	         new SeleccionPerfil().setVisible(true);
	         dispose();
	     });

	     contenido.add(continuar);
	 }

	
	 private JPanel crearFilaConIcono(String texto, String tooltip) {
	     JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	     panel.setOpaque(false);

	     JLabel lbl = new JLabel("• " + texto);
	     lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
	     lbl.setForeground(COLOR_TEXTO);

	     JLabel icono = new JLabel(ICONO_INFO);
	     icono.setToolTipText(tooltip);

	     panel.add(lbl);
	     panel.add(Box.createHorizontalStrut(5));
	     panel.add(icono);

	     return panel;
	 }

	 // Panel degraadado
	 class GradientPanel extends JPanel {
	     /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		 protected void paintComponent(Graphics g) {
	         super.paintComponent(g);
	         Graphics2D g2 = (Graphics2D) g;
	         g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	         g2.setPaint(new GradientPaint(
	             0, 0, new Color(10, 25, 50),
	             0, getHeight(), new Color(0, 50, 100)
	         ));
	         g2.fillRect(0, 0, getWidth(), getHeight());
	     }
	 }

	 public static void main(String[] args) {
	     SwingUtilities.invokeLater(() -> new VentanaComoJugar().setVisible(true));
	 }
	}