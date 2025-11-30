package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorBaseDatos {

    // Nombre del archivo de la base de datos
    private static final String URL = "jdbc:sqlite:battleship_db.sqlite";

    // Bloque estático para asegurar la carga del driver
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el driver de SQLite.");
            System.err.println("Asegúrate de incluir la librería sqlite-jdbc en tu proyecto.");
            e.printStackTrace();
        }
    }

    public static Connection conectar() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD: " + e.getMessage());
        }
        return connection;
    }

    // Crea la tabla de estadísticas si no existe.
    public static void inicializarTablas() {
        String sql = "CREATE TABLE IF NOT EXISTS partidas ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fecha TEXT NOT NULL,"
                + " ganador TEXT NOT NULL," 
                + " turnos INTEGER NOT NULL,"
                + " barcos_hundidos INTEGER NOT NULL"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sql);
                System.out.println("Base de datos inicializada correctamente.");
            }
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
}