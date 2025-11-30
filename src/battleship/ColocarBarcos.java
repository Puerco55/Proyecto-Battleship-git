package battleship;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt. GridLayout;
import java. awt.event.ActionEvent;
import java.awt.event. ActionListener;
import java.util.Map;
import java.util.function.Consumer;
import java.util.HashMap;

import javax.swing. BorderFactory;
import javax.swing.Box;
import javax. swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax. swing.JFrame;
import javax.swing.JLabel;
import javax.swing. JOptionPane;
import javax.swing. JPanel;
import javax.swing.JRadioButton;

public class ColocarBarcos extends JFrame {

    private static final long serialVersionUID = 1L;

    // Componentes de la interfaz
    private JButton[][] celdas = new JButton[10][10];
    private JRadioButton orientacionHorizontal;
    private JRadioButton orientacionVertical;
    private JButton guardarButton;
    private JButton reiniciarButton;
    private int barcoSeleccionado = -1;
    private Map<Integer, Integer> barcosDisponibles;
    private Map<Integer, Integer> barcosOriginales;
    private boolean[][] tablero = new boolean[10][10];

    // Labels de disponibilidad
    private Map<Integer, JLabel> labelsDisponibilidad = new HashMap<>();

    public ColocarBarcos(int numeroJugador, Map<Integer, Integer> configBarcos, Consumer<boolean[][]> onGuardar) {

        // Clonamos el map para que cada jugador tenga el suyo
        this.barcosDisponibles = new HashMap<>(configBarcos);
        this. barcosOriginales = new HashMap<>(configBarcos);

        // Configuración de la ventana
        setTitle("Hundir la Flota - Jugador " + numeroJugador);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel superior
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory. createEmptyBorder(10, 10, 0, 10));
        JLabel titulo = new JLabel("Coloca tus barcos, Jugador " + numeroJugador);
        titulo.setFont(titulo.getFont().deriveFont(16f));
        panelSuperior. add(titulo);
        add(panelSuperior, BorderLayout.NORTH);

        // Panel cuadricula
        JPanel panelCuadricula = new JPanel(new GridLayout(10, 10));
        panelCuadricula. setBorder(BorderFactory.createLineBorder(Color. BLACK, 2));
        for (int fila = 0; fila < 10; fila++) {
            for (int col = 0; col < 10; col++) {
                JButton celda = new JButton();
                celda. setBackground(new Color(0, 150, 200));
                celda.setFocusPainted(false);
                celdas[fila][col] = celda;

                int f = fila, c = col;
                celda.addActionListener(e -> colocarBarco(f, c));

                panelCuadricula.add(celda);
            }
        }
        add(panelCuadricula, BorderLayout.CENTER);

        // Panel derecho
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout. Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));

        panelDerecho.add(new JLabel("1.  Elige un barco:"));
        panelDerecho.add(Box.createVerticalStrut(5));
        
        ButtonGroup grupoBarcos = new ButtonGroup();

        // Crear botones de barcos + labels de disponibilidad
        configBarcos.forEach((tamano, cantidad) -> {
            String nombreBarco = "Barco de " + tamano + " casillas";
            JRadioButton radioBarco = new JRadioButton(nombreBarco);
            grupoBarcos.add(radioBarco);
            panelDerecho. add(radioBarco);

            radioBarco.addActionListener(e -> barcoSeleccionado = tamano);

            JLabel cantidadLabel = new JLabel("   Disponibles: " + cantidad);
            cantidadLabel.setForeground(cantidad > 0 ?  new Color(0, 128, 0) : Color.RED);
            panelDerecho.add(cantidadLabel);
            panelDerecho. add(Box.createVerticalStrut(3));

            // Guardamos el label asociado al tamaño
            labelsDisponibilidad.put(tamano, cantidadLabel);
        });

        panelDerecho. add(Box.createVerticalStrut(15));

        // Orientación
        panelDerecho.add(new JLabel("2. Elige la orientación:"));
        panelDerecho.add(Box.createVerticalStrut(5));
        
        ButtonGroup grupoOrientacion = new ButtonGroup();
        orientacionHorizontal = new JRadioButton("Horizontal", true);
        orientacionVertical = new JRadioButton("Vertical");
        grupoOrientacion.add(orientacionHorizontal);
        grupoOrientacion.add(orientacionVertical);
        panelDerecho. add(orientacionHorizontal);
        panelDerecho.add(orientacionVertical);

        panelDerecho. add(Box.createVerticalStrut(15));
        
        panelDerecho. add(new JLabel("3.  Haz clic en el tablero"));

        panelDerecho. add(Box.createVerticalGlue());

        // Botón reiniciar
        reiniciarButton = new JButton("Reiniciar Tablero");
        reiniciarButton.setAlignmentX(LEFT_ALIGNMENT);
        reiniciarButton.addActionListener(e -> reiniciarTablero());
        panelDerecho.add(reiniciarButton);
        
        panelDerecho. add(Box.createVerticalStrut(10));

        // Botón guardar
        guardarButton = new JButton("GUARDAR Y CONTINUAR");
        guardarButton. setAlignmentX(LEFT_ALIGNMENT);
        guardarButton.setBackground(new Color(100, 180, 100));
        panelDerecho. add(guardarButton);

        add(panelDerecho, BorderLayout. EAST);

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Verificar que se ha colocado al menos un barco
                boolean hayBarcos = false;
                for (int i = 0; i < 10 && !hayBarcos; i++) {
                    for (int j = 0; j < 10 && !hayBarcos; j++) {
                        if (tablero[i][j]) hayBarcos = true;
                    }
                }
                
                if (! hayBarcos) {
                    JOptionPane.showMessageDialog(ColocarBarcos. this,
                        "Debes colocar al menos un barco",
                        "Sin barcos",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                System.out.println("Guardando configuración del Jugador " + numeroJugador);

                dispose();

                if (onGuardar != null) {
                    // Pasar el tablero al callback
                    onGuardar.accept(clonarTablero(tablero));
                }
            }
        });

        setVisible(true);
    }
    
    /**
     * Reinicia el tablero a su estado inicial
     */
    private void reiniciarTablero() {
        // Reiniciar matriz
        tablero = new boolean[10][10];
        
        // Reiniciar celdas visuales
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                celdas[i][j].setBackground(new Color(0, 150, 200));
            }
        }
        
        // Reiniciar barcos disponibles
        barcosDisponibles = new HashMap<>(barcosOriginales);
        
        // Actualizar labels
        barcosOriginales.forEach((tamano, cantidad) -> {
            JLabel label = labelsDisponibilidad.get(tamano);
            label.setText("   Disponibles: " + cantidad);
            label.setForeground(cantidad > 0 ? new Color(0, 128, 0) : Color.RED);
        });
        
        barcoSeleccionado = -1;
    }
    
    /**
     * Crea una copia del tablero para evitar referencias compartidas
     */
    private boolean[][] clonarTablero(boolean[][] original) {
        boolean[][] copia = new boolean[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                copia[i][j] = original[i][j];
            }
        }
        return copia;
    }
    
    // Colocacion de Barcos
    private void colocarBarco(int fila, int col) {

        if (barcoSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, 
                "Selecciona un barco primero", 
                "Sin selección", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (barcosDisponibles.get(barcoSeleccionado) == null || 
            barcosDisponibles. get(barcoSeleccionado) == 0) {
            JOptionPane.showMessageDialog(this, 
                "No quedan barcos de tamaño " + barcoSeleccionado, 
                "Sin barcos", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean horizontal = orientacionHorizontal.isSelected();

        // Verificar que cabe
        if (horizontal) {
            if (col + barcoSeleccionado > 10) {
                JOptionPane.showMessageDialog(this, 
                    "El barco no cabe horizontalmente en esa posición", 
                    "No cabe", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (fila + barcoSeleccionado > 10) {
                JOptionPane.showMessageDialog(this, 
                    "El barco no cabe verticalmente en esa posición", 
                    "No cabe", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Para que los barcos no se solapen
        for (int i = 0; i < barcoSeleccionado; i++) {
            int f = fila + (horizontal ? 0 : i);
            int c = col + (horizontal ? i : 0);
            if (tablero[f][c]) {
                JOptionPane.showMessageDialog(this, 
                    "Ya hay un barco en esa posición", 
                    "Solapamiento", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Colocar barco
        for (int i = 0; i < barcoSeleccionado; i++) {
            int f = fila + (horizontal ?  0 : i);
            int c = col + (horizontal ?  i : 0);

            tablero[f][c] = true;
            celdas[f][c].setBackground(Color. GRAY);
        }

        // Reducir disponibilidad
        int nuevaCantidad = barcosDisponibles. get(barcoSeleccionado) - 1;
        barcosDisponibles.put(barcoSeleccionado, nuevaCantidad);

        // Actualizar disponibilidad
        JLabel label = labelsDisponibilidad.get(barcoSeleccionado);
        label.setText("   Disponibles: " + nuevaCantidad);
        label.setForeground(nuevaCantidad > 0 ? new Color(0, 128, 0) : Color.RED);

        System.out.println("Barco de tamaño " + barcoSeleccionado + " colocado.");
    }
}