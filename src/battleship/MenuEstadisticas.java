package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuEstadisticas extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;

    public MenuEstadisticas() {
        dao = new EstadisticasDAO();
        
        // Configuración de la ventana
        setTitle("Estadísticas Históricas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Obtener datos de la BD
        EstadisticasDAO.ResumenEstadisticas datos = dao.obtenerResumen();

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Título
        JLabel titulo = new JLabel("Historial de Batallas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(titulo, BorderLayout.NORTH);

        // Panel de Datos (Grid)
        JPanel statsPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        
        // Crear tarjetas para cada estadística
        statsPanel.add(crearTarjetaDato("Partidas Jugadas", String.valueOf(datos.total), Color.LIGHT_GRAY));
        statsPanel.add(crearTarjetaDato("Victorias", String.valueOf(datos.victorias), new Color(144, 238, 144))); // Verde claro
        statsPanel.add(crearTarjetaDato("Derrotas", String.valueOf(datos.derrotas), new Color(255, 182, 193))); // Rojo claro
        
        mainPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Aseguramos que la tabla exista (por si es la primera vez que se abre)
        GestorBaseDatos.inicializarTablas();
    }

    /**
     * Método auxiliar para crear un panel bonito para cada dato
     */
    private JPanel crearTarjetaDato(String titulo, String valor, Color colorFondo) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(colorFondo);
        tarjeta.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        
        tarjeta.add(lblTitulo, BorderLayout.NORTH);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        
        return tarjeta;
    }
}