package battleship;

public class Partida {

    private String fecha;
    private String ganador;
    private int turnos;
    private int barcosHundidos;

    public Partida(String fecha, String ganador, int turnos, int barcosHundidos) {
        this.fecha = fecha;
        this.ganador = ganador;
        this.turnos = turnos;
        this.barcosHundidos = barcosHundidos;
    }

    //Getters, obvio
    public String getFecha() {
        return fecha;
    }

    public String getGanador() {
        return ganador;
    }

    public int getTurnos() {
        return turnos;
    }

    public int getBarcosHundidos() {
        return barcosHundidos;
    }

    //Setters
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public void setTurnos(int turnos) {
        this.turnos = turnos;
    }

    public void setBarcosHundidos(int barcosHundidos) {
        this.barcosHundidos = barcosHundidos;
    }

    //ToString
    @Override
    public String toString() {
        return "Partida {" +
                "fecha='" + fecha + '\'' +
                ", ganador='" + ganador + '\'' +
                ", turnos=" + turnos +
                ", barcosHundidos=" + barcosHundidos +
                '}';
    }
   
}
