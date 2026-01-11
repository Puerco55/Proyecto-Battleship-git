package db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import main.Configuracion;

public class GestorBaseDatos {

    // Obtenemos la URL desde nuestro archivo de configuración
    private static final String URL = Configuracion.getDbUrl();

    static {
        try {
            Class.forName("org.sqlite.JDBC");

            // --- LÓGICA DE CREACIÓN DE CARPETAS ---
            // Extraemos la ruta del archivo de la URL JDBC (quitamos "jdbc:sqlite:")
            String rutaArchivo = URL.replace("jdbc:sqlite:", "");
            File archivoDb = new File(rutaArchivo);
            File carpetaPadre = archivoDb.getParentFile();

            // Si la carpeta no existe (ej: resources/db), la creamos
            if (carpetaPadre != null && !carpetaPadre.exists()) {
                if (carpetaPadre.mkdirs()) {
                    System.out.println("-> Carpeta de base de datos creada: " + carpetaPadre.getPath());
                }
            }
            // --------------------------------------

        } catch (ClassNotFoundException e) {
            System.err.println("Error crítico: No se encontró el driver de SQLite.");
            e.printStackTrace();
        }
    }

    public static Connection conectar() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
            Statement stmt = connection.createStatement();
            // Activar Foreign Keys en SQLite
            stmt.execute("PRAGMA foreign_keys = ON;"); 
            stmt.close();
        } catch (SQLException e) {
            System.out.println("Error al conectar con la BD: " + e.getMessage());
        }
        return connection;
    }

    public static void inicializarTablas() {
        String sqlPartidas = "CREATE TABLE IF NOT EXISTS partidas ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " fecha TEXT NOT NULL,"
                + " ganador TEXT NOT NULL," 
                + " turnos INTEGER NOT NULL,"
                + " barcos_hundidos INTEGER NOT NULL"
                + ");";

        String sqlDetalles = "CREATE TABLE IF NOT EXISTS detalles_partida ("
                + " id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + " id_partida INTEGER NOT NULL,"
                + " clave TEXT NOT NULL,"
                + " valor TEXT NOT NULL,"
                + " FOREIGN KEY(id_partida) REFERENCES partidas(id) ON DELETE CASCADE"
                + ");";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            if (conn != null) {
                stmt.execute(sqlPartidas);
                stmt.execute(sqlDetalles);
                System.out.println("Base de datos inicializada correctamente en: " + URL);
            }
        } catch (SQLException e) {
            System.out.println("Error al crear tablas: " + e.getMessage());
        }
    }
}