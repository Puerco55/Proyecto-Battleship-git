package gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PantallaInicioJuego extends JFrame {
    private static final long serialVersionUID = 1L;
    private int[][] tableroJugador1;
    private int[][] tableroJugador2;

    private final Color COLOR_BOTON = new Color(30, 136, 229);
    private final Color COLOR_TEXTO = Color.WHITE;

    public PantallaInicioJuego(int superDisparos, int megaDisparos, int escudos,
            int[][] tableroJ1, int[][] tableroJ2) {
        this.tableroJugador1 = tableroJ1;
        this.tableroJugador2 = tableroJ2;

        setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 300);
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

        // Texto
        JLabel mensaje = new JLabel("Ambos jugadores han colocado sus barcos");
        mensaje.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        mensaje.setForeground(COLOR_TEXTO);
        mensaje.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(mensaje, BorderLayout.CENTER);

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
                VentanaJuego ventanaJuego = new VentanaJuego(
                        1,
                        superDisparos,
                        megaDisparos,
                        escudos,
                        tableroJugador1,
                        tableroJugador2);
                ventanaJuego.setVisible(true);
                dispose();
            }
        });
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                new EmptyBorder(10, 40, 10, 40)));
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