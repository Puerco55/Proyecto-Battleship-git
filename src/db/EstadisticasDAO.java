package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import domain.Partida;

public class EstadisticasDAO {

    // 1. INSERCIÓN (CREATE)
    public int guardarPartida(String ganador, int turnos, int barcosHundidos) {
        String sql = "INSERT INTO partidas(fecha, ganador, turnos, barcos_hundidos) VALUES(?,?,?,?)";
        int generatedId = -1;

        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            pstmt.setString(1, fechaActual);
            pstmt.setString(2, ganador);
            pstmt.setInt(3, turnos);
            pstmt.setInt(4, barcosHundidos);
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                        
                        // --- AQUÍ ESTÁ EL CAMBIO: SIN CLIMA ---
                        // Guardamos solo datos relevantes para cumplir el requisito de 2 tablas
                        guardarDetalle(generatedId, "Modo", "Clásico");
                        guardarDetalle(generatedId, "Eficiencia", (barcosHundidos * 20) + "%");
                        // guardarDetalle(generatedId, "Clima", "Soleado"); // <-- ELIMINADO
                    }
                }
            }
            System.out.println("-> Partida guardada (ID: " + generatedId + ")");
        } catch (SQLException e) {
            System.out.println("Error guardarPartida: " + e.getMessage());
        }
        return generatedId;
    }
    
    // Método auxiliar para la segunda tabla
    public void guardarDetalle(int idPartida, String clave, String valor) {
        String sql = "INSERT INTO detalles_partida(id_partida, clave, valor) VALUES(?,?,?)";
        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartida);
            pstmt.setString(2, clave);
            pstmt.setString(3, valor);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. CONSULTA (READ)
    public List<Partida> obtenerHistorial() {
        List<Partida> historial = new ArrayList<>();
        String sql = "SELECT id, fecha, ganador, turnos, barcos_hundidos FROM partidas ORDER BY id DESC";
        try (Connection conn = GestorBaseDatos.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                historial.add(new Partida(
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("ganador"),
                        rs.getInt("turnos"),
                        rs.getInt("barcos_hundidos")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return historial;
    }
    
    public List<String> obtenerDetallesPartida(int idPartida) {
        List<String> detalles = new ArrayList<>();
        String sql = "SELECT clave, valor FROM detalles_partida WHERE id_partida = ?";
        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartida);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) detalles.add(rs.getString("clave") + ": " + rs.getString("valor"));
        } catch (SQLException e) { e.printStackTrace(); }
        return detalles;
    }

    // 3. MODIFICACIÓN (UPDATE)
    public void corregirGanador(int idPartida, String nuevoGanador) {
        String sql = "UPDATE partidas SET ganador = ? WHERE id = ?";
        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevoGanador);
            pstmt.setInt(2, idPartida);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    // 4. BORRADO ESPECÍFICO (DELETE)
    public void borrarPartidaPorId(int idPartida) {
        String sql = "DELETE FROM partidas WHERE id = ?";
        try (Connection conn = GestorBaseDatos.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, idPartida);
            pstmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    
    // 5. BORRADO TOTAL Y REINICIO DE IDs (RESET)
    // Este método borra todo y pone el contador a 0 de nuevo.
    public void borrarHistorial() {
        String sqlDelete = "DELETE FROM partidas";
        // Comando mágico para resetear el contador AUTOINCREMENT en SQLite:
        String sqlResetSeq = "DELETE FROM sqlite_sequence WHERE name='partidas'"; 
        
        try (Connection conn = GestorBaseDatos.conectar(); 
             Statement stmt = conn.createStatement()) {
            
            // Borramos los datos
            stmt.executeUpdate(sqlDelete);
            // Reseteamos el contador de ID
            stmt.executeUpdate(sqlResetSeq);
            
            System.out.println("Historial borrado y contador de IDs reiniciado.");
            
        } catch (SQLException e) {
            System.out.println("Error al borrar historial: " + e.getMessage());
        }
    }
    
    // (Opcional) Método para obtener estadísticas generales
    public ResumenEstadisticas obtenerResumen() {
        // Implementación simplificada o la que tenías antes
        return new ResumenEstadisticas(0,0,0); 
    }
    
    public static class ResumenEstadisticas {
        public int total, victoriasJ1, victoriasJ2;
        public ResumenEstadisticas(int t, int v1, int v2) { this.total=t; this.victoriasJ1=v1; this.victoriasJ2=v2; }
    }
}