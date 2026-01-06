
package gui;

import java.awt.*;

import javax.swing.*;
import db.EstadisticasDAO;
import domain.Jugador;
import domain.DetectorHundimiento;

public class VentanaJuego extends JFrame {

    private static final long serialVersionUID = 1L;

    // Colores del tema
    private final Color COLOR_BOTON = new Color(30, 136, 229);
    private final Color COLOR_BOTON_ESPECIAL = new Color(255, 152, 0);
    private final Color COLOR_BOTON_ESCUDO = new Color(76, 175, 80);
    private final Color COLOR_TEXTO = Color.WHITE;
    private final Color COLOR_AGUA = new Color(0, 150, 200);
    private final Color COLOR_BARCO = new Color(96, 125, 139);

    // Componentes UI
    private JLabel labelTiempo;
    private JLabel labelInfoJugador;
    private JButton[][] celdasAtaque = new JButton[10][10];
    private JPanel[][] celdasPropias = new JPanel[10][10];

    // Estadísticas UI
    private JLabel labelAciertos, labelFallos, labelBarcosHundidos;
    private JButton botonSuperDisparo, botonMegaDisparo, botonPasarTurno, botonEscudo;

    // Lógica del Juego
    private Jugador j1;
    private Jugador j2;
    private Jugador jugadorActual;
    private Jugador oponente;

    private int turnosTotales = 0;
    private boolean superDisparoActivo = false;
    private boolean megaDisparoActivo = false;
    private boolean yaDisparo = false; // Controla si el turno ha terminado

    // Variables para el tiempo
    private int tiempoRestanteJ1 = 120; // segundos restantes del jugador 1
    private int tiempoRestanteJ2 = 120; // segundos restantes de jugador 2
    private volatile boolean cronometroPausado = false;

    private Thread hiloCronometro;
    private volatile boolean juegoActivo = true;
    private EstadisticasDAO estadisticasDAO;

    public VentanaJuego(int numeroJugadorInicial, int superDisparos, int megaDisparos, int escudos,
            int[][] tableroJ1, int[][] tableroJ2) {

        this.j1 = new Jugador(1, tableroJ1, superDisparos, megaDisparos, escudos);
        this.j2 = new Jugador(2, tableroJ2, superDisparos, megaDisparos, escudos);
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
        setSize(900, 650);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void inicializarComponentes() {
        // Panel principal con gradiente
        GradientPanel panelPrincipal = new GradientPanel();
        panelPrincipal.setLayout(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setContentPane(panelPrincipal);

        // --- TOP PANEL (Estadísticas y Tiempo) ---
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setOpaque(false);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsPanel.setOpaque(false);
        labelAciertos = crearLabelEstilo("Aciertos: 0", COLOR_TEXTO);
        labelFallos = crearLabelEstilo("Fallos: 0", COLOR_TEXTO);
        labelBarcosHundidos = crearLabelEstilo("Hundidos: 0", COLOR_TEXTO);

        statsPanel.add(labelAciertos);
        statsPanel.add(labelFallos);
        statsPanel.add(labelBarcosHundidos);

        labelTiempo = new JLabel("00:00");
        labelTiempo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelTiempo.setForeground(COLOR_TEXTO);

        if (jugadorActual == j1) {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ1));
        } else {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ2));
        }

        panelInfo.add(statsPanel, BorderLayout.WEST);
        panelInfo.add(labelTiempo, BorderLayout.EAST);

        labelInfoJugador = new JLabel("Turno del JUGADOR " + jugadorActual.getId(), SwingConstants.CENTER);
        labelInfoJugador.setFont(new Font("Segoe UI", Font.BOLD, 20));
        labelInfoJugador.setForeground(COLOR_TEXTO);
        panelInfo.add(labelInfoJugador, BorderLayout.SOUTH);

        panelPrincipal.add(panelInfo, BorderLayout.NORTH);

        // --- CENTER (TABLERO ATAQUE) ---
        JPanel panelTableroAtaque = new JPanel(new GridLayout(10, 10));
        panelTableroAtaque.setOpaque(false);
        panelTableroAtaque.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                "RADAR DE ATAQUE (Oponente)",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXTO));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton btn = new JButton();
                btn.setBackground(COLOR_AGUA);
                btn.setFocusPainted(false);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                int r = i, c = j;
                btn.addActionListener(e -> procesarClicCelda(r, c));
                celdasAtaque[i][j] = btn;
                panelTableroAtaque.add(btn);
            }
        }
        panelPrincipal.add(panelTableroAtaque, BorderLayout.CENTER);

        // --- RIGHT (CONTROLES Y TABLERO PROPIO) ---
        JPanel panelDerecho = new JPanel();
        panelDerecho.setOpaque(false);
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setPreferredSize(new Dimension(280, 0));

        // El borde debe coincidir con el del radar para alinear los contenidos
        panelDerecho.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(0, 10, 0, 0),
                "Tu Flota:",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_TEXTO));

        // Tablero propio
        JPanel miniTablero = new JPanel(new GridLayout(10, 10));
        miniTablero.setPreferredSize(new Dimension(250, 250));
        miniTablero.setMaximumSize(new Dimension(250, 250));
        miniTablero.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255), 2));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JPanel p = new JPanel();
                p.setBackground(COLOR_AGUA);
                p.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                celdasPropias[i][j] = p;
                miniTablero.add(p);
            }
        }
        panelDerecho.add(miniTablero);

        // Empujador para centrar verticalmente o alinear botones al fondo
        panelDerecho.add(Box.createVerticalGlue());

        // Botones
        botonSuperDisparo = crearBotonEstilizado("Super Disparo", COLOR_BOTON_ESPECIAL);
        botonSuperDisparo.addActionListener(e -> activarHabilidad(true, false));

        botonMegaDisparo = crearBotonEstilizado("Mega Disparo", COLOR_BOTON_ESPECIAL);
        botonMegaDisparo.addActionListener(e -> activarHabilidad(false, true));

        botonEscudo = crearBotonEstilizado("Escudo (" + jugadorActual.getEscudos() + ")", COLOR_BOTON_ESCUDO);
        botonEscudo.addActionListener(e -> activarEscudo());

        botonPasarTurno = crearBotonEstilizado("TERMINAR TURNO", COLOR_BOTON);
        botonPasarTurno.addActionListener(e -> cambiarTurno());

        panelDerecho.add(alinearBoton(botonEscudo));
        panelDerecho.add(Box.createVerticalStrut(10)); // Pequeña separación
        panelDerecho.add(alinearBoton(botonSuperDisparo));
        panelDerecho.add(Box.createVerticalStrut(10)); // Pequeña separación
        panelDerecho.add(alinearBoton(botonMegaDisparo));

        panelDerecho.add(Box.createVerticalStrut(30)); // Separación mayor
        panelDerecho.add(alinearBoton(botonPasarTurno));

        panelPrincipal.add(panelDerecho, BorderLayout.EAST);
    }

    private JLabel crearLabelEstilo(String texto, Color color) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setForeground(color);
        return l;
    }

    private Component alinearBoton(JButton b) {
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(240, 45));
        return b;
    }

    private void procesarClicCelda(int fila, int col) {
        // Si ya se ha acabado el turno (porque se falló o se usó especial), no dejar
        // clicar
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Tu turno de ataque ha terminado. Pulsa 'Terminar Turno'.");
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

    // Logica de turnos
    private void ejecutarDisparoSimple(int fila, int col) {
        // 1. Verificar Escudo
        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this,
                    "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                            "Tu disparo ha sido bloqueado y tu turno termina.");
            oponente.resetEscudoTurno();
            yaDisparo = true;
            actualizarInterfaz();
            return;
        }

        int resultado = procesarImpacto(fila, col);

        // Evaluar resultado del disparo y mostrar mensajes
        if (resultado == 2) {
            // Hundido - detectar tamaño con recursividad
            DetectorHundimiento detector = new DetectorHundimiento(
                    oponente.getTableroPropio(),
                    oponente.getImpactosRecibidos());
            int tamaño = detector.contarTamañoBarco(fila, col);

            JOptionPane.showMessageDialog(this,
                    "💥 ¡¡HUNDIDO!!  💥\n\n" +
                            "¡Has hundido un barco de " + tamaño + " casillas!\n" +
                            "Barcos hundidos: " + jugadorActual.getBarcosHundidos() + "\n\n" +
                            "¡Sigue disparando!",
                    "¡BARCO HUNDIDO!", JOptionPane.WARNING_MESSAGE);

        } else if (resultado == 1) {
            // TOCADO
            JOptionPane.showMessageDialog(this,
                    "🎯 ¡TOCADO!\n\n¡Sigue disparando!",
                    "Impacto", JOptionPane.INFORMATION_MESSAGE);

        } else {
            // AGUA - Se acaba el turno
            yaDisparo = true;
            JOptionPane.showMessageDialog(this,
                    "🌊 Agua.. .\n\nFin de tus disparos.",
                    "Fallo", JOptionPane.INFORMATION_MESSAGE);
        }

        verificarVictoria();
        actualizarInterfaz();
    }

    // Procesa un impacto: 0=Agua, 1=Tocado, 2=Hundido
    private int procesarImpacto(int fila, int col) {
        // Validar límites
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) {
            return 0;
        }

        // Verificar si ya se disparó aquí
        if (jugadorActual.getTableroDisparos()[fila][col]) {
            return 0; // Ya disparado
        }

        // Marcar como disparado
        jugadorActual.getTableroDisparos()[fila][col] = true;

        // Verificar si hay barco en esta posición (ID > 0)
        if (oponente.getTableroPropio()[fila][col] > 0) {
            // ¡IMPACTO!
            oponente.getImpactosRecibidos()[fila][col] = true;
            jugadorActual.incrementarAciertos();
            oponente.recibirImpacto();

            // Detectar si el barco está hundido usando recursividad
            DetectorHundimiento detector = new DetectorHundimiento(
                    oponente.getTableroPropio(),
                    oponente.getImpactosRecibidos());

            if (detector.estaBarcoHundido(fila, col)) {
                jugadorActual.incrementarBarcosHundidos();
                oponente.marcarBarcoHundido(fila, col, oponente.getTableroPropio());
                return 2; // Hundido
            }

            return 1; // Tocado (pero no hundido)
        } else {
            // AGUA
            jugadorActual.incrementarFallos();
            return 0;
        }
    }

    private void ejecutarSuperDisparo(int r, int c) {
        if (jugadorActual.getSuperDisparos() <= 0)
            return;

        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this,
                    "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                            "Tu disparo ha sido bloqueado.");
            oponente.resetEscudoTurno();
            yaDisparo = true;
            actualizarInterfaz();
            return;
        }

        int[][] coords = { { r, c }, { r - 1, c }, { r + 1, c }, { r, c - 1 }, { r, c + 1 } };
        int aciertos = 0;
        int hundidos = 0;

        for (int[] par : coords) {
            int res = procesarImpacto(par[0], par[1]);
            if (res == 1)
                aciertos++;
            if (res == 2) {
                aciertos++;
                hundidos++;
            }
        }

        jugadorActual.usarSuperDisparo();

        // Mensaje mejorado
        String mensaje = "Super Disparo completado.\n\nImpactos:  " + aciertos;
        if (hundidos > 0) {
            mensaje += "\n💥 ¡Barcos hundidos: " + hundidos + "!";
        }
        mensaje += "\n\nEl ataque especial finaliza tu turno.";

        superDisparoActivo = false;
        megaDisparoActivo = false;
        yaDisparo = true;

        verificarVictoria();
        actualizarInterfaz();
        JOptionPane.showMessageDialog(this, mensaje, "Super Disparo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void ejecutarMegaDisparo(int r, int c) {
        if (jugadorActual.getMegaDisparos() <= 0)
            return;

        if (oponente.tieneEscudoActivo()) {
            JOptionPane.showMessageDialog(this,
                    "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" +
                            "El MEGA DISPARO ha sido bloqueado.");
            oponente.resetEscudoTurno();
            yaDisparo = true;
            actualizarInterfaz();
            return;
        }

        int aciertos = 0;
        int hundidos = 0;

        for (int i = r - 1; i <= r + 1; i++) {
            for (int j = c - 1; j <= c + 1; j++) {
                int res = procesarImpacto(i, j);
                if (res == 1)
                    aciertos++;
                if (res == 2) {
                    aciertos++;
                    hundidos++;
                }
            }
        }

        jugadorActual.usarMegaDisparo();

        // Mensaje mejorado
        String mensaje = "Mega Disparo completado.\n\nImpactos: " + aciertos;
        if (hundidos > 0) {
            mensaje += "\n💥 ¡Barcos hundidos: " + hundidos + "!";
        }
        mensaje += "\n\nEl ataque especial finaliza tu turno.";

        superDisparoActivo = false;
        megaDisparoActivo = false;
        yaDisparo = true;

        verificarVictoria();
        actualizarInterfaz();
        JOptionPane.showMessageDialog(this, mensaje, "Mega Disparo", JOptionPane.INFORMATION_MESSAGE);
    }

    @SuppressWarnings("unused")
    private void finalizarAtaqueEspecial(String nombre, int aciertos) {
        superDisparoActivo = false;
        megaDisparoActivo = false;

        // REGLA: Los ataques especiales siempre acaban el turno, den o no.
        yaDisparo = true;

        verificarVictoria();
        actualizarInterfaz();
        JOptionPane.showMessageDialog(this,
                nombre + " completado.\nImpactos: " + aciertos + "\nEl ataque especial finaliza tu turno.");
    }

    private void activarHabilidad(boolean esSuper, boolean esMega) {
        if (yaDisparo) {
            JOptionPane.showMessageDialog(this, "Tu turno de ataque ha terminado.");
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
        // Solo verificamos si ha disparado si es un turno donde falló.
        // Si ha acertado 3 veces y quiere pasar, puede hacerlo.
        if (!yaDisparo) {
            // Opción: Permitir pasar turno sin disparar si se arrepiente, o forzar disparo.
            // Dejamos la confirmación:
            int confirm = JOptionPane.showConfirmDialog(this, "¿Pasar sin atacar/fallar?", "Confirmar",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;
        }

        cronometroPausado = true;
        turnosTotales++;

        if (jugadorActual == j1) {
            tiempoRestanteJ1 += 6;
        } else {
            tiempoRestanteJ2 += 6;
        }

        String tiempoActual = (jugadorActual == j1) ? formatearTiempo(tiempoRestanteJ1)
                : formatearTiempo(tiempoRestanteJ2);

        this.setVisible(false);

        JOptionPane.showMessageDialog(this,
                "Fin del turno. Tiempo restante del jugador: " + tiempoActual + "\n\n" +
                        "Pasa el dispositivo al OTRO jugador.",
                "Cambio de Jugador", JOptionPane.INFORMATION_MESSAGE);

        this.setVisible(true);

        Jugador temp = jugadorActual;
        jugadorActual = oponente;
        oponente = temp;

        // Resetear variables de turno
        yaDisparo = false;
        superDisparoActivo = false;
        megaDisparoActivo = false;

        if (jugadorActual == j1) {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ1));
        } else {
            labelTiempo.setText(formatearTiempo(tiempoRestanteJ2));
        }

        cronometroPausado = false;
        actualizarInterfaz();
    }

    private void actualizarInterfaz() {
        labelInfoJugador.setText("Turno del JUGADOR " + jugadorActual.getId());
        labelAciertos.setText("Aciertos: " + jugadorActual.getAciertos());
        labelFallos.setText("Fallos: " + jugadorActual.getFallos());

        // Actualizar contador de barcos hundidos
        labelBarcosHundidos.setText("Hundidos: " + jugadorActual.getBarcosHundidos());

        botonSuperDisparo.setText("Super Disparo (" + jugadorActual.getSuperDisparos() + ")");
        botonMegaDisparo.setText("Mega Disparo (" + jugadorActual.getMegaDisparos() + ")");
        botonEscudo.setText("Escudo (" + jugadorActual.getEscudos() + ")");

        botonSuperDisparo.setEnabled(jugadorActual.getSuperDisparos() > 0);
        botonMegaDisparo.setEnabled(jugadorActual.getMegaDisparos() > 0);
        botonEscudo.setEnabled(jugadorActual.getEscudos() > 0);

        // ... resto del código existente para actualizar tableros ...

        // Tablero Ataque
        boolean[][] disparos = jugadorActual.getTableroDisparos();
        int[][] barcosEnemigos = oponente.getTableroPropio();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (disparos[i][j]) {
                    celdasAtaque[i][j].setEnabled(false);
                    if (barcosEnemigos[i][j] > 0) { // Si hay barco (ID > 0)
                        // Hundido = ROJO OSCURO, Tocado = NARANJA
                        if (oponente.getCasillasHundidas()[i][j]) {
                            celdasAtaque[i][j].setBackground(Color.RED); // Rojo oscuro
                        } else {
                            celdasAtaque[i][j].setBackground(Color.ORANGE); // Naranja
                        }
                        celdasAtaque[i][j].setText("X");
                        celdasAtaque[i][j].setForeground(Color.BLACK);
                    } else {
                        celdasAtaque[i][j].setBackground(Color.CYAN); // Azul profundo (agua disparada)
                        celdasAtaque[i][j].setText("O");
                        celdasAtaque[i][j].setForeground(Color.BLACK);
                    }
                } else {
                    celdasAtaque[i][j].setEnabled(true);
                    celdasAtaque[i][j].setBackground(COLOR_AGUA);
                    celdasAtaque[i][j].setText("");
                }
            }
        }

        // Tablero Propio
        int[][] miTablero = jugadorActual.getTableroPropio();
        boolean[][] misDaños = jugadorActual.getImpactosRecibidos();

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (misDaños[i][j]) {
                    celdasPropias[i][j]
                            .setBackground((miTablero[i][j] > 0) ? Color.RED : Color.CYAN);
                } else {
                    celdasPropias[i][j].setBackground((miTablero[i][j] > 0) ? COLOR_BARCO : COLOR_AGUA);
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
                            if (tiempoRestanteJ1 <= 0)
                                perderPorTiempo(j1);
                        } else {
                            tiempoRestanteJ2--;
                            SwingUtilities.invokeLater(() -> labelTiempo.setText(formatearTiempo(tiempoRestanteJ2)));
                            if (tiempoRestanteJ2 <= 0)
                                perderPorTiempo(j2);
                        }
                    }

                } catch (InterruptedException e) {
                    return;
                }
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

        jugadorActual.usarEscudo();
        JOptionPane.showMessageDialog(this, "¡Escudo activado para este turno!");
        actualizarInterfaz();
    }

    private JButton crearBotonEstilizado(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(COLOR_TEXTO);
        btn.setBackground(colorFondo);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                new javax.swing.border.EmptyBorder(8, 20, 8, 20)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(colorFondo);
            }
        });
        return btn;
    }

    class GradientPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setPaint(new GradientPaint(0, 0, new Color(10, 25, 50), 0, getHeight(), new Color(0, 50, 100)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}