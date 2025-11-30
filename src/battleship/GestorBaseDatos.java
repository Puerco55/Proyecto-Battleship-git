package battleship;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GestorBaseDatos {

    //Nombre del archivo de la base de datos
    private static final String URL = "jdbc:sqlite:battleship_db.sqlite";

    public static Connection conectar() {
        Connection connection = null;
        try {
            //Crear conexión
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD: " + e.getMessage());
        }
        return connection;
    }

    
    //Crea la tabla de estadísticas si no existe.
    public static void inicializarTablas() {
        String sql = "CREATE TABLE IF NOT EXISTS partidas ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fecha TEXT NOT NULL,"
                + " ganador TEXT NOT NULL," // Jugador uno o jugador dos
                + " turnos INTEGER NOT NULL,"
                + " barcos_hundidos INTEGER NOT NULL"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'partidas' verificada/creada correctamente.");
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
    
    //En caso d no crear la columna barco hundidos
    public static void actualizarTabla() {
        String sql = "ALTER TABLE partidas ADD COLUMN barcos_hundidos INTEGER DEFAULT 0";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Columna barcos_hundidos añadida.");
        } catch (SQLException e) {
            System.out.println("Aviso: " + e.getMessage());
        }
    }

}
