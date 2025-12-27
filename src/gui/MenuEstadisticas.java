package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import db.EstadisticasDAO;
import domain.Partida;

public class MenuEstadisticas extends JFrame {

    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;

    // COLORES: Tema Púrpura
    private final Color COLOR_BOTON = new Color(106, 27, 154); 
    private final Color COLOR_BOTON_HOVER = new Color(74, 20, 140);
    private final Color COLOR_TEXTO = Color.WHITE;

    public MenuEstadisticas() {
        // Inicializar DAO
        dao = new EstadisticasDAO();

        setTitle("Estadísticas Históricas");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // 1. Fondo Degradado
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        setContentPane(mainPanel);

        // 2. Título
        JLabel titleLabel = new JLabel("RANKING DE BATALLAS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(COLOR_TEXTO);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // 3. Tabla con Datos Reales
        // Recuperar historial de la BD
        List<Partida> historial = dao.obtenerHistorial();
        
        String[] columnas = {"Fecha", "Ganador", "Turnos", "Hundidos"};
        
        // Modelo de tabla no editable
        DefaultTableModel model = new DefaultTableModel(columnas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Llenar el modelo con los datos
        for (Partida p : historial) {
            Object[] fila = {
                p.getFecha(),
                p.getGanador(),
                p.getTurnos(),
                p.getBarcosHundidos()
            };
            model.addRow(fila);
        }

        JTable table = new JTable(model);
        estilizarTabla(table); // Aplicar estilo transparente

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,50)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 4. Botón Cerrar
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(20,0,0,0));
        
        JButton closeButton = crearBotonEstilizado("Cerrar");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        btnPanel.add(closeButton);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    private void estilizarTabla(JTable table) {
        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setShowGrid(false);
        
        // Ancho de columnas
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Fecha
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,0,0,150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setOpaque(false);
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
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(COLOR_BOTON_HOVER); }
            public void mouseExited(MouseEvent e) { btn.setBackground(COLOR_BOTON); }
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
            // DEGRADADO: Violeta oscuro a Negro
            GradientPaint gp = new GradientPaint(0, 0, new Color(74, 20, 140), 0, getHeight(), new Color(10, 5, 20));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}