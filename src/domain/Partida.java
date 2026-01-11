package domain;

public class Partida {

    private int id; 
    private String fecha;
    private String ganador;
    private int turnos;
    private int barcosHundidos;

    // Constructor actualizado
    public Partida(int id, String fecha, String ganador, int turnos, int barcosHundidos) {
        this.id = id;
        this.fecha = fecha;
        this.ganador = ganador;
        this.turnos = turnos;
        this.barcosHundidos = barcosHundidos;
    }

    public int getId() { return id; } // Getter nuevo

    public String getFecha() { return fecha; }
    public String getGanador() { return ganador; }
    public int getTurnos() { return turnos; }
    public int getBarcosHundidos() { return barcosHundidos; }

    @Override
    public String toString() {
        return "Partida [id=" + id + ", fecha=" + fecha + ", ganador=" + ganador + "]";
    }
}