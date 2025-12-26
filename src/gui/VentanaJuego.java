package gui;

import java.awt.*;
import javax.swing.*;
import db.EstadisticasDAO;
import domain.Jugador;

public class VentanaJuego extends JFrame {

    private static final long serialVersionUID = 1L;

    // Componentes UI
    private JLabel labelTiempo;
    private JLabel labelInfoJugador;
    private JButton[][] celdasAtaque = new JButton[10][10];
    private JPanel[][] celdasPropias = new JPanel[10][10];
    
    // Estadísticas UI
    private JLabel labelAciertos, labelFallos, labelBarcosHundidos;
    private JButton botonSuperDisparo, botonMegaDisparo, botonPasarTurno;
    
    // Lógica del Juego
    private Jugador j1;
    private Jugador j2;
    private Jugador jugadorActual;
    private Jugador oponente;

    private int turnosTotales = 0;
    private boolean superDisparoActivo = false;
    private boolean megaDisparoActivo = false;
    private boolean yaDisparo = false;
    
    // --- NUEVAS VARIABLES PARA EL TIEMPO ---
    private int segundosTurnoActual = 0;      // Tiempo que ve el jugador ahora
    private int segundosTotalesPartida = 0;   // Acumulador de toda la partida
    private volatile boolean cronometroPausado = false; // Para detenerlo al cambiar turno
    // ---------------------------------------
    
    private Thread hiloCronometro;
    private volatile boolean juegoActivo = true;
    private EstadisticasDAO estadisticasDAO;

    public VentanaJuego(int numeroJugadorInicial, int superDisparos, int megaDisparos, 
                        boolean[][] tableroJ1, boolean[][] tableroJ2) {
        
        this.j1 = new Jugador(1, tableroJ1, superDisparos, megaDisparos);
        this.j2 = new Jugador(2, tableroJ2, superDisparos, megaDisparos);
        this.estadisticasDAO = new EstadisticasDAO();

        if (numeroJugadorInicial == 1) {
            configurarTurno(j1, j2);
        } else {
            configurarTurno(j2, j1);
        }

        configurarVentana();
        inicializarComponentes();
        actualizarInterfaz();
        iniciarCronometro(); // Arranca el hilo
    }
    
    private void configurarTurno(Jugador actual, Jugador enemigo) {
        this.jugadorActual = actual;
        this.oponente = enemigo;
    }

    private void configurarVentana() {
        setTitle("Hundir la Flota - Batalla Naval");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panelPrincipal);

        // --- TOP PANEL ---
        JPanel panelInfo = new JPanel(new BorderLayout());
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        labelAciertos = crearLabelEstilo("Aciertos: 0", new Color(0, 128, 0));
        labelFallos = crearLabelEstilo("Fallos: 0", Color.RED);
        labelBarcosHundidos = crearLabelEstilo("Barcos: 0", Color.BLUE);
        
        statsPanel.add(labelAciertos);
        statsPanel.add(labelFallos);
        statsPanel.add(labelBarcosHundidos);
        
        labelTiempo = new JLabel("00:00");
        labelTiempo.setFont(new Font("Monospaced", Font.BOLD, 20));
        labelTiempo.setForeground(Color.DARK_GRAY);
        
        panelInfo.add(statsPanel, BorderLayout.WEST);
        panelInfo.add(labelTiempo, BorderLayout.EAST);
        
        labelInfoJugador = new JLabel("Turno del JUGADOR " + jugadorActual.getId(), SwingConstants.CENTER);
        labelInfoJugador.setFont(new Font("Arial", Font.BOLD, 18));
        panelInfo.add(labelInfoJugador, BorderLayout.SOUTH);

        panelPrincipal.add(panelInfo, BorderLayout.NORTH);

        // --- CENTER (TABLERO ATAQUE) ---
        JPanel panelTableroAtaque = new JPanel(new GridLayout(10, 10));
        panelTableroAtaque.setBorder(BorderFactory.createTitledBorder("RADAR DE ATAQUE (Oponente)"));
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton btn = new JButton();
                btn.setBackground(new Color(0, 150, 200));
                btn.setFocusPainted(false);
                int r = i, c = j;
                btn.addActionListener(e -> procesarClicCelda(r, c));
                celdasAtaque[i][j] = btn;
                panelTableroAtaque.add(btn);
            }
        }
        panelPrincipal.add(panelTableroAtaque, BorderLayout.CENTER);

        // --- RIGHT (CONTROLES Y TABLERO PROPIO) ---
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setPreferredSize(new Dimension(250, 0));
        
        panelDerecho.add(new JLabel("Tu Flota:"));
        JPanel miniTablero = new JPanel(new GridLayout(10, 10));
        miniTablero.setPreferredSize(new Dimension(200, 200));
        miniTablero.setMaximumSize(new Dimension(200, 200));
        miniTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JPanel p = new JPanel();
                p.setBackground(new Color(0, 150, 200));
                celdasPropias[i][j] = p;
                miniTablero.add(p);
            }
        }
        panelDerecho.add(miniTablero);
        panelDerecho.add(Box.createVerticalStrut(20));
        
        botonSuperDisparo = new JButton("Super Disparo");
        botonSuperDisparo.addActionListener(e -> activarHabilidad(true, false));
        
        botonMegaDisparo = new JButton("Mega Disparo");
        botonMegaDisparo.addActionListener(e -> activarHabilidad(false, true));
        
        botonPasarTurno = new JButton("TERMINAR TURNO");
        botonPasarTurno.setBackground(new Color(100, 180, 100));
        botonPasarTurno.setFont(new Font("Arial", Font.BOLD, 14));
        botonPasarTurno.addActionListener(e -> cambiarTurno());
        
        panelDerecho.add(alinearBoton(botonSuperDisparo));
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(alinearBoton(botonMegaDisparo));
        panelDerecho.add(Box.createVerticalGlue());
        panelDerecho.add(alinearBoton(botonPasarTurno));

        panelPrincipal.add(panelDerecho, BorderLayout.EAST);
    }

    private JLabel crearLabelEstilo(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Arial", Font.BOLD, 14));
        l.setForeground(color);
        return l;
    }
    
    private Component alinearBoton(JButton b) {
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(220, 40));
        return b;
    }

    // --- LÓGICA DEL JUEGO ---

    private void procesarClicCelda(int fila, int col) {
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Ya has disparado. Pasa el turno.");
            return;
        }

        if (superDisparoActivo) {
            ejecutarSuperDisparo(fila, col);
        } else if (megaDisparoActivo) {
            ejecutarMegaDisparo(fila, col);
        } else {
            ejecutarDisparoSimple(fila, col);
        }
    }

    private void ejecutarDisparoSimple(int fila, int col) {
        if (jugadorActual.getTableroDisparos()[fila][col]) {
            JOptionPane.showMessageDialog(this, "Ya disparaste aquí.");
            return;
        }

        boolean impacto = procesarImpacto(fila, col);
        yaDisparo = true;
        
        if (impacto) {
            JOptionPane.showMessageDialog(this, "¡IMPACTO!", "Boom", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Agua...", "Splash", JOptionPane.INFORMATION_MESSAGE);
        }
        
        verificarVictoria();
        actualizarInterfaz();
    }
    
    private boolean procesarImpacto(int fila, int col) {
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) return false;
        
        if (jugadorActual.getTableroDisparos()[fila][col]) return false;

        jugadorActual.getTableroDisparos()[fila][col] = true;
        oponente.getImpactosRecibidos()[fila][col] = true;

        if (oponente.getTableroPropio()[fila][col]) {
            jugadorActual.incrementarAciertos();
            oponente.recibirImpacto();
            return true;
        } else {
            jugadorActual.incrementarFallos();
            return false;
        }
    }

    private void ejecutarSuperDisparo(int r, int c) {
        if (jugadorActual.getSuperDisparos() <= 0) return;

        int[][] coords = {{r,c}, {r-1,c}, {r+1,c}, {r,c-1}, {r,c+1}};
        int aciertos = 0;
        
        for (int[] par : coords) {
            if (procesarImpacto(par[0], par[1])) aciertos++;
        }
        
        jugadorActual.usarSuperDisparo();
        finalizarAtaqueEspecial("Super Disparo", aciertos);
    }

    private void ejecutarMegaDisparo(int r, int c) {
        if (jugadorActual.getMegaDisparos() <= 0) return;

        int aciertos = 0;
        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                if (procesarImpacto(i, j)) aciertos++;
            }
        }
        
        jugadorActual.usarMegaDisparo();
        finalizarAtaqueEspecial("Mega Disparo", aciertos);
    }
    
    private void finalizarAtaqueEspecial(String nombre, int aciertos) {
        superDisparoActivo = false;
        megaDisparoActivo = false;
        yaDisparo = true;
        verificarVictoria();
        actualizarInterfaz();
        JOptionPane.showMessageDialog(this, nombre + " completado.\nImpactos: " + aciertos);
    }

    private void activarHabilidad(boolean esSuper, boolean esMega) {
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Ya disparaste.");
            return;
        }
        
        if (esSuper && jugadorActual.getSuperDisparos() > 0) {
            superDisparoActivo = true;
            megaDisparoActivo = false;
            JOptionPane.showMessageDialog(this, "Modo SUPER activo (Cruz). Elige objetivo.");
        } else if (esMega && jugadorActual.getMegaDisparos() > 0) {
            megaDisparoActivo = true;
            superDisparoActivo = false;
            JOptionPane.showMessageDialog(this, "Modo MEGA activo (3x3). Elige objetivo.");
        } else {
            JOptionPane.showMessageDialog(this, "No te quedan disparos especiales de este tipo.");
        }
    }

    private void verificarVictoria() {
        if (oponente.haPerdido()) {
            juegoActivo = false;
            
            // Calculamos el tiempo FINAL total
            int tiempoFinal = segundosTotalesPartida + segundosTurnoActual;
            String tiempoFormateado = formatearTiempo(tiempoFinal);
            
            estadisticasDAO.guardarPartida("Jugador " + jugadorActual.getId(), turnosTotales, 5); 
            
            JOptionPane.showMessageDialog(this, 
                "¡EL JUGADOR " + jugadorActual.getId() + " GANA LA GUERRA!\n\n" +
                "Turnos totales: " + turnosTotales + "\n" +
                "Duración de la partida: " + tiempoFormateado,
                "VICTORIA", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            new gui.MainMenu().setVisible(true);
        }
    }

    // --- AQUÍ ESTÁ LA LÓGICA DEL CAMBIO DE TURNO Y TIEMPO ---
    private void cambiarTurno() {
        if (!yaDisparo) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Pasar sin disparar?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        // 1. Pausar cronómetro
        cronometroPausado = true;
        
        // 2. Acumular el tiempo del turno al total
        segundosTotalesPartida += segundosTurnoActual;
        
        turnosTotales++;
        
        // 3. Diálogo de espera (mientras está abierto, el tiempo no corre gracias a cronometroPausado)
        JOptionPane.showMessageDialog(this, 
            "Fin del turno. Tiempo acumulado partida: " + formatearTiempo(segundosTotalesPartida) + "\n\n" +
            "Pasa el dispositivo al OTRO jugador.",
            "Cambio de Jugador", JOptionPane.INFORMATION_MESSAGE);
        
        // 4. Intercambio de roles
        Jugador temp = jugadorActual;
        jugadorActual = oponente;
        oponente = temp;
        
        // 5. Resetear estado para el nuevo turno
        yaDisparo = false;
        superDisparoActivo = false;
        megaDisparoActivo = false;
        
        // 6. REINICIAR el contador visual del turno
        segundosTurnoActual = 0; 
        labelTiempo.setText("00:00");
        
        // 7. Reanudar cronómetro
        cronometroPausado = false;
        
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        labelInfoJugador.setText("Turno del JUGADOR " + jugadorActual.getId());
        labelAciertos.setText("Aciertos: " + jugadorActual.getAciertos());
        labelFallos.setText("Fallos: " + jugadorActual.getFallos());
        
        botonSuperDisparo.setText("Super Disparo (" + jugadorActual.getSuperDisparos() + ")");
        botonMegaDisparo.setText("Mega Disparo (" + jugadorActual.getMegaDisparos() + ")");
        
        boolean[][] disparos = jugadorActual.getTableroDisparos();
        boolean[][] barcosEnemigos = oponente.getTableroPropio(); 
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (disparos[i][j]) {
                    celdasAtaque[i][j].setEnabled(false);
                    if (barcosEnemigos[i][j]) {
                        celdasAtaque[i][j].setBackground(Color.RED);
                        celdasAtaque[i][j].setText("X");
                    } else {
                        celdasAtaque[i][j].setBackground(Color.CYAN); 
                        celdasAtaque[i][j].setText("O");
                    }
                } else {
                    celdasAtaque[i][j].setEnabled(true);
                    celdasAtaque[i][j].setBackground(new Color(0, 150, 200));
                    celdasAtaque[i][j].setText("");
                }
            }
        }

        boolean[][] miTablero = jugadorActual.getTableroPropio();
        boolean[][] misDaños = jugadorActual.getImpactosRecibidos();
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (misDaños[i][j]) {
                    celdasPropias[i][j].setBackground(miTablero[i][j] ? Color.RED : Color.CYAN);
                } else {
                    celdasPropias[i][j].setBackground(miTablero[i][j] ? Color.GRAY : new Color(0, 150, 200));
                }
            }
        }
    }
    
    // --- NUEVA LÓGICA DEL CRONÓMETRO ---
    private void iniciarCronometro() {
        hiloCronometro = new Thread(() -> {
            while (juegoActivo) {
                try {
                    Thread.sleep(1000);
                    
                    // Solo contamos si NO está pausado (ej: durante el popup de cambio de turno)
                    if (!cronometroPausado) {
                        segundosTurnoActual++;
                        String tiempo = formatearTiempo(segundosTurnoActual);
                        SwingUtilities.invokeLater(() -> labelTiempo.setText(tiempo));
                    }
                    
                } catch (InterruptedException e) { return; }
            }
        });
        hiloCronometro.start();
    }
    
    // Método auxiliar para formatear mm:ss
    private String formatearTiempo(int totalSegundos) {
        int min = totalSegundos / 60;
        int seg = totalSegundos % 60;
        return String.format("%02d:%02d", min, seg);
    }
}