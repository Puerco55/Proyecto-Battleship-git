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

	// Tablero grande
	private JButton[][] celdasAtaque = new JButton[10][10];

	// Tablero pequeño
	private JPanel[][] celdasPropias = new JPanel[10][10];

	// Labels de estadisticas
	private JLabel labelAciertos;
	private JLabel labelFallos;
	private JLabel labelBarcosHundidos;

	// Botones de disparos especiales
	private JButton botonSuperDisparo;
	private JButton botonMegaDisparo;
	private JButton botonGuardar;

	// Estadisticas por jugador
	private int[] aciertos = { 0, 0 };
	private int[] fallos = { 0, 0 };
	private int[] barcosHundidosContador = { 0, 0 };
	private int[] superDisparos = new int[2];
	private int[] megaDisparos = new int[2];

	// Control de jugadores
	private int jugadorActual;

	// Contador de turnos totales para estadisticas
	private int turnosTotales = 0;

	// Tableros de ambos jugadores (true = hay barco)
	private boolean[][] tableroJugador1;
	private boolean[][] tableroJugador2;

	// Matrices para registrar los disparos de cada jugador
	private boolean[][] disparosJugador1 = new boolean[10][10];
	private boolean[][] disparosJugador2 = new boolean[10][10];

	// Matrices para registrar impactos recibidos en cada tablero
	private boolean[][] impactosEnTablero1 = new boolean[10][10];
	private boolean[][] impactosEnTablero2 = new boolean[10][10];

	// Total de casillas con barco de cada jugador
	private int totalCasillasBarcoJ1 = 0;
	private int totalCasillasBarcoJ2 = 0;

	// Casillas de barco acertadas por cada jugador
	private int casillasAcertadasPorJ1 = 0;
	private int casillasAcertadasPorJ2 = 0;

	// Control de si el jugador ya disparó este turno
	private boolean yaDisparo = false;

	public VentanaJuego(int numeroJugador, int superDisparosIniciales, int megaDisparosIniciales, boolean[][] tableroJ1,
			boolean[][] tableroJ2) {
		this.jugadorActual = numeroJugador;
		this.superDisparos[0] = superDisparosIniciales;
		this.superDisparos[1] = superDisparosIniciales;
		this.megaDisparos[0] = megaDisparosIniciales;
		this.megaDisparos[1] = megaDisparosIniciales;

		// Guardar los tableros de ambos jugadores
		this.tableroJugador1 = tableroJ1;
		this.tableroJugador2 = tableroJ2;

		// Contar casillas con barco de cada jugador
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (tableroJugador1[i][j])
					totalCasillasBarcoJ1++;
				if (tableroJugador2[i][j])
					totalCasillasBarcoJ2++;
			}
		}

		// Configuración de la ventana
		setTitle("Hundir la Flota - Turno del Jugador " + jugadorActual);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(650, 500);
		setLocationRelativeTo(null);
		setResizable(false);

		// Panel principal
		JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(panelPrincipal);

		// PANEL SUPERIOR (Estadísticas)
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

		// PANEL CENTRAL (Tablero de ataque)
		JPanel panelTableroAtaque = new JPanel(new GridLayout(10, 10));
		panelTableroAtaque.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK, 2),
				"Tablero del Oponente (Dispara aquí)"));
		panelTableroAtaque.setPreferredSize(new Dimension(380, 380));

		for (int fila = 0; fila < 10; fila++) {
			for (int col = 0; col < 10; col++) {
				JButton celda = new JButton();
				celda.setBackground(new Color(0, 150, 200));
				celda.setFocusPainted(false);
				celdasAtaque[fila][col] = celda;

				int f = fila, c = col;
				celda.addActionListener(e -> {
					procesarDisparo(f, c);
				});

				panelTableroAtaque.add(celda);
			}
		}
		panelPrincipal.add(panelTableroAtaque, BorderLayout.CENTER);

		// PANEL DERECHO
		JPanel panelDerecho = new JPanel();
		panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
		panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
		panelDerecho.setPreferredSize(new Dimension(200, 400));

		// Tablero pequeño del propio jugador
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

		// DISPAROS ESPECIALES
		JLabel labelDisparos = new JLabel("DISPAROS ESPECIALES:");
		labelDisparos.setFont(new Font("Arial", Font.BOLD, 12));
		labelDisparos.setAlignmentX(LEFT_ALIGNMENT);
		panelDerecho.add(labelDisparos);

		panelDerecho.add(Box.createVerticalStrut(10));

		// Super Disparo (disparo en cruz)
		botonSuperDisparo = new JButton("Super Disparo (" + superDisparos[0] + ")");
		botonSuperDisparo.setPreferredSize(new Dimension(180, 35));
		botonSuperDisparo.setMaximumSize(new Dimension(180, 35));
		botonSuperDisparo.setAlignmentX(LEFT_ALIGNMENT);
		botonSuperDisparo.setToolTipText("Dispara en cruz (5 casillas)");
		// TODO Implementar activarSuperDisparo()
		botonSuperDisparo.addActionListener(e -> activarSuperDisparo());
		panelDerecho.add(botonSuperDisparo);

		panelDerecho.add(Box.createVerticalStrut(10));

		// Mega Disparo (disparo 3x3)
		botonMegaDisparo = new JButton("Mega Disparo (" + megaDisparos[0] + ")");
		botonMegaDisparo.setPreferredSize(new Dimension(180, 35));
		botonMegaDisparo.setMaximumSize(new Dimension(180, 35));
		botonMegaDisparo.setAlignmentX(LEFT_ALIGNMENT);
		botonMegaDisparo.setToolTipText("Dispara en área 3x3 (9 casillas)");
		// TODO Implementar activarMegaDisparo()
		botonMegaDisparo.addActionListener(e -> activarMegaDisparo());
		panelDerecho.add(botonMegaDisparo);

		panelDerecho.add(Box.createVerticalGlue());

		// Botón para pasar turno
		botonGuardar = new JButton("Pasar Turno");
		botonGuardar.setPreferredSize(new Dimension(180, 40));
		botonGuardar.setMaximumSize(new Dimension(180, 40));
		botonGuardar.setAlignmentX(LEFT_ALIGNMENT);
		botonGuardar.setFont(new Font("Arial", Font.BOLD, 14));
		botonGuardar.setBackground(new Color(100, 180, 100));
		botonGuardar.addActionListener(e -> cambiarJugador());
		panelDerecho.add(botonGuardar);

		panelPrincipal.add(panelDerecho, BorderLayout.EAST);

		// Inicializar el tablero propio del jugador actual
		actualizarTableroPropio();
		actualizarEstadisticas();
		actualizarBotonesDisparos();
	}

	// Actualiza el tablero propio del jugador actual
	private void actualizarTableroPropio() {
		boolean[][] tableroActual;
		boolean[][] impactosRecibidos;

		if (jugadorActual == 1) {
			tableroActual = tableroJugador1;
			impactosRecibidos = impactosEnTablero1;
		} else {
			tableroActual = tableroJugador2;
			impactosRecibidos = impactosEnTablero2;
		}

		for (int fila = 0; fila < 10; fila++) {
			for (int col = 0; col < 10; col++) {
				if (impactosRecibidos[fila][col]) {
					if (tableroActual[fila][col]) {
						// Impacto en un barco
						celdasPropias[fila][col].setBackground(Color.RED);
					} else {
						// Disparo al agua
						celdasPropias[fila][col].setBackground(Color.WHITE);
					}
				} else if (tableroActual[fila][col]) {
					// Barco sin impactar
					celdasPropias[fila][col].setBackground(Color.GRAY);
				} else {
					// Agua sin disparar
					celdasPropias[fila][col].setBackground(new Color(0, 150, 200));
				}
			}
		}
	}

	// Actualiza el tablero de ataque del jugador actual
	private void actualizarTableroAtaque() {
		boolean[][] disparosActuales;
		boolean[][] tableroOponente;

		if (jugadorActual == 1) {
			disparosActuales = disparosJugador1;
			tableroOponente = tableroJugador2;
		} else {
			disparosActuales = disparosJugador2;
			tableroOponente = tableroJugador1;
		}

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

	// Actualiza las estadísticas
	private void actualizarEstadisticas() {
		int idx = jugadorActual - 1;
		labelAciertos.setText("Aciertos: " + aciertos[idx]);
		labelFallos.setText("Fallos: " + fallos[idx]);
		labelBarcosHundidos.setText("Barcos hundidos: " + barcosHundidosContador[idx]);
	}

	// Actualiza los textos de los botones de disparos especiales
	private void actualizarBotonesDisparos() {
		int idx = jugadorActual - 1;
		botonSuperDisparo.setText("Super Disparo (" + superDisparos[idx] + ")");
		botonMegaDisparo.setText("Mega Disparo (" + megaDisparos[idx] + ")");
	}

	// Procesa un disparo normal en la casilla especificada
	private void procesarDisparo(int fila, int columna) {
		boolean[][] disparosActuales = (jugadorActual == 1) ? disparosJugador1 : disparosJugador2;

		// Verificar si ya disparó en esta casilla
		if (disparosActuales[fila][columna]) {
			JOptionPane.showMessageDialog(this, "Ya disparaste en esta casilla", "Disparo inválido",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		// Disparo normal
		ejecutarDisparoNormal(fila, columna);
		yaDisparo = true;
	}

	// Ejecuta un disparo en la posición especificada y devuelve true si es un
	// acierto
	protected boolean ejecutarDisparoEnPosicion(int fila, int columna) {
		if (fila < 0 || fila >= 10 || columna < 0 || columna >= 10) {
			return false;
		}

		boolean[][] disparosActuales;
		boolean[][] tableroOponente;
		boolean[][] impactosEnTableroOponente;

		if (jugadorActual == 1) {
			disparosActuales = disparosJugador1;
			tableroOponente = tableroJugador2;
			impactosEnTableroOponente = impactosEnTablero2;
		} else {
			disparosActuales = disparosJugador2;
			tableroOponente = tableroJugador1;
			impactosEnTableroOponente = impactosEnTablero1;
		}

		// Si ya se disparó aquí, no hacer nada
		if (disparosActuales[fila][columna]) {
			return false;
		}

		disparosActuales[fila][columna] = true;
		impactosEnTableroOponente[fila][columna] = true;
		int idx = jugadorActual - 1;

		if (tableroOponente[fila][columna]) {
			// ¡Acierto!
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
			// Agua
			celdasAtaque[fila][columna].setBackground(Color.WHITE);
			celdasAtaque[fila][columna].setText("•");
			celdasAtaque[fila][columna].setEnabled(false);
			fallos[idx]++;
			return false;
		}
	}

	// Ejecuta un disparo normal y muestra mensajes correspondientes
	private void ejecutarDisparoNormal(int fila, int columna) {
		boolean acierto = ejecutarDisparoEnPosicion(fila, columna);

		actualizarEstadisticas();

		// TODO Llamar a verificarVictoria() aquí

		if (acierto) {
			JOptionPane.showMessageDialog(this, "¡ACIERTO!  Has impactado un barco.", "¡Boom!",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(this, "Agua...Has fallado.", "Splash", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Cambia el turno al otro jugador
	private void cambiarJugador() {
		if (!yaDisparo) {
			int respuesta = JOptionPane.showConfirmDialog(this,
					"No has disparado este turno.¿Seguro que quieres pasar? ", "Confirmar", JOptionPane.YES_NO_OPTION);

			if (respuesta != JOptionPane.YES_OPTION) {
				return;
			}
		}

		// Incrementar contador de turnos
		turnosTotales++;

		// Mostrar pantalla de transición
		JOptionPane.showMessageDialog(this, "Pasa el dispositivo al Jugador " + (jugadorActual == 1 ? 2 : 1) + "\n\n"
				+ "Pulsa OK cuando esté listo.", "Cambio de Turno", JOptionPane.INFORMATION_MESSAGE);

		// Alternar entre jugador 1 y 2
		jugadorActual = (jugadorActual == 1) ? 2 : 1;

		// Resetear control de disparo
		yaDisparo = false;

		// Actualizar título
		setTitle("Hundir la Flota - Turno del Jugador " + jugadorActual);

		// Actualizar tableros y estadísticas
		actualizarTableroAtaque();
		actualizarTableroPropio();
		actualizarEstadisticas();
		actualizarBotonesDisparos();
	}

	// TODO Implementar activación del Super Disparo
	private void activarSuperDisparo() {
		JOptionPane.showMessageDialog(this, "Super Disparo - Pendiente de implementar por Compañero B", "TODO",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// TODO Implementar activación del Mega Disparo
	private void activarMegaDisparo() {
		JOptionPane.showMessageDialog(this, "Mega Disparo - Pendiente de implementar por Compañero B", "TODO",
				JOptionPane.INFORMATION_MESSAGE);
	}

	// Getters y setters protegidos para estadísticas y control de juego

	protected int getJugadorActual() {
		return jugadorActual;
	}

	protected int getTurnosTotales() {
		return turnosTotales;
	}

	protected int[] getSuperDisparos() {
		return superDisparos;
	}

	protected int[] getMegaDisparos() {
		return megaDisparos;
	}

	protected int[] getAciertos() {
		return aciertos;
	}

	protected int[] getFallos() {
		return fallos;
	}

	protected boolean isYaDisparo() {
		return yaDisparo;
	}

	protected void setYaDisparo(boolean valor) {
		yaDisparo = valor;
	}

	protected int getTotalCasillasBarcoJ1() {
		return totalCasillasBarcoJ1;
	}

	protected int getTotalCasillasBarcoJ2() {
		return totalCasillasBarcoJ2;
	}

	protected int getCasillasAcertadasPorJ1() {
		return casillasAcertadasPorJ1;
	}

	protected int getCasillasAcertadasPorJ2() {
		return casillasAcertadasPorJ2;
	}
}