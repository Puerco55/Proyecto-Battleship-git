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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import db.GestorBaseDatos;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Colores
    private final Color COLOR_BOTON = new Color(30, 136, 229);
    private final Color COLOR_TEXTO = Color.WHITE;

    public MainMenu() {
    	setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(450, 550); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        // Inicializar BD
        GestorBaseDatos.inicializarTablas();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        // Fondo Degradado
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        setContentPane(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("HUNDIR LA FLOTA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(COLOR_TEXTO);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Botones
        JPanel buttonContainer = new JPanel();
        buttonContainer.setLayout(new BoxLayout(buttonContainer, BoxLayout.Y_AXIS));
        buttonContainer.setOpaque(false);
        mainPanel.add(buttonContainer, BorderLayout.CENTER);
        
        JButton playButton = crearBotonEstilizado("Jugar");
        JButton optionsButton = crearBotonEstilizado("Opciones");
        JButton statsButton = crearBotonEstilizado("Estadísticas");
        JButton exitButton = crearBotonEstilizado("Salir");
        
        buttonContainer.add(Box.createVerticalGlue());
        agregarBoton(buttonContainer, playButton);
        agregarBoton(buttonContainer, optionsButton);
        agregarBoton(buttonContainer, statsButton);
        agregarBoton(buttonContainer, exitButton);
        buttonContainer.add(Box.createVerticalGlue());

        // LISTENERS
        
        // Seleccion de perfil
        playButton.addActionListener(e -> {
            new VentanaComoJugar().setVisible(true); 
            dispose(); 
        });

        optionsButton.addActionListener(e -> new MenuOpciones().setVisible(true));
        
        statsButton.addActionListener(e -> {
            MenuEstadisticas ventanaStats = new MenuEstadisticas();
            ventanaStats.setVisible(true);
        });

        exitButton.addActionListener(e -> confirmarSalida());
    }
    
    private void agregarBoton(JPanel panel, JButton boton) {
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(boton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                new EmptyBorder(10, 40, 10, 40)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(250, 50));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_BOTON.brighter()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(COLOR_BOTON); }
        });
        return btn;
    }
    
    private void confirmarSalida() {
        int respuesta = JOptionPane.showConfirmDialog(this, "¿Seguro que quieres salir?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) System.exit(0);
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
}