package battleship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EstadisticasDAO {
    
     //Guarda el resultado de una partida al finalizar
	
    public void guardarPartida(String ganador, int turnos) {
        String sql = "INSERT INTO partidas(fecha, ganador, turnos) VALUES(?,?,?)";

        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            
            pstmt.setString(1, fechaActual);
            pstmt.setString(2, ganador);
            pstmt.setInt(3, turnos);
            pstmt.executeUpdate();
            
            System.out.println("Partida guardada en la base de datos.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Obtiene un objeto con los datos resumidos.
     */
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