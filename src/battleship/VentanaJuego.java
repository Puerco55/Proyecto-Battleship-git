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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class VentanaJuego extends JFrame {

    private static final long serialVersionUID = 1L;

    // Tablero grande (donde el jugador dispara al oponente)
    private JButton[][] celdasAtaque = new JButton[10][10];
    
    // Tablero pequeño (tablero propio del jugador, solo visual)
    private JPanel[][] celdasPropias = new JPanel[10][10];
    
    // Labels de estadísticas
    private JLabel labelAciertos;
    private JLabel labelFallos;
    private JLabel labelBarcosHundidos;
    
    // Botones de disparos especiales
    private JButton botonSuperDisparo;
    private JButton botonMegaDisparo;
    private JButton botonGuardar;
    
    // Estadísticas por jugador [0] = J1, [1] = J2
    private int[] aciertos = {0, 0};
    private int[] fallos = {0, 0};
    private int[] barcosHundidosContador = {0, 0};
    private int[] superDisparos = new int[2];
    private int[] megaDisparos = new int[2];
    
    // Control de jugadores
    private int jugadorActual;
    
    // Contador de turnos totales para estadísticas
    private int turnosTotales = 0;
    
    //Tableros de ambos jugadores
    private boolean[][] tableroJugador1;
    private boolean[][] tableroJugador2;
    
    //Matrices para registrar los disparos de cada jugador
    private boolean[][] disparosJugador1 = new boolean[10][10];
    private boolean[][] disparosJugador2 = new boolean[10][10];
    
    //Matrices para registrar impactos recibidos en cada tablero
    private boolean[][] impactosEnTablero1 = new boolean[10][10];
    private boolean[][] impactosEnTablero2 = new boolean[10][10];
    
    //Total de casillas con barco de cada jugador
    private int totalCasillasBarcoJ1 = 0;
    private int totalCasillasBarcoJ2 = 0;
    
    //Casillas de barco acertadas por cada jugador
    private int casillasAcertadasPorJ1 = 0;
    private int casillasAcertadasPorJ2 = 0;
    
    //DAO para guardar estadísticas
    private EstadisticasDAO estadisticasDAO;
    
    //Estado del disparo especial activo
    private boolean superDisparoActivo = false;
    private boolean megaDisparoActivo = false;
    
    //Control de si el jugador ya disparó este turno
    private boolean yaDisparo = false;

    public VentanaJuego(int numeroJugador, int superDisparosIniciales, int megaDisparosIniciales, 
                        boolean[][] tableroJ1, boolean[][] tableroJ2) {
        this.jugadorActual = numeroJugador;
        this.superDisparos[0] = superDisparosIniciales;
        this.superDisparos[1] = superDisparosIniciales;
        this.megaDisparos[0] = megaDisparosIniciales;
        this.megaDisparos[1] = megaDisparosIniciales;
        this.estadisticasDAO = new EstadisticasDAO();
        
        //Guardar los tableros de ambos jugadores
        this.tableroJugador1 = tableroJ1;
        this.tableroJugador2 = tableroJ2;
        
        //Contar casillas con barco de cada jugador
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (tableroJugador1[i][j]) totalCasillasBarcoJ1++;
                if (tableroJugador2[i][j]) totalCasillasBarcoJ2++;
            }
        }

        setTitle("Hundir la Flota - Turno del Jugador " + jugadorActual);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(panelPrincipal);

        //Panel superior (Estadísticas)
        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(new BoxLayout(panelEstadisticas, BoxLayout.X_AXIS));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        labelAciertos = new JLabel("Aciertos: 0");
        labelAciertos.setFont(new Font("Arial", Font.BOLD, 14));
        labelAciertos.setForeground(new Color(0, 128, 0));
        panelEstadisticas.add(labelAciertos);
        panelEstadisticas.add(Box.createHorizontalStrut(30));

        labelFallos = new JLabel("Fallos: 0");
        labelFallos.setFont(new Font("Arial", Font.BOLD, 14));
        labelFallos.setForeground(Color.RED);
        panelEstadisticas.add(labelFallos);
        panelEstadisticas.add(Box.createHorizontalStrut(30));

        labelBarcosHundidos = new JLabel("Barcos hundidos: 0");
        labelBarcosHundidos.setFont(new Font("Arial", Font.BOLD, 14));
        labelBarcosHundidos.setForeground(Color.BLUE);
        panelEstadisticas.add(labelBarcosHundidos);

        panelPrincipal.add(panelEstadisticas, BorderLayout.NORTH);

        //Panel central (Tablero de ataque)
        JPanel panelTableroAtaque = new JPanel(new GridLayout(10, 10));
        panelTableroAtaque.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.BLACK, 2), "Tablero del Oponente (Dispara aquí)"));
        panelTableroAtaque.setPreferredSize(new Dimension(380, 380));
        
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JButton celda = new JButton();
                celda.setBackground(new Color(0, 150, 200));
                celda.setFocusPainted(false);
                celdasAtaque[fila][col] = celda;

                int f = fila, c = col;
                celda.addActionListener(e -> procesarDisparo(f, c));

                panelTableroAtaque.add(celda);
            }
        }
        panelPrincipal.add(panelTableroAtaque, BorderLayout.CENTER);

        //Panel derecho
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        panelDerecho.setPreferredSize(new Dimension(200, 400));

        //Tablero pequeño
        JLabel labelTableroPropio = new JLabel("Tu tablero:");
        labelTableroPropio.setFont(new Font("Arial", Font.BOLD, 12));
        labelTableroPropio.setAlignmentX(LEFT_ALIGNMENT);
        panelDerecho.add(labelTableroPropio);
        panelDerecho.add(Box.createVerticalStrut(5));
        
        JPanel panelTableroPropio = new JPanel(new GridLayout(10, 10));
        panelTableroPropio.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panelTableroPropio.setPreferredSize(new Dimension(150, 150));
        panelTableroPropio.setMaximumSize(new Dimension(150, 150));
        panelTableroPropio.setAlignmentX(LEFT_ALIGNMENT);
        
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JPanel celda = new JPanel();
                celda.setBackground(new Color(0, 150, 200));
                celda.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
                celdasPropias[fila][col] = celda;
                panelTableroPropio.add(celda);
            }
        }
        panelDerecho.add(panelTableroPropio);
        panelDerecho.add(Box.createVerticalStrut(20));

        //Disparos especiales
        JLabel labelDisparos = new JLabel("DISPAROS ESPECIALES:");
        labelDisparos.setFont(new Font("Arial", Font.BOLD, 12));
        labelDisparos.setAlignmentX(LEFT_ALIGNMENT);
        panelDerecho.add(labelDisparos);
        panelDerecho.add(Box.createVerticalStrut(10));

        botonSuperDisparo = new JButton("Super Disparo (" + superDisparos[0] + ")");
        botonSuperDisparo.setPreferredSize(new Dimension(180, 35));
        botonSuperDisparo.setMaximumSize(new Dimension(180, 35));
        botonSuperDisparo.setAlignmentX(LEFT_ALIGNMENT);
        botonSuperDisparo.setToolTipText("Dispara en cruz (5 casillas)");
        botonSuperDisparo.addActionListener(e -> activarSuperDisparo());
        panelDerecho.add(botonSuperDisparo);
        panelDerecho.add(Box.createVerticalStrut(10));

        botonMegaDisparo = new JButton("Mega Disparo (" + megaDisparos[0] + ")");
        botonMegaDisparo.setPreferredSize(new Dimension(180, 35));
        botonMegaDisparo.setMaximumSize(new Dimension(180, 35));
        botonMegaDisparo.setAlignmentX(LEFT_ALIGNMENT);
        botonMegaDisparo.setToolTipText("Dispara en área 3x3 (9 casillas)");
        botonMegaDisparo.addActionListener(e -> activarMegaDisparo());
        panelDerecho.add(botonMegaDisparo);
        panelDerecho.add(Box.createVerticalGlue());

        botonGuardar = new JButton("Pasar Turno");
        botonGuardar.setPreferredSize(new Dimension(180, 40));
        botonGuardar.setMaximumSize(new Dimension(180, 40));
        botonGuardar.setAlignmentX(LEFT_ALIGNMENT);
        botonGuardar.setFont(new Font("Arial", Font.BOLD, 14));
        botonGuardar.setBackground(new Color(100, 180, 100));
        botonGuardar.addActionListener(e -> cambiarJugador());
        panelDerecho.add(botonGuardar);

        panelPrincipal.add(panelDerecho, BorderLayout.EAST);
        
        actualizarTableroPropio();
        actualizarEstadisticas();
        actualizarBotonesDisparos();
    }

    private void actualizarTableroPropio() {
        boolean[][] tableroActual = (jugadorActual == 1) ? tableroJugador1 : tableroJugador2;
        boolean[][] impactosRecibidos = (jugadorActual == 1) ? impactosEnTablero1 : impactosEnTablero2;
        
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                if (impactosRecibidos[fila][col]) {
                    celdasPropias[fila][col].setBackground(tableroActual[fila][col] ?  Color.RED : Color.WHITE);
                } else if (tableroActual[fila][col]) {
                    celdasPropias[fila][col].setBackground(Color.GRAY);
                } else {
                    celdasPropias[fila][col].setBackground(new Color(0, 150, 200));
                }
            }
        }
    }
    
    private void actualizarTableroAtaque() {
        boolean[][] disparosActuales = (jugadorActual == 1) ? disparosJugador1 : disparosJugador2;
        boolean[][] tableroOponente = (jugadorActual == 1) ?  tableroJugador2 : tableroJugador1;
        
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                if (disparosActuales[fila][col]) {
                    if (tableroOponente[fila][col]) {
                        celdasAtaque[fila][col].setBackground(Color.RED);
                        celdasAtaque[fila][col].setText("X");
                    } else {
                        celdasAtaque[fila][col].setBackground(Color.WHITE);
                        celdasAtaque[fila][col].setText("•");
                    }
                    celdasAtaque[fila][col].setEnabled(false);
                } else {
                    celdasAtaque[fila][col].setBackground(new Color(0, 150, 200));
                    celdasAtaque[fila][col].setText("");
                    celdasAtaque[fila][col].setEnabled(true);
                }
            }
        }
    }
    
    private void actualizarEstadisticas() {
        int idx = jugadorActual - 1;
        labelAciertos.setText("Aciertos: " + aciertos[idx]);
        labelFallos.setText("Fallos: " + fallos[idx]);
        labelBarcosHundidos.setText("Barcos hundidos: " + barcosHundidosContador[idx]);
    }
    
    private void actualizarBotonesDisparos() {
        int idx = jugadorActual - 1;
        botonSuperDisparo.setText("Super Disparo (" + superDisparos[idx] + ")");
        botonMegaDisparo.setText("Mega Disparo (" + megaDisparos[idx] + ")");
    }

    private void procesarDisparo(int fila, int columna) {
        boolean[][] disparosActuales = (jugadorActual == 1) ?  disparosJugador1 : disparosJugador2;
        
        if (disparosActuales[fila][columna]) {
            JOptionPane.showMessageDialog(this, "Ya disparaste en esta casilla", 
                "Disparo inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        //Verificar si hay disparo especial
        if (superDisparoActivo) {
            ejecutarSuperDisparo(fila, columna);
            superDisparoActivo = false;
            yaDisparo = true;
            return;
        }
        
        if (megaDisparoActivo) {
            ejecutarMegaDisparo(fila, columna);
            megaDisparoActivo = false;
            yaDisparo = true;
            return;
        }
        
        ejecutarDisparoNormal(fila, columna);
        yaDisparo = true;
        deshabilitarTableroAtaque();
    }
    
    private void deshabilitarTableroAtaque() {
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                celdasAtaque[fila][col]. setEnabled(false);
            }
        }
    }

	private boolean ejecutarDisparoEnPosicion(int fila, int columna) {
        if (fila < 0 || fila >= 10 || columna < 0 || columna >= 10) {
            return false;
        }
        
        boolean[][] disparosActuales = (jugadorActual == 1) ?  disparosJugador1 : disparosJugador2;
        boolean[][] tableroOponente = (jugadorActual == 1) ? tableroJugador2 : tableroJugador1;
        boolean[][] impactosEnTableroOponente = (jugadorActual == 1) ? impactosEnTablero2 : impactosEnTablero1;
        
        if (disparosActuales[fila][columna]) {
            return false;
        }
        
        disparosActuales[fila][columna] = true;
        impactosEnTableroOponente[fila][columna] = true;
        int idx = jugadorActual - 1;
        
        if (tableroOponente[fila][columna]) {
            celdasAtaque[fila][columna].setBackground(Color.RED);
            celdasAtaque[fila][columna].setText("X");
            celdasAtaque[fila][columna].setEnabled(false);
            aciertos[idx]++;
            
            if (jugadorActual == 1) {
                casillasAcertadasPorJ1++;
            } else {
                casillasAcertadasPorJ2++;
            }
            return true;
        } else {
            celdasAtaque[fila][columna].setBackground(Color.WHITE);
            celdasAtaque[fila][columna].setText("•");
            celdasAtaque[fila][columna].setEnabled(false);
            fallos[idx]++;
            return false;
        }
    }
    
    private void ejecutarDisparoNormal(int fila, int columna) {
        boolean acierto = ejecutarDisparoEnPosicion(fila, columna);
        actualizarEstadisticas();
        
        //Verificar victoria
        if (verificarVictoria()) {
            return;
        }
        
        if (acierto) {
            JOptionPane.showMessageDialog(this, "¡ACIERTO! Has impactado un barco.", 
                "¡Boom!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Agua...Has fallado.", 
                "Splash", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void cambiarJugador() {
        if (! yaDisparo) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                "No has disparado este turno.¿Seguro que quieres pasar? ",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (respuesta != JOptionPane.YES_OPTION) return;
        }
        
        turnosTotales++;
        
        JOptionPane.showMessageDialog(this,
            "Pasa el dispositivo al Jugador " + (jugadorActual == 1 ? 2 : 1) + "\n\nPulsa OK cuando esté listo.",
            "Cambio de Turno", JOptionPane.INFORMATION_MESSAGE);
        
        jugadorActual = (jugadorActual == 1) ? 2 : 1;
        yaDisparo = false;
        superDisparoActivo = false;
        megaDisparoActivo = false;
        
        setTitle("Hundir la Flota - Turno del Jugador " + jugadorActual);
        actualizarTableroAtaque();
        actualizarTableroPropio();
        actualizarEstadisticas();
        actualizarBotonesDisparos();
    }
    
    //Activa el modo Super Disparo
    private void activarSuperDisparo() {
        int idx = jugadorActual - 1;
        
        if (superDisparos[idx] <= 0) {
            JOptionPane.showMessageDialog(this, "No te quedan Super Disparos", 
                "Sin disparos especiales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Ya has disparado este turno.Pasa el turno primero.", 
                "Turno usado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        superDisparoActivo = true;
        megaDisparoActivo = false;
        
        JOptionPane.showMessageDialog(this, 
            "Super Disparo activado.\nDispara en CRUZ (5 casillas).\nSelecciona el centro del disparo.", 
            "Super Disparo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Ejecuta el Super Disparo
    private void ejecutarSuperDisparo(int fila, int columna) {
        int idx = jugadorActual - 1;
        superDisparos[idx]--;
        actualizarBotonesDisparos();
        
        int totalAciertos = 0;
        
        // Disparo en cruz: centro + 4 direcciones
        int[][] posiciones = {
            {fila, columna},           // Centro
            {fila - 1, columna},       // Arriba
            {fila + 1, columna},       // Abajo
            {fila, columna - 1},       // Izquierda
            {fila, columna + 1}        // Derecha
        };
        
        for (int[] pos : posiciones) {
            if (ejecutarDisparoEnPosicion(pos[0], pos[1])) {
                totalAciertos++;
            }
        }
        
        actualizarEstadisticas();
        
        if (verificarVictoria()) {
            return;
        }
        
        deshabilitarTableroAtaque();
        
        JOptionPane.showMessageDialog(this, 
            "Super Disparo ejecutado.\nAciertos: " + totalAciertos + " de 5 casillas.", 
            "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Activa el modo Mega Disparo
    private void activarMegaDisparo() {
        int idx = jugadorActual - 1;
        
        if (megaDisparos[idx] <= 0) {
            JOptionPane.showMessageDialog(this, "No te quedan Mega Disparos", 
                "Sin disparos especiales", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Ya has disparado este turno.Pasa el turno primero.", 
                "Turno usado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        megaDisparoActivo = true;
        superDisparoActivo = false;
        
        JOptionPane.showMessageDialog(this, 
            "Mega Disparo activado.\nDispara en ÁREA 3x3 (9 casillas).\nSelecciona el centro del disparo.", 
            "Mega Disparo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Ejecuta el Mega Disparo
    private void ejecutarMegaDisparo(int fila, int columna) {
        int idx = jugadorActual - 1;
        megaDisparos[idx]--;
        actualizarBotonesDisparos();
        
        int totalAciertos = 0;
        
        // Disparo en 3x3
        for (int f = fila - 1; f <= fila + 1; f++) {
            for (int c = columna - 1; c <= columna + 1; c++) {
                if (ejecutarDisparoEnPosicion(f, c)) {
                    totalAciertos++;
                }
            }
        }
        
        actualizarEstadisticas();
        
        if (verificarVictoria()) {
            return;
        }
        
        deshabilitarTableroAtaque();
        
        JOptionPane.showMessageDialog(this, 
            "Mega Disparo ejecutado.\nAciertos: " + totalAciertos + " de 9 casillas.", 
            "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }
    
    //Verifica si el jugador ha ganado la partida
    private boolean verificarVictoria() {
        int casillasNecesarias;
        int casillasAcertadas;
        
        if (jugadorActual == 1) {
            casillasNecesarias = totalCasillasBarcoJ2;
            casillasAcertadas = casillasAcertadasPorJ1;
        } else {
            casillasNecesarias = totalCasillasBarcoJ1;
            casillasAcertadas = casillasAcertadasPorJ2;
        }
        
        if (casillasAcertadas >= casillasNecesarias) {
            finalizarPartida(jugadorActual);
            return true;
        }
        
        return false;
    }
    
    //Finaliza la partida, muestra el ganador y guarda las estadísticas
    private void finalizarPartida(int jugadorGanador) {
        // Mostrar mensaje de victoria
        JOptionPane.showMessageDialog(this, 
            "¡¡¡FELICIDADES!! !\n\nEl Jugador " + jugadorGanador + " ha GANADO la partida!\n\n" +
            "Turnos totales: " + turnosTotales + "\n" +
            "Aciertos del ganador: " + aciertos[jugadorGanador - 1] + "\n" +
            "Fallos del ganador: " + fallos[jugadorGanador - 1],
            "¡¡¡VICTORIA!!! ",
            JOptionPane.INFORMATION_MESSAGE);
        
        //Guardar estadísticas en la bd
        String ganador = "Jugador " + jugadorGanador;
        estadisticasDAO.guardarPartida(ganador, turnosTotales, barcosHundidosContador[jugadorGanador -1]);
        
        //Preguntar quiere jugar otra partida
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Quieres jugar otra partida?",
            "Nueva Partida",
            JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            MainMenu menu = new MainMenu();
            menu.setVisible(true);
        }
        
        dispose();
    }
}