
package gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import db.EstadisticasDAO;
import domain.Jugador;
import domain.DetectorHundimiento;

public class VentanaJuego extends JFrame {

	private static final long serialVersionUID = 1L;

	// Colores del tema
	private final Color COLOR_BOTON = new Color(30, 136, 229);
	private final Color COLOR_BOTON_ESPECIAL = new Color(255, 152, 0);
	private final Color COLOR_BOTON_ESCUDO = new Color(76, 175, 80);
	private final Color COLOR_BOTON_RENDIRSE = new Color(211, 47, 47);
	private final Color COLOR_TEXTO = Color.WHITE;
	private final Color COLOR_AGUA = new Color(0, 150, 200);

	// Componentes UI
	private JLabel labelTiempo;
	private JLabel labelInfoJugador;
	private JButton[][] celdasAtaque = new JButton[10][10];
	private JButton botonRendirse;
	private JTable tablaFlota;
	private ModeloFlota modeloFlota;
	private BufferedImage imagenBarcoPropio; // Imagen del equipo actual

	// Musica de fondo
	private ReproductorAudio musicaFondo;

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

	public VentanaJuego(int numeroJugadorInicial, int superDisparos, int megaDisparos, int escudos, int[][] tableroJ1,
			int[][] tableroJ2, String equipoJ1, String equipoJ2) {

		this.j1 = new Jugador(1, tableroJ1, superDisparos, megaDisparos, escudos, equipoJ1);
		this.j2 = new Jugador(2, tableroJ2, superDisparos, megaDisparos, escudos, equipoJ2);
		this.estadisticasDAO = new EstadisticasDAO();

		if (numeroJugadorInicial == 1) {
			configurarTurno(j1, j2);
		} else {
			configurarTurno(j2, j1);
		}

		configurarVentana();
		cargarImagenEquipo();
		inicializarComponentes();
		actualizarInterfaz();
		iniciarCronometro();
		
		musicaFondo = new ReproductorAudio();
        // 0.0f es el volumen máximo natural del archivo. 
        musicaFondo.reproducir("resources/sounds/musicaFondo.wav", -15f);

		
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
		panelTableroAtaque.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(),
				"RADAR DE ATAQUE (Oponente)", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_TEXTO));

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
		panelDerecho.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0),
				"Tu Flota:", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_TEXTO));

		// --- TABLERO PROPIO ---
		modeloFlota = new ModeloFlota();
		reconstruirFlotaModelo();

		tablaFlota = new JTable(modeloFlota);
		tablaFlota.setRowHeight(25); // 10 filas * 25px = 250px exactos de cuerpo
		tablaFlota.setEnabled(false); // Desactiva clicks
		tablaFlota.setShowGrid(true);
		tablaFlota.setGridColor(new Color(0, 0, 0, 50));
		tablaFlota.setDefaultRenderer(Object.class, new BarcoRenderer());
		tablaFlota.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // Importante para control total

		// --- BLOQUEO TOTAL DEL HEADER (Nivel Paranoico) ---
		javax.swing.table.JTableHeader header = tablaFlota.getTableHeader();
		header.setResizingAllowed(false);
		header.setReorderingAllowed(false);
		header.setFont(new Font("Segoe UI", Font.BOLD, 12));

		// Bloqueamos cada columna individualmente para asegurar que no se pueden mover
		for (int i = 0; i < 10; i++) {
		    javax.swing.table.TableColumn col = tablaFlota.getColumnModel().getColumn(i);
		    col.setPreferredWidth(25);
		    col.setMinWidth(25);
		    col.setMaxWidth(25);
		    col.setResizable(false); // <--- ESTO ES LA CLAVE para que no aparezca la flecha de redimensionar
		}

		// --- CONTENEDOR AJUSTADO ---
		JPanel contenedorTabla = new JPanel(new BorderLayout());
		// Añadimos header y tabla
		contenedorTabla.add(header, BorderLayout.NORTH);
		contenedorTabla.add(tablaFlota, BorderLayout.CENTER);

		// --- CÁLCULO MATEMÁTICO DINÁMICO ---
		// Obtenemos la altura REAL que tiene la cabecera en tu ordenador
		int alturaHeader = header.getPreferredSize().height;
		int alturaCuerpo = 250; // 10 filas * 25px
		int borde = 2; // 2 pixeles de borde

		// La altura total debe ser la suma de todo. 
		// Antes faltaba sumar 'alturaHeader', por eso se descuadraba.
		int altoTotal = alturaHeader + alturaCuerpo + (borde * 2);
		int anchoTotal = 250 + (borde * 2);

		Dimension dimensionExacta = new Dimension(anchoTotal, altoTotal);

		contenedorTabla.setPreferredSize(dimensionExacta);
		contenedorTabla.setMaximumSize(dimensionExacta);
		contenedorTabla.setMinimumSize(dimensionExacta);
		contenedorTabla.setBorder(BorderFactory.createLineBorder(new Color(100, 200, 255), borde));

		panelDerecho.add(contenedorTabla);

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

		botonRendirse = crearBotonEstilizado("RENDIRSE", COLOR_BOTON_RENDIRSE);
		botonRendirse.addActionListener(e -> {
			rendirse();
			if (musicaFondo != null) musicaFondo.detener();
		});
		
		panelDerecho.add(alinearBoton(botonEscudo));
		panelDerecho.add(Box.createVerticalStrut(5)); // Pequeña separación
		panelDerecho.add(alinearBoton(botonSuperDisparo));
		panelDerecho.add(Box.createVerticalStrut(5)); // Pequeña separación
		panelDerecho.add(alinearBoton(botonMegaDisparo));

		panelDerecho.add(Box.createVerticalStrut(20)); // Separación mayor
		panelDerecho.add(alinearBoton(botonPasarTurno));

		panelDerecho.add(Box.createVerticalStrut(5)); // Separación mayor
		panelDerecho.add(alinearBoton(botonRendirse));

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
		b.setMaximumSize(new Dimension(240, 35));
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
			EfectosSonido.reproducir("resources/sounds/sonido_escudo.wav", -5.0f);
			JOptionPane.showMessageDialog(this, "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n"
					+ "Tu disparo ha sido bloqueado y tu turno termina.");
			oponente.resetEscudoTurno();
			yaDisparo = true;
			actualizarInterfaz();
			return;
		}

		int resultado = procesarImpacto(fila, col);
		
		// Evaluar resultado del disparo y mostrar mensajes
		if (resultado == 2) {
			// Hundido - detectar tamaño con recursividad
			DetectorHundimiento detector = new DetectorHundimiento(oponente.getTableroPropio(),
					oponente.getImpactosRecibidos());
			int tamaño = detector.contarTamañoBarco(fila, col);

			JOptionPane.showMessageDialog(this,
					"💥 ¡¡HUNDIDO!!  💥\n\n" + "¡Has hundido un barco de " + tamaño + " casillas!\n"
							+ "Barcos hundidos: " + jugadorActual.getBarcosHundidos() + "\n\n" + "¡Sigue disparando!",
					"¡BARCO HUNDIDO!", JOptionPane.WARNING_MESSAGE);

		} else if (resultado == 1) {
			// TOCADO
			EfectosSonido.reproducir("resources/sounds/sonido_disparo.wav", -6.0f);
			JOptionPane.showMessageDialog(this, "🎯 ¡TOCADO!\n\n¡Sigue disparando!", "Impacto",
					JOptionPane.INFORMATION_MESSAGE);

		} else {
			// AGUA - Se acaba el turno
			EfectosSonido.reproducir("resources/sounds/sonido_agua.wav", -8.0f);
			yaDisparo = true;
			JOptionPane.showMessageDialog(this, "🌊 Agua.. .\n\nFin de tus disparos.", "Fallo",
					JOptionPane.INFORMATION_MESSAGE);
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
			DetectorHundimiento detector = new DetectorHundimiento(oponente.getTableroPropio(),
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
			EfectosSonido.reproducir("resources/sounds/sonido_escudo.wav", -5.0f);
			JOptionPane.showMessageDialog(this,
					"El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n" + "Tu disparo ha sido bloqueado.");
			oponente.resetEscudoTurno();
			yaDisparo = true;
			actualizarInterfaz();
			return;
		}
		EfectosSonido.reproducir("resources/sounds/sonido_disparo2.wav", -5.0f);

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
			EfectosSonido.reproducir("resources/sounds/sonido_escudo.wav", -5.0f);
			JOptionPane.showMessageDialog(this, "El jugador " + oponente.getId() + " tenía un ESCUDO activo.\n"
					+ "El MEGA DISPARO ha sido bloqueado.");
			oponente.resetEscudoTurno();
			yaDisparo = true;
			actualizarInterfaz();
			return;
		}
		EfectosSonido.reproducir("resources/sounds/sonido_disparo2.wav", -5.0f);

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
					"¡EL JUGADOR " + jugadorActual.getId() + " GANA LA GUERRA!\n\n" + "Turnos totales: " + turnosTotales
							+ "\n" + "Tiempo restante J1: " + tiempoFormateadoJ1 + "\n" + "Tiempo restante J2: "
							+ tiempoFormateadoJ2,
					"VICTORIA", JOptionPane.INFORMATION_MESSAGE);

			
			dispose();
			new MainMenu().setVisible(true);
			if (musicaFondo != null) {
                musicaFondo.detener();
			}
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

		JOptionPane.showMessageDialog(this, "Fin del turno. Tiempo restante del jugador: " + tiempoActual + "\n\n"
				+ "Pasa el dispositivo al OTRO jugador.", "Cambio de Jugador", JOptionPane.INFORMATION_MESSAGE);

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
		// Recargar imagen del nuevo jugador y reconstruir su tablero visual
		cargarImagenEquipo();
		reconstruirFlotaModelo();
		actualizarInterfaz();
	}

	private void rendirse() {
		int confirm = JOptionPane.showConfirmDialog(this,
				"¿Seguro que deseas rendirte?\n\nEl jugador " + oponente.getId() + " ganará la partida.",
				"Confirmar Rendición", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		juegoActivo = false;

		JOptionPane.showMessageDialog(this, "El JUGADOR " + jugadorActual.getId() + " se ha rendido.\n\n"
				+ "🏆 GANA EL JUGADOR " + oponente.getId(), "Fin de la partida", JOptionPane.INFORMATION_MESSAGE);

		

		dispose();
		new MainMenu().setVisible(true);
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

		modeloFlota.fireTableDataChanged();
		tablaFlota.repaint();
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
		JOptionPane.showMessageDialog(this, "¡EL JUGADOR " + jugador.getId() + " SE QUEDÓ SIN TIEMPO!\n" + "El jugador "
				+ oponente.getId() + " gana la partida.", "DERROTA POR TIEMPO", JOptionPane.INFORMATION_MESSAGE);
		
		if (musicaFondo != null) musicaFondo.detener();

		

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
				BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1), new EmptyBorder(4, 20, 4, 20)));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(colorFondo.brighter());
			}

			public void mouseExited(MouseEvent e) {
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

	private void cargarImagenEquipo() {
		try {
			String equipo = jugadorActual.getNombreEquipo();
			if ("Submarine".equals(equipo))
				equipo = "SubMarine";
			String path = "resources/images/Ship/Ship" + equipo + "Hull.png";
			File f = new File(path);
			if (f.exists())
				imagenBarcoPropio = ImageIO.read(f);
			else
				imagenBarcoPropio = ImageIO.read(getClass().getResource("/images/Ship/Ship" + equipo + "Hull.png"));
		} catch (Exception e) {
			imagenBarcoPropio = null;
		}
	}

	// Convertimos el int[][] a PiezaBarco[][] para poder pintar trozos
	private void reconstruirFlotaModelo() {
		int[][] ids = jugadorActual.getTableroPropio();
		PiezaBarco[][] piezas = new PiezaBarco[10][10];
		boolean[][] visitado = new boolean[10][10];

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				int id = ids[i][j];
				if (id > 0 && !visitado[i][j]) {
					// Nuevo barco encontrado. Determinar tamaño y orientación.
					boolean horiz = false;
					int tam = 0;

					// Suposición: ID único contiguo.
					if (j + 1 < 10 && ids[i][j + 1] == id)
						horiz = true;

					// Contar tamaño
					if (horiz) {
						int c = j;
						while (c < 10 && ids[i][c] == id) {
							tam++;
							c++;
						}
					} else {
						// Verificar vertical
						int r = i;
						while (r < 10 && ids[r][j] == id) {
							tam++;
							r++;
						}
					}

					// Si tam es 1 (puede ser horizontal o vertical sin vecinos), asumimos horiz por
					// defecto o checkeamos limite
					if (tam == 0)
						tam = 1;

					// Rellenar PiezaBarco
					for (int k = 0; k < tam; k++) {
						int r = i + (horiz ? 0 : k);
						int c = j + (horiz ? k : 0);
						piezas[r][c] = new PiezaBarco(id, k, tam, horiz);
						visitado[r][c] = true;
					}
				}
			}
		}
		modeloFlota.setDatos(piezas);
	}

	class ModeloFlota extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		private PiezaBarco[][] datos = new PiezaBarco[10][10];

		public ModeloFlota() {
			super(10, 10);
		}

		public void setDatos(PiezaBarco[][] d) {
			this.datos = d;
			fireTableDataChanged();
		}

		public Object getValueAt(int r, int c) {
			return datos[r][c];
		}

		public boolean isCellEditable(int r, int c) {
			return false;
		}
	}

	class PiezaBarco {
		int id, idx, total;
		boolean horiz;

		public PiezaBarco(int id, int idx, int total, boolean horiz) {
			this.id = id;
			this.idx = idx;
			this.total = total;
			this.horiz = horiz;
		}
	}

	class BarcoRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private PiezaBarco p;
		private int row, col;

		public BarcoRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
			this.p = (PiezaBarco) v;
			this.row = r;
			this.col = c;
			setBackground(COLOR_AGUA); // Fondo base
			return this;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			int w = getWidth(), h = getHeight();
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			boolean dañado = jugadorActual.getImpactosRecibidos()[row][col];

			if (p != null) {
				// Dibujar Barco
				if (imagenBarcoPropio != null) {
					if (p.horiz) {
						g2.drawImage(imagenBarcoPropio, -(p.idx * w), 0, w * p.total, h, null);
					} else {
						AffineTransform at = g2.getTransform();
						g2.translate(w / 2.0, h / 2.0);
						g2.rotate(Math.toRadians(90));
						g2.translate(-h / 2.0, -w / 2.0);
						g2.drawImage(imagenBarcoPropio, -(p.idx * h), 0, h * p.total, w, null);
						g2.setTransform(at);
					}
				} else {
					g2.setColor(Color.GRAY);
					g2.fillRect(2, 2, w - 4, h - 4);
				}
			}

			// Sobreponer daños (Si está dañado)
			if (dañado) {
				g2.setColor(new Color(255, 0, 0, 150)); // Rojo semitransparente
				g2.fillRect(0, 0, w, h);
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(3));
				g2.drawLine(5, 5, w - 5, h - 5);
				g2.drawLine(w - 5, 5, 5, h - 5);
			}
		}
	}
}