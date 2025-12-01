package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class MenuOpciones extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;

    public MenuOpciones() {
        dao = new EstadisticasDAO();
        
        setTitle("Opciones y Ayuda");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(500, 550); 
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);
        
        // --- SECCIÓN 1: TÍTULO ---
        JLabel titulo = new JLabel("Configuración y Ayuda", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titulo, BorderLayout.NORTH);
        
        // --- SECCIÓN 2: CONTENIDO CENTRAL (Instrucciones) ---
        JPanel panelCentro = new JPanel(new BorderLayout(5, 5));
        panelCentro.setBorder(BorderFactory.createTitledBorder("Cómo Jugar"));
        
        JTextArea areaInstrucciones = new JTextArea();
        areaInstrucciones.setEditable(false);
        areaInstrucciones.setLineWrap(true);
        areaInstrucciones.setWrapStyleWord(true);
        areaInstrucciones.setFont(new Font("SansSerif", Font.PLAIN, 13));
        areaInstrucciones.setText(
            "1. PREPARACIÓN:\n" +
            "   Cada jugador coloca sus barcos en secreto. Tienes barcos de diferentes tamaños " +
            "y una cantidad limitada de disparos especiales.\n\n" +
            
            "2. EL JUEGO:\n" +
            "   Los jugadores se turnan para disparar al tablero del oponente.\n" +
            "   - Disparo Normal: Golpea 1 casilla.\n" +
            "   - Super Disparo: Golpea 5 casillas en cruz (+).\n" +
            "   - Mega Disparo: Golpea 9 casillas en área (3x3).\n\n" +
            
            "3. OBJETIVO:\n" +
            "   Hunde todos los barcos de tu enemigo antes de que él hunda los tuyos.\n\n" +
            
            "4. INFORMACIÓN:\n" +
            "   El cronómetro superior indica la duración de la partida. " +
            "   Las estadísticas se guardan automáticamente al finalizar."
        );
        areaInstrucciones.setCaretPosition(0); // Para que el scroll empiece arriba
        
        JScrollPane scrollInstrucciones = new JScrollPane(areaInstrucciones);
        panelCentro.add(scrollInstrucciones, BorderLayout.CENTER);
        
        mainPanel.add(panelCentro, BorderLayout.CENTER);
        
        // --- SECCIÓN 3: GESTIÓN DE DATOS (Botones inferiores) ---
        JPanel panelSur = new JPanel(new GridLayout(3, 1, 10, 10));
        
        // Panel para el borrado de datos
        JPanel panelBorrado = new JPanel(new BorderLayout());
        panelBorrado.setBorder(BorderFactory.createTitledBorder("Gestión de Base de Datos"));
        
        JLabel lblInfoBorrado = new JLabel("<html><center>¿Quieres reiniciar las estadísticas?<br>Esta acción no se puede deshacer.</center></html>", SwingConstants.CENTER);
        lblInfoBorrado.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton btnBorrarHistorial = new JButton("Borrar Historial de Partidas");
        btnBorrarHistorial.setBackground(new Color(255, 100, 100)); // Rojo suave
        btnBorrarHistorial.setForeground(Color.WHITE);
        btnBorrarHistorial.setFont(new Font("Arial", Font.BOLD, 12));
        btnBorrarHistorial.setPreferredSize(new Dimension(0, 35));
        
        panelBorrado.add(lblInfoBorrado, BorderLayout.CENTER);
        panelBorrado.add(btnBorrarHistorial, BorderLayout.SOUTH);
        
        // Panel de Créditos
        JLabel lblCreditos = new JLabel("Desarrollado para Proyecto Final de Programación", SwingConstants.CENTER);
        lblCreditos.setFont(new Font("Arial", Font.ITALIC, 11));
        lblCreditos.setForeground(Color.GRAY);

        // Añadir todo al panel sur
        panelSur.add(panelBorrado);
        panelSur.add(Box.createVerticalStrut(5));
        panelSur.add(lblCreditos);
        
        mainPanel.add(panelSur, BorderLayout.SOUTH);
        
        // --- ACCIONES ---
        btnBorrarHistorial.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarBorrado();
            }
        });
    }
    
    private void confirmarBorrado() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Estás seguro de que quieres borrar TODO el historial de partidas?\nSe perderán todas las estadísticas.",
            "Confirmar Borrado de Base de Datos",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (respuesta == JOptionPane.YES_OPTION) {
            dao.borrarHistorial();
            JOptionPane.showMessageDialog(this, 
                "El historial ha sido borrado correctamente.", 
                "Operación Exitosa", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}