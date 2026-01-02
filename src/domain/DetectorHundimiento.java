package domain;

// Clase que detecta hundimientos de barcos usando recursividad con IDs
public class DetectorHundimiento {

    private int[][] tableroBarcos; // AHORA IDs de barcos
    private boolean[][] tableroDaños;
    private boolean[][] visitados;

    // Constructor
    public DetectorHundimiento(int[][] tableroBarcos, boolean[][] tableroDaños) {
        this.tableroBarcos = tableroBarcos;
        this.tableroDaños = tableroDaños;
        this.visitados = new boolean[10][10];
    }

    // Verifica si el barco en la posición está hundido
    public boolean estaBarcoHundido(int fila, int col) {
        int idBarco = tableroBarcos[fila][col];
        if (idBarco == 0) return true; // Si es agua, se considera "no vivo" (o true por defecto)

        visitados = new boolean[10][10];
        
        // Llamar al método recursivo filtrando por ID
        return comprobarHundimientoRecursivo(fila, col, idBarco);
    }

    // Método recursivo que explora casillas conectadas del MISMO barco
    private boolean comprobarHundimientoRecursivo(int fila, int col, int idObjetivo) {
        // Caso base: fuera de límites
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) {
            return true;
        }

        // Caso base: ya visitado
        if (visitados[fila][col]) {
            return true;
        }

        // Caso base: NO es el mismo barco (otro ID o agua)
        if (tableroBarcos[fila][col] != idObjetivo) {
            return true; // No afecta al hundimiento del barco objetivo
        }

        // Marcar como visitada
        visitados[fila][col] = true;

        // Caso base: es parte del barco pero NO golpeada
        if (!tableroDaños[fila][col]) {
            return false; // Parte del barco encontrada e intacta -> NO hundido
        }

        // Caso recursivo: es parte del barco y está golpeada -> seguir explorando
        boolean arribaHundido = comprobarHundimientoRecursivo(fila - 1, col, idObjetivo);
        boolean abajoHundido = comprobarHundimientoRecursivo(fila + 1, col, idObjetivo);
        boolean izquierdaHundido = comprobarHundimientoRecursivo(fila, col - 1, idObjetivo);
        boolean derechaHundido = comprobarHundimientoRecursivo(fila, col + 1, idObjetivo);

        return arribaHundido && abajoHundido && izquierdaHundido && derechaHundido;
    }

    // Cuenta el tamaño del barco
    public int contarTamañoBarco(int fila, int col) {
        int idBarco = tableroBarcos[fila][col];
        if (idBarco == 0) return 0;
        
        visitados = new boolean[10][10];
        return contarRecursivo(fila, col, idBarco);
    }

    // Método recursivo para contar casillas del barco
    private int contarRecursivo(int fila, int col, int idObjetivo) {
        // Caso base: fuera de límites
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) {
            return 0;
        }

        // Caso base: ya visitado
        if (visitados[fila][col]) {
            return 0;
        }

        // Caso base: no es el mismo barco
        if (tableroBarcos[fila][col] != idObjetivo) {
            return 0;
        }

        // Marcar visitado
        visitados[fila][col] = true;

        // Caso recursivo: contar esta casilla + adyacentes
        return 1 
            + contarRecursivo(fila - 1, col, idObjetivo)
            + contarRecursivo(fila + 1, col, idObjetivo)
            + contarRecursivo(fila, col - 1, idObjetivo)
            + contarRecursivo(fila, col + 1, idObjetivo);
    }
}