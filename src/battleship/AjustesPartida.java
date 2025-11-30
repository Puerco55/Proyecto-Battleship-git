package battleship;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class AjustesPartida extends JFrame {

    private static final long serialVersionUID = 1L;

    // Componentes de la interfaz
    private JSpinner[] spinners;
    private JButton guardarButton;
    private String[] nombresBarcos = { "Barco de 5", "Barco de 4", "Barco de 3", "Barco de 2", "Super Disparo", "Mega Disparo"};
    private int[] tamanosBarcos = { 5, 4, 3, 2 };

    public AjustesPartida() {
        // Configuracion de la ventana
        setTitle("Ajustes de la Partida");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        // Título
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titulo = new JLabel("Configura la Partida");
        titulo.setFont(titulo.getFont().deriveFont(18f));
        add(titulo, gbc);
        
        gbc.gridwidth = 1;

        spinners = new JSpinner[nombresBarcos.length];

        // Crear etiquetas y spinners para cada tipo de barco
        for (int i = 0; i < nombresBarcos.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i + 1;
            gbc.anchor = GridBagConstraints.WEST;
            add(new JLabel(nombresBarcos[i] + ":"), gbc);

            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            
            if (i == 0) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 1, 1));
            } else if (i == 1) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
            } else if (i == 2) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(2, 0, 3, 1));
            } else if (i == 3) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 4, 1));
            } else if (i == 4) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 3, 1));
            } else if (i == 5) {
                spinners[i] = new JSpinner(new SpinnerNumberModel(1, 0, 2, 1));
            }
            
            spinners[i].setPreferredSize(new Dimension(60, 28));
            add(spinners[i], gbc);
        }

        // Boton para guardar la configuracion
        guardarButton = new JButton("Comenzar Partida");
        guardarButton.setPreferredSize(new Dimension(180, 35));
        gbc.gridx = 0;
        gbc.gridy = nombresBarcos.length + 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 10, 10, 10);
        add(guardarButton, gbc);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);

        guardarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Recoge la configuracion de los spinners
                Map<Integer, Integer> configBarcos = new HashMap<>();
                int totalCasillasBarco = 0;
                
                for (int i = 0; i < tamanosBarcos.length; i++) {
                    int cantidad = (int) spinners[i].getValue();
                    if (cantidad > 0) {
                        configBarcos.put(tamanosBarcos[i], cantidad);
                        totalCasillasBarco += tamanosBarcos[i] * cantidad;
                    }
                }
                
                // Verificar que hay al menos un barco
                if (configBarcos.isEmpty()) {
                    JOptionPane.showMessageDialog(AjustesPartida.this,
                        "Debes seleccionar al menos un barco",
                        "Sin barcos",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Verificar que no hay demasiadas casillas ocupadas
                if (totalCasillasBarco > 50) {
                    JOptionPane.showMessageDialog(AjustesPartida.this,
                        "Demasiados barcos.Máximo 50 casillas ocupadas.\nActual: " + totalCasillasBarco,
                        "Demasiados barcos",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Recoger los valores de disparos especiales
                final int superDisparos = (int) spinners[4].getValue();
                final int megaDisparos = (int) spinners[5].getValue();

                dispose();

                // Arrays para almacenar los tableros de ambos jugadores
                final boolean[][][] tableros = new boolean[2][10][10];

                // Secuencia de configuracion de jugadores
                Runnable iniciarPartida = () -> {
                    System.out.println("Ambos jugadores han colocado sus barcos.");
                    PantallaInicioJuego inicioJuego = new PantallaInicioJuego(
                        superDisparos, 
                        megaDisparos, 
                        tableros[0],
                        tableros[1]
                    );
                    inicioJuego.setVisible(true);
                };

                // Jugador 2 coloca sus barcos
                java.util.function.Consumer<boolean[][]> configurarJugador2 = (tableroJ1) -> {
                    tableros[0] = tableroJ1;
                    
                    // Pantalla de transición
                    JOptionPane.showMessageDialog(null,
                        "Jugador 1 ha terminado.\n\nPasa el dispositivo al Jugador 2.\nPulsa OK cuando esté listo.",
                        "Cambio de Jugador",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    new ColocarBarcos(2, configBarcos, (tableroJ2) -> {
                        tableros[1] = tableroJ2;
                        iniciarPartida.run();
                    });
                };

                // Jugador 1 coloca sus barcos
                new ColocarBarcos(1, configBarcos, configurarJugador2);
            }
        });
    }
}