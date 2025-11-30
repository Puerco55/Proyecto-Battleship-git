package battleship;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;


public class MenuEstadisticas extends JFrame {
    
    private static final long serialVersionUID = 1L;
    private EstadisticasDAO dao;

    public MenuEstadisticas() {
        dao = new EstadisticasDAO();

        setTitle("Estadísticas Históricas");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(null);
        setResizable(false);
        
        //Obtener lista d datos
        List<Partida> historial = dao.obtenerHistorial();

        // Panel Principal
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Título
        JLabel titulo = new JLabel("Historial de Batallas", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(titulo, BorderLayout.NORTH);

        // Crear tabla
        String[] columnas = {"Fecha", "Ganador", "Turnos", "Barcos Hundidos"};
        Object[][] filas = new Object[historial.size()][4];

        for (int i = 0; i < historial.size(); i++) {
            Partida p = historial.get(i);
            filas[i][0] = p.getFecha();
            filas[i][1] = p.getGanador();
            filas[i][2] = p.getTurnos();
            filas[i][3] = p.getBarcosHundidos();
        }

        JTable tabla = new JTable(filas, columnas);
        tabla.setEnabled(false);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(130);
        tabla.setFont(new Font("Arial", Font.PLAIN, 14));
        tabla.setRowHeight(22);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        add(new JLabel("Historial de Batallas", SwingConstants.CENTER), BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        
        // Aseguramos que la tabla exista (por si es la primera vez que se abre)
        GestorBaseDatos.inicializarTablas();
    }

    
    //Método auxiliar para crear un panel bonito para cada dato
    //Tmbn dejo esto, no hace danio a nadie
//    private JPanel crearTarjetaDato(String titulo, String valor, Color colorFondo) {
//        JPanel tarjeta = new JPanel(new BorderLayout());
//        tarjeta.setBackground(colorFondo);
//        tarjeta.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
//        
//        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
//        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));
//        lblTitulo.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
//        
//        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
//        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
//        
//        tarjeta.add(lblTitulo, BorderLayout.NORTH);
//        tarjeta.add(lblValor, BorderLayout.CENTER);
//        
//        return tarjeta;
//    }
}