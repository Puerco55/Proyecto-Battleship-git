package battleship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class EstadisticasDAO {
    
     //Guarda el resultado de una partida al finalizar
	
    public void guardarPartida(String ganador, int turnos, int barcosHundidos) {
        String sql = "INSERT INTO partidas(fecha, ganador, turnos, barcosHundidos) VALUES(?,?,?,?)";

        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            pstmt.setString(1, fechaActual);
            pstmt.setString(2, ganador);
            pstmt.setInt(3, turnos);
            pstmt.setInt(4, barcosHundidos);
            pstmt.executeUpdate();
            
            System.out.println("Partida guardada en la base de datos.");
            
        } catch (SQLException e) {
            System.out.println("Error al guardar partida: " + e.getMessage());
        }
    }
    
    //Metodo obtener historial en modo lista para la tabla
    public List<Partida> obtenerHistorial() {
        List<Partida> historial = new ArrayList<>();

        String sql = "SELECT fecha, ganador, turnos, barcos_hundidos FROM partidas ORDER BY id DESC";

        try (Connection conn = GestorBaseDatos.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                historial.add(new Partida(
                        rs.getString("fecha"),
                        rs.getString("ganador"),
                        rs.getInt("turnos"),
                        rs.getInt("barcos_hundidos")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener historial: " + e.getMessage());
        }

        return historial;
    }

    //Obtiene un objeto con los datos resumidos.
    //Ya no se usa pero lo dejo
    public ResumenEstadisticas obtenerResumen() {
        String sqlTotal = "SELECT COUNT(*) FROM partidas";
        String sqlVictorias = "SELECT COUNT(*) FROM partidas WHERE ganador = 'JUGADOR'";
        String sqlDerrotas = "SELECT COUNT(*) FROM partidas WHERE ganador = 'CPU'";

        int total = 0, victorias = 0, derrotas = 0;

        try (Connection conn = GestorBaseDatos.conectar()) {
            
            // Consultar Total
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sqlTotal);
            if (rs.next()) total = rs.getInt(1);

            // Consultar Victorias
            rs = stmt.executeQuery(sqlVictorias);
            if (rs.next()) victorias = rs.getInt(1);
            
            // Consultar Derrotas
            rs = stmt.executeQuery(sqlDerrotas);
            if (rs.next()) derrotas = rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error al obtener estadísticas: " + e.getMessage());
        }

        return new ResumenEstadisticas(total, victorias, derrotas);
    }
    
    // Clase interna simple para transportar los datos
    public static class ResumenEstadisticas {
        public int total;
        public int victorias;
        public int derrotas;

        public ResumenEstadisticas(int t, int v, int d) {
            this.total = t;
            this.victorias = v;
            this.derrotas = d;
        }
    }
    
}