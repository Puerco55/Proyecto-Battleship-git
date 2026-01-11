package test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import db.EstadisticasDAO;
import db.GestorBaseDatos;

import java.util.List;
import domain.Partida;

public class EstadisticasDAOTest {

    private EstadisticasDAO dao;

    @Before
    public void setUp() {
        // 1. Inicializamos el DAO y aseguramos que las tablas existan
        dao = new EstadisticasDAO();
        GestorBaseDatos.inicializarTablas();
        
        dao.borrarHistorial();
    }

    @Test
    public void testRequisitosBaseDatosCompleto() {
        System.out.println("--INICIAR JUNIT--");

        
        int idGenerado = dao.guardarPartida("JugadorJUnit", 20, 8);
        
        //El ID debe ser mayor que 0 si se guardó bien en la BD SQLite
        assertTrue("El ID de la partida debería ser mayor que 0", idGenerado > 0);
        System.out.println("1. Inserción correcta. ID generado: " + idGenerado);

       
        // Verifica tabla partidas
        List<Partida> historial = dao.obtenerHistorial();
        Partida partidaEncontrada = null;
        for (Partida p : historial) {
            if (p.getId() == idGenerado) {
                partidaEncontrada = p;
                break;
            }
        }
        assertNotNull("La partida debería existir en la tabla 'partidas'", partidaEncontrada);
        assertEquals("El ganador debería ser el que insertamos", "JugadorJUnit", partidaEncontrada.getGanador());

        // Verificamos tabla detalles_partida
        List<String> detalles = dao.obtenerDetallesPartida(idGenerado);
        assertFalse("La lista de detalles no debería estar vacía (se insertaron automáticamente)", detalles.isEmpty());
        boolean contieneEficiencia = false;
        for (String d : detalles) {
            if (d.contains("Eficiencia")) contieneEficiencia = true;
        }
        assertTrue("Debería existir un registro relacionado en la tabla secundaria", contieneEficiencia);
        System.out.println("2. Lectura y relación entre tablas verificada.");

        String nuevoGanador = "Ganador_Modificado_JUnit";
        dao.corregirGanador(idGenerado, nuevoGanador);
        
        // Recuperamos de nuevo para verificar
        historial = dao.obtenerHistorial();
        String ganadorActual = "";
        for (Partida p : historial) {
            if (p.getId() == idGenerado) {
                ganadorActual = p.getGanador();
            }
        }
        assertEquals("El ganador debería haberse actualizado", nuevoGanador, ganadorActual);
        System.out.println("3. Modificación (Update) verificada.");

        dao.borrarPartidaPorId(idGenerado);
        
        // Verificar que ya no está en la tabla partidas
        historial = dao.obtenerHistorial();
        boolean existe = false;
        for (Partida p : historial) {
            if (p.getId() == idGenerado) existe = true;
        }
        assertFalse("La partida debería haber sido eliminada", existe);
        
        // Verificar que se borraron los detalles 
        List<String> detallesDespuesBorrado = dao.obtenerDetallesPartida(idGenerado);
        assertTrue("Los detalles deberían haberse borrado automáticamente (Integridad referencial)", detallesDespuesBorrado.isEmpty());
        System.out.println("4. Borrado y limpieza en cascada verificado.");
        
        System.out.println("--TEST FINALIZADO: EXITO--");
    }
}