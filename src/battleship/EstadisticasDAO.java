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
    
    // Guarda el resultado de una partida al finalizar
    public void guardarPartida(String ganador, int turnos, int barcosHundidos) {
        String sql = "INSERT INTO partidas(fecha, ganador, turnos, barcos_hundidos) VALUES(?,?,?,?)";

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
    
    // Metodo obtener historial en modo lista para la tabla
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

    // He corregido este método para que busque "Jugador 1" y "Jugador 2"
    // que es lo que escribes en la clase VentanaJuego.
    public ResumenEstadisticas obtenerResumen() {
        String sqlTotal = "SELECT COUNT(*) FROM partidas";
        // Nota el espacio en 'Jugador 1'
        String sqlVictoriasJ1 = "SELECT COUNT(*) FROM partidas WHERE ganador = 'Jugador 1'";
        String sqlVictoriasJ2 = "SELECT COUNT(*) FROM partidas WHERE ganador = 'Jugador 2'";

        int total = 0, victoriasJ1 = 0, victoriasJ2 = 0;

        try (Connection conn = GestorBaseDatos.conectar()) {
            if (conn != null) {
                Statement stmt = conn.createStatement();

                // Consultar Total
                ResultSet rs = stmt.executeQuery(sqlTotal);
                if (rs.next()) total = rs.getInt(1);

                // Consultar Victorias J1
                rs = stmt.executeQuery(sqlVictoriasJ1);
                if (rs.next()) victoriasJ1 = rs.getInt(1);
                
                // Consultar Victorias J2
                rs = stmt.executeQuery(sqlVictoriasJ2);
                if (rs.next()) victoriasJ2 = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estadísticas: " + e.getMessage());
        }

        // Reutilizo tu clase interna, asignando J1 a victorias y J2 a derrotas (o viceversa según quieras verlo)
        // O podrías cambiar la clase interna para tener victoriasJ1 y victoriasJ2 explícitamente.
        return new ResumenEstadisticas(total, victoriasJ1, victoriasJ2);
    }
    // Método para borrar todo el historial
    public void borrarHistorial() {
        String sql = "DELETE FROM partidas";
        
        try (Connection conn = GestorBaseDatos.conectar();
             Statement stmt = conn.createStatement()) {
            
            int filasAfectadas = stmt.executeUpdate(sql);
            System.out.println("Historial borrado. Filas eliminadas: " + filasAfectadas);
            
        } catch (SQLException e) {
            System.out.println("Error al borrar historial: " + e.getMessage());
        }
    }
    
    public static class ResumenEstadisticas {
        public int total;
        public int victoriasJ1; // Cambiado nombre para claridad
        public int victoriasJ2; // Cambiado nombre para claridad

        public ResumenEstadisticas(int t, int v1, int v2) {
            this.total = t;
            this.victoriasJ1 = v1;
            this.victoriasJ2 = v2;
        }
    }
}