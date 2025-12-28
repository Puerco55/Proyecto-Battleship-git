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
    private JLabel labelAciertos, labelFallos, labelBarcosHundidos, labelEscudos;
    private JButton botonSuperDisparo, botonMegaDisparo, botonPasarTurno, botonEscudo;
    
    // Lógica del Juego
    private Jugador j1;
    private Jugador j2;
    private Jugador jugadorActual;
    private Jugador oponente;

    private int turnosTotales = 0;
    private boolean superDisparoActivo = false;
    private boolean megaDisparoActivo = false;
    private boolean yaDisparo = false;
    
    // Variables para el tiempo
    private int tiempoRestanteJ1 = 120; // segundos restantes del jugador 1
    private int tiempoRestanteJ2 = 120; // segundos restantes de jugador 2
    private volatile boolean cronometroPausado = false; 
    
    private Thread hiloCronometro;
    private volatile boolean juegoActivo = true;
    private EstadisticasDAO estadisticasDAO;

    public VentanaJuego(int numeroJugadorInicial, int superDisparos, int megaDisparos, int escudos,
                        boolean[][] tableroJ1, boolean[][] tableroJ2) {
        
        this.j1 = new Jugador(1, tableroJ1, superDisparos, megaDisparos, escudos);
        this.j2 = new Jugador(2, tableroJ2, superDisparos, megaDisparos,escudos);
        this.estadisticasDAO = new EstadisticasDAO();

        if (numeroJugadorInicial == 1) {
            configurarTurno(j1, j2);
        } else {
            configurarTurno(j2, j1);
        }

        configurarVentana();
        inicializarComponentes();
        actualizarInterfaz();
        iniciarCronometro(); 
    }
    
    private void configurarTurno(Jugador actual, Jugador enemigo) {
        this.jugadorActual = actual;
        this.oponente = enemigo;
    }

    private void configurarVentana() {
        setTitle("Hundir la Flota - Batalla Naval");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650); // Un poco más grande para acomodar todo bien
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panelPrincipal);

        // --- TOP PANEL (Estadísticas y Tiempo) ---
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
        
        if (jugadorActual == j1) {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ1));
        } else {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ2));
        }
        
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
        panelDerecho.setPreferredSize(new Dimension(280, 0)); // Un poco más ancho
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        panelDerecho.add(new JLabel("Tu Flota:"));
        
        // Tablero propio
        JPanel miniTablero = new JPanel(new GridLayout(10, 10));
        miniTablero.setPreferredSize(new Dimension(250, 250));
        miniTablero.setMaximumSize(new Dimension(250, 250));
        // Borde exterior del mini tablero
        miniTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JPanel p = new JPanel();
                p.setBackground(new Color(0, 150, 200));
                
                
                // Añadimos un borde gris oscuro a cada celda individual
                p.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY)); 
                

                celdasPropias[i][j] = p;
                miniTablero.add(p);
            }
        }
        panelDerecho.add(miniTablero);
        panelDerecho.add(Box.createVerticalStrut(20));
        
        // Botones
        botonSuperDisparo = new JButton("Super Disparo");
        botonSuperDisparo.addActionListener(e -> activarHabilidad(true, false));
        
        botonMegaDisparo = new JButton("Mega Disparo");
        botonMegaDisparo.addActionListener(e -> activarHabilidad(false, true));
        botonEscudo = new JButton("Escudo (" + jugadorActual.getEscudos() + ")");
        botonEscudo.addActionListener(e -> activarEscudo());
        
        botonPasarTurno = new JButton("TERMINAR TURNO");
        botonPasarTurno.setBackground(new Color(100, 180, 100));
        botonPasarTurno.setFont(new Font("Arial", Font.BOLD, 14));
        botonPasarTurno.addActionListener(e -> cambiarTurno());
        
        labelEscudos = new JLabel("Escudos 🛡️: " + jugadorActual.getEscudos());	
        labelEscudos.setFont(new Font("Arial", Font.BOLD, 14));
        
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(alinearBoton(botonEscudo));
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
        b.setMaximumSize(new Dimension(240, 45));
        return b;
    }

    

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
        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this, 
                "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                "Tu disparo ha sido bloqueado.");
            oponente.resetEscudoTurno();
            yaDisparo = true;
            actualizarInterfaz();
            return; // Salir sin disparar
        }
        int resultado = procesarImpacto(fila, col);
        yaDisparo = true;  	

        if (resultado == 1) {
            JOptionPane.showMessageDialog(this, "¡IMPACTO!", "Boom", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Agua...", "Splash", JOptionPane.INFORMATION_MESSAGE);
        } 	
        
        verificarVictoria();
        actualizarInterfaz();
        
    }
    
    private int procesarImpacto(int fila, int col) {
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) return 0;
        if (jugadorActual.getTableroDisparos()[fila][col]) return 0;
          
        jugadorActual.getTableroDisparos()[fila][col] = true;
        
        if (oponente.getTableroPropio()[fila][col]) {
            oponente.getImpactosRecibidos()[fila][col] = true;
            jugadorActual.incrementarAciertos();
            oponente.recibirImpacto();
            return 1;
        } else {
            jugadorActual.incrementarFallos();
            return 0;
        }
    }

    private void ejecutarSuperDisparo(int r, int c) {
        if (jugadorActual.getSuperDisparos() <= 0) return;
        
        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this, 
                "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                "Tu disparo ha sido bloqueado.");
            oponente.resetEscudoTurno();
            yaDisparo = true;
            actualizarInterfaz();
            return; // Salir sin disparar
        }
        
        int[][] coords = {{r,c}, {r-1,c}, {r+1,c}, {r,c-1}, {r,c+1}};
        int aciertos = 0;
        
        for (int[] par : coords) {
            int res = procesarImpacto(par[0], par[1]);
            if (res == 1) aciertos++; 
        }
        
        jugadorActual.usarSuperDisparo();
        finalizarAtaqueEspecial("Super Disparo", aciertos);
    }

    private void ejecutarMegaDisparo(int r, int c) {
        if (jugadorActual.getMegaDisparos() <= 0) return;
        
        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this,
                "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                "El MEGA DISPARO ha sido bloqueado.");
            oponente.resetEscudoTurno();
            jugadorActual.usarSuperDisparo();
            yaDisparo = true;
            actualizarInterfaz();
            return;
        }
        
        int aciertos = 0;
        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                int res = procesarImpacto(i, j);
                if (res == 1) aciertos++; 
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
        }
    }

    private void verificarVictoria() {
        if (oponente.haPerdido()) {
            juegoActivo = false;
            
            // Calculamos el tiempo FINAL total
            String tiempoFormateadoJ1 = formatearTiempo(tiempoRestanteJ1);
            String tiempoFormateadoJ2 = formatearTiempo(tiempoRestanteJ2);
            
            estadisticasDAO.guardarPartida("Jugador " + jugadorActual.getId(), turnosTotales, 5); 
            
            JOptionPane.showMessageDialog(this, 
            	    "¡EL JUGADOR " + jugadorActual.getId() + " GANA LA GUERRA!\n\n" +
            	    "Turnos totales: " + turnosTotales + "\n" +
            	    "Tiempo restante J1: " + tiempoFormateadoJ1 + "\n" +
            	    "Tiempo restante J2: " + tiempoFormateadoJ2,
            	    "VICTORIA", JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
            new MainMenu().setVisible(true);
        }
    }

    private void cambiarTurno() {
        if (!yaDisparo) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Pasar sin disparar?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
        }

        // Pausar cronómetro mientras se muestra el diálogo
        cronometroPausado = true;

        turnosTotales++;

        // Sumar 6 s al jugador que termina su turno
        if (jugadorActual == j1) {
            tiempoRestanteJ1 += 6;
        } else {
            tiempoRestanteJ2 += 6;
        }

        // Diálogo de espera
        String tiempoActual = (jugadorActual == j1) ? formatearTiempo(tiempoRestanteJ1)
                                                    : formatearTiempo(tiempoRestanteJ2);
        JOptionPane.showMessageDialog(this, 
            "Fin del turno. Tiempo restante del jugador: " + tiempoActual + "\n\n" +
            "Pasa el dispositivo al OTRO jugador.",
            "Cambio de Jugador", JOptionPane.INFORMATION_MESSAGE);

        // Intercambio de roles
        Jugador temp = jugadorActual;
        jugadorActual = oponente;
        oponente = temp;

        // Resetear estado del turno
        yaDisparo = false;
        superDisparoActivo = false;
        megaDisparoActivo = false;

        // Actualizar label del reloj al jugador que empieza
        if (jugadorActual == j1) {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ1));
        } else {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ2));
        }

        // Reanudar cronómetro
        cronometroPausado = false;

        // Actualizar interfaz
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        labelInfoJugador.setText("Turno del JUGADOR " + jugadorActual.getId());
        labelAciertos.setText("Aciertos: " + jugadorActual.getAciertos());
        labelFallos.setText("Fallos: " + jugadorActual.getFallos());
        labelEscudos.setText("Escudos: " + jugadorActual.getEscudos());
        
        botonSuperDisparo.setText("Super Disparo (" + jugadorActual.getSuperDisparos() + ")");
        botonMegaDisparo.setText("Mega Disparo (" + jugadorActual.getMegaDisparos() + ")");
        botonEscudo.setText("Escudo (" + jugadorActual.getEscudos() + ")");
        // Desactivar botoones
        botonSuperDisparo.setEnabled(jugadorActual.getSuperDisparos() > 0);
        botonMegaDisparo.setEnabled(jugadorActual.getMegaDisparos() > 0);
        botonEscudo.setEnabled(jugadorActual.getEscudos() > 0);
        
        // Tablero Ataque
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

        // Tablero Propio
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
    
    private void iniciarCronometro() {
        hiloCronometro = new Thread(() -> {
            while (juegoActivo) {
                try {
                    Thread.sleep(1000);

                    if (!cronometroPausado) {
                        if (jugadorActual == j1) {
                            tiempoRestanteJ1--;
                            SwingUtilities.invokeLater(() -> labelTiempo.setText(formatearTiempo(tiempoRestanteJ1)));
                            if (tiempoRestanteJ1 <= 0) perderPorTiempo(j1);
                        } else {
                            tiempoRestanteJ2--;
                            SwingUtilities.invokeLater(() -> labelTiempo.setText(formatearTiempo(tiempoRestanteJ2)));
                            if (tiempoRestanteJ2 <= 0) perderPorTiempo(j2);
                        }
                    }

                } catch (InterruptedException e) { return; }
            }
        });
        hiloCronometro.start();
    }

    private String formatearTiempo(int totalSegundos) {
        int min = totalSegundos / 60;
        int seg = totalSegundos % 60;
        return String.format("%02d:%02d", min, seg);
    }
    
    private void perderPorTiempo(Jugador jugador) {
        juegoActivo = false;
        JOptionPane.showMessageDialog(this,
            "¡EL JUGADOR " + jugador.getId() + " SE QUEDÓ SIN TIEMPO!\n" +
            "El jugador " + oponente.getId() + " gana la partida.",
            "DERROTA POR TIEMPO", JOptionPane.INFORMATION_MESSAGE);

        dispose();
        new MainMenu().setVisible(true);
    }
    private void activarEscudo() {
        if (jugadorActual.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this, "Ya tienes un escudo activo.");
            return;
        }
        if (jugadorActual.getEscudos() <= 0) {
            JOptionPane.showMessageDialog(this, "No te quedan escudos disponibles.");
            return;
        }

        jugadorActual.usarEscudo(); // resta 1 escudo y marca activo
        JOptionPane.showMessageDialog(this, "¡Escudo activado para este turno!");
        actualizarInterfaz();
    }
}
