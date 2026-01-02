package domain;

// Clase que detecta hundimientos de barcos usando recursividad
public class DetectorHundimiento {

    private boolean[][] tableroBarcos;
    private boolean[][] tableroDaños;
    private boolean[][] visitados;

    // Constructor
    public DetectorHundimiento(boolean[][] tableroBarcos, boolean[][] tableroDaños) {
        this.tableroBarcos = tableroBarcos;
        this.tableroDaños = tableroDaños;
        this.visitados = new boolean[10][10];
    }

    // Verifica si el barco en la posición está hundido
    public boolean estaBarcoHundido(int fila, int col) {
        // Reiniciar visitados
        visitados = new boolean[10][10];
        
        // Llamar al método recursivo
        return comprobarHundimientoRecursivo(fila, col);
    }

    // Método recursivo que explora casillas conectadas del barco
    private boolean comprobarHundimientoRecursivo(int fila, int col) {
        // Caso base: fuera de límites
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) {
            return true;
        }

        // Caso base: ya visitado
        if (visitados[fila][col]) {
            return true;
        }

        // Marcar como visitada
        visitados[fila][col] = true;

        // Caso base: no hay barco
        if (!tableroBarcos[fila][col]) {
            return true;
        }

        // Caso base: hay barco pero no golpeado
        if (!tableroDaños[fila][col]) {
            return false;
        }

        // Caso recursivo: explorar las 4 direcciones
        boolean arribaHundido = comprobarHundimientoRecursivo(fila - 1, col);     // Arriba
        boolean abajoHundido = comprobarHundimientoRecursivo(fila + 1, col);      // Abajo
        boolean izquierdaHundido = comprobarHundimientoRecursivo(fila, col - 1);  // Izquierda
        boolean derechaHundido = comprobarHundimientoRecursivo(fila, col + 1);    // Derecha

        // Solo hundido si todas las direcciones confirman
        return arribaHundido && abajoHundido && izquierdaHundido && derechaHundido;
    }

    // Cuenta el tamaño del barco
    public int contarTamañoBarco(int fila, int col) {
        visitados = new boolean[10][10];
        return contarRecursivo(fila, col);
    }

    // Método recursivo para contar casillas del barco
    private int contarRecursivo(int fila, int col) {
        // Caso base: fuera de límites
        if (fila < 0 || fila >= 10 || col < 0 || col >= 10) {
            return 0;
        }

        // Caso base: ya visitado
        if (visitados[fila][col]) {
            return 0;
        }

        // Caso base: no hay barco
        if (!tableroBarcos[fila][col]) {
            return 0;
        }

        // Marcar visitado
        visitados[fila][col] = true;

        // Caso recursivo: contar esta casilla + adyacentes
        return 1 
            + contarRecursivo(fila - 1, col)   // Arriba
            + contarRecursivo(fila + 1, col)   // Abajo
            + contarRecursivo(fila, col - 1)   // Izquierda
            + contarRecursivo(fila, col + 1);  // Derecha
    }
}