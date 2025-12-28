package domain;

public class Jugador {
    private int id; // 1 o 2
    private boolean[][] tableroPropio;
    private boolean[][] tableroDisparos; // Registro de dónde ha disparado
    private boolean[][] impactosRecibidos; // Dónde le han dado
    
    // Estadísticas
    private int aciertos = 0;
    private int fallos = 0;
    private int barcosHundidos = 0;
    
    // Inventario
    private int superDisparos;
    private int megaDisparos;
    
    private int escudos;
    private boolean escudoActivo = false;  
    
    // Total casillas de barcos vivos
    private int casillasTotales;
    private int casillasGolpeadasPropias = 0;

    public Jugador(int id, boolean[][] tableroInicial, int superDisparos, int megaDisparos, int escudos) {
        this.id = id;
        this.tableroPropio = tableroInicial;
        this.superDisparos = superDisparos;
        this.megaDisparos = megaDisparos;
        this.escudos = escudos;
        this.tableroDisparos = new boolean[10][10];
        this.impactosRecibidos = new boolean[10][10];
        this.escudoActivo = false;
        calcularCasillasTotales();
    }

    private void calcularCasillasTotales() {
        casillasTotales = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (tableroPropio[i][j]) casillasTotales++;
            }
        }
    }

    // Lógica: ¿Este jugador ha perdido?
    public boolean haPerdido() {
        return casillasGolpeadasPropias >= casillasTotales;
    }

    // Getters y Setters necesarios
    public int getId() { return id; }
    public boolean[][] getTableroPropio() { return tableroPropio; }
    public boolean[][] getTableroDisparos() { return tableroDisparos; }
    public boolean[][] getImpactosRecibidos() { return impactosRecibidos; }
    
    public int getAciertos() { return aciertos; }
    public void incrementarAciertos() { this.aciertos++; }
    
    public int getFallos() { return fallos; }
    public void incrementarFallos() { this.fallos++; }
    
    public int getBarcosHundidos() { return barcosHundidos; } // Nota: La lógica real de barcos hundidos requiere objetos Barco, por ahora lo mantenemos simple.
    
    public int getSuperDisparos() { return superDisparos; }
    public void usarSuperDisparo() { this.superDisparos--; }
    
    public int getMegaDisparos() { return megaDisparos; }
    public void usarMegaDisparo() { this.megaDisparos--; }
    
    public int getEscudos() { return escudos; }
    public boolean tieneEscudoActivo() { return escudoActivo; }

    public void usarEscudo() {
        if (escudos > 0) {
            escudos--;
            escudoActivo = true;
        }
    }
    
    public void resetEscudoTurno() {
        escudoActivo = false;
    }
    
    public void recibirImpacto() { this.casillasGolpeadasPropias++; }
}