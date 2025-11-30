package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VentanaJuego extends JFrame {

    private static final long serialVersionUID = 1L;

    private JButton[][] celdas = new JButton[10][10];
    
    // Estadisticas
    private JLabel labelAciertos;
    private JLabel labelFallos;
    private JLabel labelBarcosHundidos;
    private JLabel labelTurno;
    
    private JLabel labelSuperDisparoDisponibles;
    private JLabel labelMegaDisparoDisponibles;
    
    // Botones
    private JButton botonSuperDisparo;
    private JButton botonMegaDisparo;
    private JButton botonGuardar;
    
    // Contadores
    private int aciertos = 0;
    private int fallos = 0;
    private int barcosHundidos = 0;
    private int superDisparosDisponibles;
    private int megaDisparosDisponibles;
    
    // Valores iniciales
    private int superDisparosIniciales;
    private int megaDisparosIniciales;
    
    // Jugadores actual
    private int jugadorActual;

    public VentanaJuego(int numeroJugador, int superDisparos, int megaDisparos) {
        this.jugadorActual = numeroJugador;
        this.superDisparosDisponibles = superDisparos;
        this.megaDisparosDisponibles = megaDisparos;
        this.superDisparosIniciales = superDisparos;
        this.megaDisparosIniciales = megaDisparos;

        // Configuracion de la ventana
        setTitle("Hundir la Flota");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        // Panel superior (titulo)
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        labelTurno = new JLabel("Turno del Jugador " + jugadorActual);
        labelTurno.setFont(new Font("Arial", Font.BOLD, 16));
        panelSuperior.add(labelTurno);
        panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

        // Panel cuadrícula 10x10
        JPanel panelCuadricula = new JPanel(new GridLayout(10, 10));
        panelCuadricula.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JButton celda = new JButton();
                celda.setBackground(Color.CYAN.darker());
                celdas[fila][col] = celda;

                int f = fila, c = col;
                celda.addActionListener(e -> {
                    // TODO: Implementar funcionalidad de disparo
                    System.out.println("Disparo en: (" + f + ", " + c + ")");
                });

                panelCuadricula.add(celda);
            }
        }
        panelPrincipal.add(panelCuadricula, BorderLayout.CENTER);

        // Panel estadisticas y botones
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        panelDerecho.setPreferredSize(new Dimension(180, 300));

        // ESTADISTICAS
        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(new BoxLayout(panelEstadisticas, BoxLayout.Y_AXIS));
        panelEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas"));
        panelEstadisticas.setAlignmentX(LEFT_ALIGNMENT);

        labelAciertos = new JLabel("Aciertos: " + aciertos);
        labelAciertos.setFont(new Font("Arial", Font.PLAIN, 14));
        panelEstadisticas.add(labelAciertos);
        
        panelEstadisticas.add(Box.createVerticalStrut(5));

        labelFallos = new JLabel("Fallos: " + fallos);
        labelFallos.setFont(new Font("Arial", Font.PLAIN, 14));
        panelEstadisticas.add(labelFallos);
        
        panelEstadisticas.add(Box.createVerticalStrut(5));

        labelBarcosHundidos = new JLabel("Barcos hundidos: " + barcosHundidos);
        labelBarcosHundidos.setFont(new Font("Arial", Font.PLAIN, 14));
        panelEstadisticas.add(labelBarcosHundidos);

        panelDerecho.add(panelEstadisticas);
        panelDerecho.add(Box.createVerticalStrut(20));

        // DISPAROS ESPECIALES
        JPanel panelDisparosEspeciales = new JPanel();
        panelDisparosEspeciales.setLayout(new BoxLayout(panelDisparosEspeciales, BoxLayout.Y_AXIS));
        panelDisparosEspeciales.setAlignmentX(LEFT_ALIGNMENT);

        // Super Disparo
        labelSuperDisparoDisponibles = new JLabel("Te quedan: " + superDisparosDisponibles);
        labelSuperDisparoDisponibles.setFont(new Font("Arial", Font.PLAIN, 12));
        panelDisparosEspeciales.add(labelSuperDisparoDisponibles);
        
        panelDisparosEspeciales.add(Box.createVerticalStrut(5));

        botonSuperDisparo = new JButton("Super Disparo");
        botonSuperDisparo.setPreferredSize(new Dimension(150, 35));
        botonSuperDisparo.setMaximumSize(new Dimension(150, 35));
        botonSuperDisparo.addActionListener(e -> {
            // TODO: Implementar super disparo
            System.out.println("Super Disparo activado");
        });
        panelDisparosEspeciales.add(botonSuperDisparo);

        panelDisparosEspeciales.add(Box.createVerticalStrut(15));

        // Mega Disparo
        labelMegaDisparoDisponibles = new JLabel("Te quedan: " + megaDisparosDisponibles);
        labelMegaDisparoDisponibles.setFont(new Font("Arial", Font.PLAIN, 12));
        panelDisparosEspeciales.add(labelMegaDisparoDisponibles);
        
        panelDisparosEspeciales.add(Box.createVerticalStrut(5));

        botonMegaDisparo = new JButton("Mega Disparo");
        botonMegaDisparo.setPreferredSize(new Dimension(150, 35));
        botonMegaDisparo.setMaximumSize(new Dimension(150, 35));
        botonMegaDisparo.addActionListener(e -> {
            // TODO: Implementar mega disparo
            System.out.println("Mega Disparo activado");
        });
        panelDisparosEspeciales.add(botonMegaDisparo);

        panelDerecho.add(panelDisparosEspeciales);
        panelDerecho.add(Box.createVerticalStrut(20));

        // BOTON GUARDAR
        botonGuardar = new JButton("Guardar");
        botonGuardar.setPreferredSize(new Dimension(150, 35));
        botonGuardar.setMaximumSize(new Dimension(150, 35));
        botonGuardar.setAlignmentX(LEFT_ALIGNMENT);
        botonGuardar.addActionListener(e -> {
            cambiarJugador();
        });
        panelDerecho.add(botonGuardar);

        panelPrincipal.add(panelDerecho, BorderLayout.EAST);
    }

    // Cambia el turno al siguiente jugador
    private void cambiarJugador() {
        if (jugadorActual == 1) {
            jugadorActual = 2;
        } else {
            jugadorActual = 1;
        }
        
        labelTurno.setText("Turno del Jugador " + jugadorActual);
        
        // TODO: De momento solo reseteamos el tablero visualmente pero habria que ocultar la info del jugador anterior y mostrar la del nuevo jugador
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                celdas[fila][col].setBackground(Color.CYAN.darker());
            }
        }
        
        // Resetear estadisticas
        aciertos = 0;
        fallos = 0;
        barcosHundidos = 0;
        labelAciertos.setText("Aciertos: " + aciertos);
        labelFallos.setText("Fallos: " + fallos);
        labelBarcosHundidos.setText("Barcos hundidos: " + barcosHundidos);
        
        // Resetear disparos especiales
        superDisparosDisponibles = superDisparosIniciales;
        megaDisparosDisponibles = megaDisparosIniciales;
        labelSuperDisparoDisponibles.setText("Te quedan: " + superDisparosDisponibles);
        labelMegaDisparoDisponibles.setText("Te quedan: " + megaDisparosDisponibles);
        
        System.out.println("Ahora es el turno del Jugador " + jugadorActual);
    }


    // Obtiene el jugador actual
    public int getJugadorActual() {
        return jugadorActual;
    }

    // Actualiza el contador de aciertos
    public void actualizarAciertos(int nuevosAciertos) {
        this.aciertos = nuevosAciertos;
        labelAciertos.setText("Aciertos: " + aciertos);
    }

    // Actualiza el contador de fallos
    public void actualizarFallos(int nuevosFallos) {
        this.fallos = nuevosFallos;
        labelFallos.setText("Fallos: " + fallos);
    }

    // Actualiza el contador de barcos hundidos
    public void actualizarBarcosHundidos(int nuevosHundidos) {
        this.barcosHundidos = nuevosHundidos;
        labelBarcosHundidos.setText("Barcos hundidos: " + barcosHundidos);
    }

    // Actualiza el contador de super disparos disponibles
    public void actualizarSuperDisparos(int cantidad) {
        this.superDisparosDisponibles = cantidad;
        labelSuperDisparoDisponibles.setText("Te quedan: " + superDisparosDisponibles);
    }

    // Actualiza el contador de mega disparos disponibles
    public void actualizarMegaDisparos(int cantidad) {
        this.megaDisparosDisponibles = cantidad;
        labelMegaDisparoDisponibles.setText("Te quedan: " + megaDisparosDisponibles);
    }

    // Cambia el color de una celda especifica
    public void cambiarColorCelda(int fila, int columna, Color color) {
        celdas[fila][columna].setBackground(color);
    }
}