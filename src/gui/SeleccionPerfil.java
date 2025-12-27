package gui;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import main.AjustesPartida;

public class SeleccionPerfil extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private PanelJugador jugador1;
    private PanelJugador jugador2;

    public SeleccionPerfil() {
        setTitle("Selecciona tus equipos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Fondo Degradado
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        // Panel Central (Jugadores)
        JPanel panelCentral = new JPanel(new GridLayout(1, 2, 30, 0));
        panelCentral.setOpaque(false);

        String[] equipos = {"Submarine", "Destroyer", "Cruiser", "Dreadnought"}; 
        jugador1 = new PanelJugador("Jugador 1", equipos);
        jugador2 = new PanelJugador("Jugador 2", equipos);

        panelCentral.add(jugador1);
        panelCentral.add(jugador2);
        mainPanel.add(panelCentral, BorderLayout.CENTER);

        // Botón Continuar
        JButton botonContinuar = new JButton("CONTINUAR");
        botonContinuar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        botonContinuar.setForeground(Color.WHITE);
        botonContinuar.setBackground(new Color(46, 125, 50)); // Verde
        botonContinuar.setFocusPainted(false);
        botonContinuar.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        botonContinuar.setPreferredSize(new Dimension(200, 50));
        botonContinuar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        botonContinuar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { botonContinuar.setBackground(new Color(60, 150, 60)); }
            public void mouseExited(MouseEvent e) { botonContinuar.setBackground(new Color(46, 125, 50)); }
        });

        botonContinuar.addActionListener(e -> {
             String equipo1 = jugador1.getEquipoSeleccionado();
             String equipo2 = jugador2.getEquipoSeleccionado();
             dispose();
             new AjustesPartida(equipo1, equipo2).setVisible(true);
        });

        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.add(botonContinuar);
        mainPanel.add(panelBoton, BorderLayout.SOUTH);
        
        SwingUtilities.invokeLater(() -> {
            jugador1.actualizarImagen();
            jugador2.actualizarImagen();
        });
    }

    class PanelJugador extends JPanel {
        private static final long serialVersionUID = 1L;
        private JLabel labelEquipo;
        private JLabel imagenEquipoLabel;
        private String[] equiposDisponibles;
        private int indiceActual = 0;

        public PanelJugador(String nombre, String[] equipos) {
            this.equiposDisponibles = equipos;
            setLayout(new BorderLayout());
            setOpaque(false);
            
            setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), 
                nombre, 0, 0, new Font("Segoe UI", Font.BOLD, 16), Color.WHITE
            ));

            labelEquipo = new JLabel(equipos[indiceActual], JLabel.CENTER);
            labelEquipo.setFont(new Font("Segoe UI", Font.BOLD, 20));
            labelEquipo.setForeground(Color.CYAN);
            add(labelEquipo, BorderLayout.NORTH);

            imagenEquipoLabel = new FadeLabel();
            imagenEquipoLabel.setHorizontalAlignment(JLabel.CENTER);
            add(imagenEquipoLabel, BorderLayout.CENTER);
            
            // Panel de Botones (Flechas)
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Espacio entre flechas
            panelBotones.setOpaque(false);
            
            JButton btnIzq = crearFlecha("◀");
            JButton btnDer = crearFlecha("▶");

            btnIzq.addActionListener(e -> {
                indiceActual = (indiceActual - 1 + equiposDisponibles.length) % equiposDisponibles.length;
                actualizarImagen();
            });
            btnDer.addActionListener(e -> {
                indiceActual = (indiceActual + 1) % equiposDisponibles.length;
                actualizarImagen();
            });

            panelBotones.add(btnIzq);
            panelBotones.add(btnDer);
            add(panelBotones, BorderLayout.SOUTH);
        }
        
        // Crea una flecha transparente (solo texto)
        private JButton crearFlecha(String txt) {
            JButton b = new JButton(txt);
            b.setContentAreaFilled(false); // Quita el fondo
            b.setBorderPainted(false);     // Quita el borde cuadrado
            b.setFocusPainted(false);      // Quita el foco
            b.setForeground(Color.WHITE);
            b.setFont(new Font("Segoe UI", Font.BOLD, 30)); // Flecha grande
            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            // Efecto Hover (Brillo)
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setForeground(Color.CYAN); }
                public void mouseExited(MouseEvent e) { b.setForeground(Color.WHITE); }
            });
            return b;
        }

        public void actualizarImagen() {
            String nombreEquipo = equiposDisponibles[indiceActual];
            labelEquipo.setText(nombreEquipo);
            
            try {
                ImageIcon icon = new ImageIcon("resources/images/Team/" + nombreEquipo + "Team.png");
                if(icon.getIconWidth() == -1) { 
                    imagenEquipoLabel.setText("Sin Imagen"); 
                    return;
                }
                
                Image img = icon.getImage();
                int w = Math.max(200, getWidth() - 40);
                int h = Math.max(200, getHeight() - 100);
                
                if (imagenEquipoLabel.getWidth() > 0) {
                     w = imagenEquipoLabel.getWidth();
                     h = imagenEquipoLabel.getHeight();
                }
                
                float ratio = Math.min((float)w/img.getWidth(null), (float)h/img.getHeight(null));
                if(ratio > 0) {
                    Image imgEscalada = img.getScaledInstance((int)(img.getWidth(null)*ratio), (int)(img.getHeight(null)*ratio), Image.SCALE_SMOOTH);
                    transicionconFade(new ImageIcon(imgEscalada));
                }
            } catch(Exception ex) { ex.printStackTrace(); }
        }

        private void transicionconFade(ImageIcon iconnuevo) {
            FadeLabel label = (FadeLabel) imagenEquipoLabel;
            ImageIcon iconActual = (ImageIcon) label.getIcon();

            if (iconActual == null) {
                label.setIcon(iconnuevo);
                return;
            }

            Timer timerOut = new Timer(20, null);
            final float[] opacidadOut = {1f};
            timerOut.addActionListener(e -> {
                opacidadOut[0] -= 0.1f;
                if (opacidadOut[0] <= 0f) {
                    opacidadOut[0] = 0f;
                    label.setAlpha(0f);
                    label.setIcon(iconnuevo); 
                    
                    Timer timerIn = new Timer(20, null);
                    final float[] opacidadIn = {0f};
                    timerIn.addActionListener(ev -> {
                        opacidadIn[0] += 0.1f;
                        if (opacidadIn[0] >= 1f) {
                            opacidadIn[0] = 1f;
                            timerIn.stop();
                        }
                        label.setAlpha(opacidadIn[0]);
                    });
                    timerIn.start();
                    timerOut.stop();
                } else {
                    label.setAlpha(opacidadOut[0]);
                }
            });
            timerOut.start();
        }

        public String getEquipoSeleccionado() { return equiposDisponibles[indiceActual]; }
    }
    
    class FadeLabel extends JLabel {
        private static final long serialVersionUID = 1L;
        private float alpha = 1f; 
        public void setAlpha(float alpha) { this.alpha = alpha; repaint(); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2d);
            g2d.dispose();
        }
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
            g2.setPaint(new GradientPaint(0, 0, new Color(10, 40, 70), 0, getHeight(), new Color(5, 20, 30)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}