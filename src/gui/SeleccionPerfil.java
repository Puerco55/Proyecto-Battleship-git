package gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.AjustesPartida;

public class SeleccionPerfil extends JFrame {
	
	public SeleccionPerfil() {
	this(null); 
	}
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private PanelJugador jugador1;
	private PanelJugador jugador2;
	private JButton botonContinuar;

	public SeleccionPerfil(Consumer<Map<Integer, String>> onEquiposSeleccionados) {
        setTitle("Selecciona tus equipos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel contenedor horizontal
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 15, 5));

        String[] equipos = {"Submarine", "Destroyer", "Cruiser", "Dreadnought"}; // Ejemplo de equipos
        jugador1 = new PanelJugador("Jugador 1", equipos);
        jugador2 = new PanelJugador("Jugador 2", equipos);

        panelCentral.add(jugador1);
        panelCentral.add(jugador2);
        add(panelCentral, BorderLayout.CENTER);

        // Botón de continuar
        botonContinuar = new JButton("CONTINUAR");
        botonContinuar.setFont(new Font("Arial", Font.BOLD, 18));
        botonContinuar.addActionListener(e -> {
        	 String equipo1 = jugador1.getEquipoSeleccionado();
        	 String equipo2 = jugador2.getEquipoSeleccionado();
        	 dispose(); // cerramos SeleccionPerfil
        	 new AjustesPartida(equipo1, equipo2); // abrimos AjustesPartida
        });

        add(botonContinuar, BorderLayout.SOUTH);
        setVisible(true);
        
        SwingUtilities.invokeLater(() -> {
            jugador1.actualizarImagen();
            jugador2.actualizarImagen();
        });
    	}
	}

	class PanelJugador extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private JLabel labelEquipo;
		private JLabel imagenEquipoLabel;
		private JButton botonIzquierda, botonDerecha;
		private String[] equiposDisponibles;
		private int indiceActual = 0;

    public PanelJugador(String nombre, String[] equipos) {
    	this.equiposDisponibles = equipos;

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder(nombre));

        // Label del nombre 
        labelEquipo = new JLabel(equipos[indiceActual], JLabel.CENTER);
        labelEquipo.setFont(new Font("Arial", Font.BOLD, 16));
        add(labelEquipo, BorderLayout.NORTH);

        imagenEquipoLabel = new FadeLabel();
        imagenEquipoLabel.setHorizontalAlignment(JLabel.CENTER);
        imagenEquipoLabel.setBorder(BorderFactory.createLineBorder(Color.black, 2));
        add(imagenEquipoLabel, BorderLayout.CENTER);
        actualizarImagen();

        // Panel con botones para cambiar de equipo
        JPanel panelBotones = new JPanel(new FlowLayout());
        botonIzquierda = new JButton("◀");
        botonDerecha = new JButton("▶");

        botonIzquierda.addActionListener(e -> {
            indiceActual = (indiceActual - 1 + equiposDisponibles.length) % equiposDisponibles.length;
            actualizarImagen();
        });
        botonDerecha.addActionListener(e -> {
            indiceActual = (indiceActual + 1) % equiposDisponibles.length;
            actualizarImagen();
        });

        panelBotones.add(botonIzquierda);
        panelBotones.add(botonDerecha);
        add(panelBotones, BorderLayout.SOUTH);
        
    }
    class FadeLabel extends JLabel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private float alpha = 1f; 

        public void setAlpha(float alpha) {
            this.alpha = alpha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2d);
            g2d.dispose();
        }
    	}
    public void actualizarImagen() {
        String nombreEquipo = equiposDisponibles[indiceActual];
        labelEquipo.setText(nombreEquipo);

        // Cargar imagen original
        ImageIcon icon = new ImageIcon("resources/images/Team/" + nombreEquipo + "Team.png");
        Image img = icon.getImage();

        int labelWidth = imagenEquipoLabel.getWidth();
        int labelHeight = imagenEquipoLabel.getHeight();

        ImageIcon iconoEscalado;

        if (labelWidth > 0 && labelHeight > 0) {
            // Escalar proporcionalmente
            double ratio = Math.min((double) labelWidth / img.getWidth(null), (double) labelHeight / img.getHeight(null));
            int nuevoAncho = (int) (img.getWidth(null) * ratio);
            int nuevoAlto = (int) (img.getHeight(null) * ratio);
            Image imgEscalada = img.getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
            iconoEscalado = new ImageIcon(imgEscalada);
        } else {
        	iconoEscalado = icon;
        }

        // Llamamos al fade pasando el icon ya escalado
        transicionconFade(iconoEscalado);
    }

    private void transicionconFade(ImageIcon iconnuevo) {
        FadeLabel label = (FadeLabel) imagenEquipoLabel;
        ImageIcon iconActual = (ImageIcon) label.getIcon();

        // Si no hay icon actual, asignamos directamente
        if (iconActual == null) {
            label.setIcon(iconnuevo);
            return;
        }

        // Fade out
        Timer timerOut = new Timer(40, null);
        final float[] opacidadOut = {1f};
        timerOut.addActionListener(e -> {
        	opacidadOut[0] -= 0.1f;
            if (opacidadOut[0] <= 0f) {
                opacidadOut[0] = 0f;
                label.setAlpha(0f);
                label.setIcon(iconnuevo); 

                // Fade in
                Timer timerIn = new Timer(40, null);
                final float[] opacidadIn = {0f};
                timerIn.addActionListener(ev -> {
                    opacidadIn[0] += 0.1f;
                    if (opacidadIn[0] >= 1f) opacidadIn[0] = 1f;
                    label.setAlpha(opacidadIn[0]);
                    if (opacidadIn[0] >= 1f) timerIn.stop();
                });
                timerIn.start();

                timerOut.stop();
            } else {
                label.setAlpha(opacidadOut[0]);
            }
        });
        timerOut.start();
    }

    public String getEquipoSeleccionado() {
        return equiposDisponibles[indiceActual];
    }
    
}