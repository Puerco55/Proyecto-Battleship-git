package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import db.EstadisticasDAO;

public class MenuOpciones extends JFrame {

    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;
    
    // COLORES
    private final Color COLOR_BOTON = new Color(0, 137, 123); 
    private final Color COLOR_BOTON_HOVER = new Color(0, 77, 64);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_PELIGRO = new Color(211, 47, 47);
    private final Color COLOR_PELIGRO_HOVER = new Color(183, 28, 28);

    public MenuOpciones() {
        dao = new EstadisticasDAO();
        
        setTitle("Opciones");
        setSize(450, 500); // Reducido ligeramente al quitar el texto largo
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); 

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("CONFIGURACIÓN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(COLOR_TEXTO);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // --- NUEVA SECCIÓN: CRÉDITOS (En lugar de Instrucciones) ---
        JLabel lblCreditos = new JLabel("Créditos del Proyecto:");
        lblCreditos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCreditos.setForeground(Color.CYAN);
        lblCreditos.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblCreditos);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JTextArea areaCreditos = new JTextArea();
        areaCreditos.setEditable(false);
        areaCreditos.setOpaque(false);
        areaCreditos.setForeground(Color.WHITE);
        areaCreditos.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // Puedes cambiar estos nombres
        areaCreditos.setText(
            "Desarrollado por el Grupo 14\n\n" +
            "• Asignatura: Programación III\n" +
            "• Versión: 1.0 Release"
        );
        // Centrar el texto
        areaCreditos.setAlignmentX(Component.CENTER_ALIGNMENT); 
        // Un pequeño truco para centrar el contenido del area
        areaCreditos.setMargin(new Insets(0, 50, 0, 0)); 
        
        centerPanel.add(areaCreditos);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // --- ZONA DE GESTIÓN DE DATOS ---
        JLabel lblZonaPeligro = new JLabel("Gestión de Base de Datos");
        lblZonaPeligro.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblZonaPeligro.setForeground(new Color(255, 100, 100));
        lblZonaPeligro.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(lblZonaPeligro);
        
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JButton btnBorrar = crearBotonEstilizado("Borrar Historial", true);
        btnBorrar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBorrar.addActionListener(e -> confirmarBorrado());
        centerPanel.add(btnBorrar);

        centerPanel.add(Box.createVerticalGlue()); 

        // Botón Volver
        JButton backButton = crearBotonEstilizado("Volver", false);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(backButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
    }

    private void confirmarBorrado() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que quieres borrar TODO el historial?",
            "¡ADVERTENCIA!", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            dao.borrarHistorial();
            JOptionPane.showMessageDialog(this, "Historial eliminado.", "Base de Datos Limpia", JOptionPane.INFORMATION_MESSAGE);
        }
    }

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

    class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(0, 96, 100), 0, getHeight(), new Color(0, 30, 40));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}