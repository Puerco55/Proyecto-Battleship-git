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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import db.EstadisticasDAO;

public class MenuOpciones extends JFrame {

    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;
    
    // COLORES
    private final Color COLOR_BOTON = new Color(0, 137, 123); 
    private final Color COLOR_BOTON_HOVER = new Color(0, 77, 64);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_PELIGRO = new Color(211, 47, 47); // Rojo para borrar
    private final Color COLOR_PELIGRO_HOVER = new Color(183, 28, 28);

    public MenuOpciones() {
        dao = new EstadisticasDAO();
        
        setTitle("Opciones y Ayuda");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 

        // 1. Fondo Degradado
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        // 2. Título
        JLabel titleLabel = new JLabel("CONFIGURACIÓN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXTO);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 3. Panel Central
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- ZONA DE INSTRUCCIONES ---
        JLabel lblAyuda = new JLabel("Instrucciones de Juego:");
        lblAyuda.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAyuda.setForeground(Color.CYAN);
        lblAyuda.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblAyuda);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea areaInstrucciones = new JTextArea();
        areaInstrucciones.setEditable(false);
        areaInstrucciones.setLineWrap(true);
        areaInstrucciones.setWrapStyleWord(true);
        areaInstrucciones.setOpaque(false); // Transparente
        areaInstrucciones.setForeground(Color.WHITE);
        areaInstrucciones.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        areaInstrucciones.setText(
            "1. PREPARACIÓN:\n" +
            "   Coloca tus barcos en secreto. Tienes diferentes tamaños.\n\n" +
            "2. EL JUEGO:\n" +
            "   Turnaos para disparar. ¡Hunde la flota enemiga!\n" +
            "   - Disparo Normal: 1 casilla.\n" +
            "   - Super Disparo: 5 casillas en cruz.\n" +
            "   - Mega Disparo: 9 casillas (3x3).\n\n" +
            "3. OBJETIVO:\n" +
            "   Sé el último capitán en pie."
        );
        areaInstrucciones.setCaretPosition(0);
        
        JScrollPane scrollInstrucciones = new JScrollPane(areaInstrucciones);
        scrollInstrucciones.setOpaque(false);
        scrollInstrucciones.getViewport().setOpaque(false);
        scrollInstrucciones.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,50)));
        scrollInstrucciones.setMaximumSize(new Dimension(400, 200));
        
        centerPanel.add(scrollInstrucciones);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // --- ZONA DE GESTIÓN DE DATOS ---
        JLabel lblZonaPeligro = new JLabel("Gestión de Base de Datos");
        lblZonaPeligro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblZonaPeligro.setForeground(new Color(255, 100, 100)); // Rojizo
        lblZonaPeligro.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblZonaPeligro);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Botón Borrar Historial (Rojo)
        JButton btnBorrar = crearBotonEstilizado("Borrar Historial", true);
        btnBorrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBorrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarBorrado();
            }
        });
        centerPanel.add(btnBorrar);

        centerPanel.add(Box.createVerticalGlue()); 

        // 4. Botón Volver (Normal)
        JButton backButton = crearBotonEstilizado("Volver", false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); 
            }
        });
        
        // Panel inferior para el botón volver
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void confirmarBorrado() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que quieres borrar TODO el historial?\nSe perderán todas las estadísticas permanentemente.",
            "¡ADVERTENCIA!",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            dao.borrarHistorial();
            JOptionPane.showMessageDialog(this, 
                "El historial ha sido eliminado, Comandante.", 
                "Base de Datos Limpia", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Método para estilo de botones (con opción de "Peligro")
    private JButton crearBotonEstilizado(String texto, boolean esPeligro) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(COLOR_TEXTO);
        
        Color bgNormal = esPeligro ? COLOR_PELIGRO : COLOR_BOTON;
        Color bgHover = esPeligro ? COLOR_PELIGRO_HOVER : COLOR_BOTON_HOVER;
        
        btn.setBackground(bgNormal);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                new EmptyBorder(8, 20, 8, 20)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(250, 40));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgNormal); }
        });
        return btn;
    }

    // Clase interna para el fondo degradado
    class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            int w = getWidth(), h = getHeight();
            // DEGRADADO: Verde Mar (Arriba) a Azul Oscuro (Abajo)
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 96, 100), 0, h, new Color(0, 30, 40));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, w, h);
        }
    }
}