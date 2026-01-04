package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorBaseDatos {

    private static final String URL = "jdbc:sqlite:battleship_db.sqlite";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el driver de SQLite.");
            e.printStackTrace();
        }
    }

    public static Connection conectar() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
            // IMPORTANTE: En SQLite hay que activar las Foreign Keys explícitamente
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON;"); 
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD: " + e.getMessage());
        }
        return connection;
    }

    public static void inicializarTablas() {
        // Tabla 1: Partidas (La principal)
        String sqlPartidas = "CREATE TABLE IF NOT EXISTS partidas ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fecha TEXT NOT NULL,"
                + " ganador TEXT NOT NULL," 
                + " turnos INTEGER NOT NULL,"
                + " barcos_hundidos INTEGER NOT NULL"
                + ");";

        // Tabla 2: Detalles (Relacionada con partidas por id_partida)
        // [CUMPLE REQUISITO: Diseño de dos tablas relacionadas]
        String sqlDetalles = "CREATE TABLE IF NOT EXISTS detalles_partida ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_partida INTEGER NOT NULL,"
                + " clave TEXT NOT NULL,"   // Ej: "Dificultad", "Precision_J1"
                + " valor TEXT NOT NULL,"
                + " FOREIGN KEY(id_partida) REFERENCES partidas(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sqlPartidas);
                stmt.execute(sqlDetalles);
                System.out.println("Base de datos y tablas relacionadas inicializadas correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
}