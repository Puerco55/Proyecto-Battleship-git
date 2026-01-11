package domain;

public class Jugador {
	private int id; // 1 o 2
	private int[][] tableroPropio; // AHORA ES INT[][] PARA IDs
	private boolean[][] tableroDisparos; // Registro de dónde ha disparado
	private boolean[][] impactosRecibidos; // Dónde le han dado

	// Estadísticas
	private int aciertos = 0;
	private int fallos = 0;
	private int barcosHundidos = 0;

	// Inventario
	private int superDisparos;
	private int megaDisparos;

	// Matriz para rastrear casillas de barcos hundidos
	private boolean[][] casillasHundidas = new boolean[10][10];

	private int escudos;
	private boolean escudoActivo = false;

	// Total casillas de barcos vivos
	private int casillasTotales;
	private int casillasGolpeadasPropias = 0;

	// Nombre del equipo (Skin)
	private String nombreEquipo;

	public Jugador(int id, int[][] tableroInicial, int superDisparos, int megaDisparos, int escudos,
			String nombreEquipo) {
		this.id = id;
		this.tableroPropio = tableroInicial;
		this.superDisparos = superDisparos;
		this.megaDisparos = megaDisparos;
		this.escudos = escudos;
		this.nombreEquipo = nombreEquipo;
		this.tableroDisparos = new boolean[10][10];
		this.impactosRecibidos = new boolean[10][10];
		this.escudoActivo = false;
		calcularCasillasTotales();
	}

	public String getNombreEquipo() {
		return nombreEquipo;
	}

	private void calcularCasillasTotales() {
		casillasTotales = 0;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (tableroPropio[i][j] > 0)
					casillasTotales++; // > 0 significa que hay barco
			}
		}
	}

	// Lógica: ¿Este jugador ha perdido?
	public boolean haPerdido() {
		return casillasGolpeadasPropias >= casillasTotales;
	}

	// Getters y Setters necesarios
	public int getId() {
		return id;
	}

	public int[][] getTableroPropio() {
		return tableroPropio;
	}

	public boolean[][] getTableroDisparos() {
		return tableroDisparos;
	}

	public boolean[][] getImpactosRecibidos() {
		return impactosRecibidos;
	}

	public int getAciertos() {
		return aciertos;
	}

	public void incrementarAciertos() {
		this.aciertos++;
	}

	public int getFallos() {
		return fallos;
	}

	public void incrementarFallos() {
		this.fallos++;
	}

	public int getBarcosHundidos() {
		return barcosHundidos;
	}

	public void incrementarBarcosHundidos() {
		this.barcosHundidos++;
	}

	public int getSuperDisparos() {
		return superDisparos;
	}

	public void usarSuperDisparo() {
		this.superDisparos--;
	}

	public int getMegaDisparos() {
		return megaDisparos;
	}

	public void usarMegaDisparo() {
		this.megaDisparos--;
	}

	public int getEscudos() {
		return escudos;
	}

	public boolean tieneEscudoActivo() {
		return escudoActivo;
	}

	public void usarEscudo() {
		if (escudos > 0) {
			escudos--;
			escudoActivo = true;
		}
	}

	public void resetEscudoTurno() {
		escudoActivo = false;
	}

	public void recibirImpacto() {
		this.casillasGolpeadasPropias++;
	}

	// Getter para casillas hundidas
	public boolean[][] getCasillasHundidas() {
		return casillasHundidas;
	}

	// Marcar todas las casillas de un barco como hundidas usando recursividad e ID
	public void marcarBarcoHundido(int fila, int col, int[][] tableroBarcos) {
		int idBarco = tableroBarcos[fila][col];
		if (idBarco == 0)
			return; // No es barco
		marcarRecursivo(fila, col, idBarco, tableroBarcos, new boolean[10][10]);
	}

	// Método recursivo auxiliar que SOLO propaga si el ID coincide
	private void marcarRecursivo(int f, int c, int idObjetivo, int[][] barcos, boolean[][] visitado) {
		if (f < 0 || f >= 10 || c < 0 || c >= 10)
			return;
		if (visitado[f][c])
			return;

		// Si no es el mismo barco (diferente ID), paramos
		if (barcos[f][c] != idObjetivo)
			return;

		visitado[f][c] = true;
		casillasHundidas[f][c] = true;

		marcarRecursivo(f - 1, c, idObjetivo, barcos, visitado);
		marcarRecursivo(f + 1, c, idObjetivo, barcos, visitado);
		marcarRecursivo(f, c - 1, idObjetivo, barcos, visitado);
		marcarRecursivo(f, c + 1, idObjetivo, barcos, visitado);
	}
}