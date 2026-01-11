package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
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
    private JTable table;
    private DefaultTableModel model;
    
    // Lista local para mantener referencia a los IDs de las partidas mostradas
    private List<Partida> listaPartidasActual;

    private final Color COLOR_BOTON = new Color(106, 27, 154);
    private final Color COLOR_TEXTO = Color.WHITE;

    public MenuEstadisticas() {
        dao = new EstadisticasDAO();

        setTitle("Estadísticas Históricas y Gestión de BD");
        setSize(700, 550); 
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Título
        JLabel titleLabel = new JLabel("BASE DE DATOS: PARTIDAS", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(COLOR_TEXTO);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Configuración de Tabla
        String[] columnas = {"ID", "Fecha", "Ganador", "Turnos", "Hundidos"};
        model = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        estilizarTabla(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila a la vez

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,50)));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarDatosTabla();

        // PANEL DE BOTONES (CRUD)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        btnPanel.setOpaque(false);
        
        // 1. Botón DETALLES (Muestra tabla relacionada)
        JButton btnDetalles = crearBotonEstilizado("Ver Detalles (Tabla 2)");
        btnDetalles.addActionListener(e -> mostrarDetalles());
        
        // 2. Botón MODIFICAR (Update)
        JButton btnModificar = crearBotonEstilizado("Corregir Ganador");
        btnModificar.addActionListener(e -> modificarGanador());
        
        // 3. Botón BORRAR (Delete)
        JButton btnBorrar = crearBotonEstilizado("Borrar Partida");
        btnBorrar.setBackground(new Color(180, 40, 40)); // Rojo para borrar
        btnBorrar.addActionListener(e -> borrarPartida());

        btnPanel.add(btnDetalles);
        btnPanel.add(btnModificar);
        btnPanel.add(btnBorrar);
        
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
    }

    // Carga los datos de la BD en la tabla
    private void cargarDatosTabla() {
        model.setRowCount(0); // Limpiar tabla
        listaPartidasActual = dao.obtenerHistorial(); // Traer de BD
        
        for (Partida p : listaPartidasActual) {
            Object[] fila = {
                p.getId(), // Mostramos ID para claridad académica
                p.getFecha(),
                p.getGanador(),
                p.getTurnos(),
                p.getBarcosHundidos()
            };
            model.addRow(fila);
        }
    }

    // Acción: Ver Detalles (Relación entre tablas)
    private void mostrarDetalles() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una partida para ver sus detalles.");
            return;
        }
        
        // Obtener el ID real del objeto Partida
        Partida p = listaPartidasActual.get(fila);
        
        // Consultar la segunda tabla
        List<String> detalles = dao.obtenerDetallesPartida(p.getId());
        
        if (detalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Esta partida no tiene detalles extra guardados (Tabla 2 vacía).");
        } else {
            String mensaje = "Detalles recuperados de la tabla 'detalles_partida':\n\n";
            for(String d : detalles) {
                mensaje += "- " + d + "\n";
            }
            JOptionPane.showMessageDialog(this, mensaje);
        }
    }

    // Acción: Modificar (Update)
    private void modificarGanador() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una partida para modificar.");
            return;
        }
        Partida p = listaPartidasActual.get(fila);
        
        String nuevoGanador = JOptionPane.showInputDialog(this, "Introduce el nuevo nombre del ganador:", p.getGanador());
        
        if (nuevoGanador != null && !nuevoGanador.trim().isEmpty()) {
            dao.corregirGanador(p.getId(), nuevoGanador); // Llamada a UPDATE
            cargarDatosTabla(); // Refrescar tabla
            JOptionPane.showMessageDialog(this, "Ganador actualizado correctamente.");
        }
    }

    // Acción: Borrar (Delete)
    private void borrarPartida() {
        int fila = table.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una partida para borrar.");
            return;
        }
        Partida p = listaPartidasActual.get(fila);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Seguro que quieres borrar la partida ID " + p.getId() + "?", 
                "Confirmar Borrado", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            dao.borrarPartidaPorId(p.getId()); // Llamada a DELETE
            cargarDatosTabla(); // Refrescar tabla
        }
    }

    // Estilos visuales 
    private void estilizarTabla(JTable table) {
        table.setOpaque(false);
        ((DefaultTableCellRenderer)table.getDefaultRenderer(Object.class)).setOpaque(false);
        table.setForeground(Color.WHITE);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setShowGrid(false);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,0,0,150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // --- BLOQUEO DE COLUMNAS ---
        header.setReorderingAllowed(false); // Impide arrastrar columnas para cambiar el orden
        header.setResizingAllowed(false);   // Impide cambiar el tamaño (ancho) de las columnas
    }

    private JButton crearBotonEstilizado(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(COLOR_BOTON);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Borde simple
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                new EmptyBorder(8, 15, 8, 15)));
        return btn;
    }

    class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            GradientPaint gp = new GradientPaint(0, 0, new Color(74, 20, 140), 0, getHeight(), new Color(10, 5, 20));
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}