package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java. awt.GridLayout;
import java. awt.event.ActionEvent;
import java.awt.event. ActionListener;
import java.awt.event.WindowAdapter;
import java. awt.event.WindowEvent;

import javax.swing. JButton;
import javax. swing.JFrame;
import javax.swing.JLabel;
import javax.swing. JOptionPane;
import javax.swing. JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import db.GestorBaseDatos;
import main.AjustesPartida;

public class MainMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    
    public MainMenu() {

        setTitle("Hundir la Flota 🚢");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(420, 420); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        // Inicializar base de datos
        GestorBaseDatos. inicializarTablas();
        
        // Cerrar ventana
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel. setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Título del juego
        JLabel titleLabel = new JLabel("Hundir la Flota", SwingConstants.CENTER);
        titleLabel. setFont(new Font("Arial", Font. BOLD, 36));
        mainPanel.add(titleLabel, BorderLayout. NORTH);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        buttonPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // Creación de botones
        JButton playButton = new JButton("Jugar");
        JButton optionsButton = new JButton("Opciones");
        JButton statsButton = new JButton("Estadísticas");
        JButton exitButton = new JButton("Salir");
        
        playButton.setFont(new Font("Arial", Font.PLAIN, 14));
        optionsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        statsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        
        buttonPanel.add(playButton);
        buttonPanel.add(optionsButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(exitButton);

        // 1. Botón Jugar
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Iniciar Juego");
                AjustesPartida ajustesPartida = new AjustesPartida();
                ajustesPartida.setVisible(true);
                dispose(); 
            }
        });

        // 2. Botón Opciones
        optionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MenuOpciones().setVisible(true);
            }
        });
        
        // 3. Botón Estadísticas
        statsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MenuEstadisticas ventanaStats = new MenuEstadisticas();
                ventanaStats.setVisible(true);
            }
        });

        // 4.  Botón Salir
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarSalida();
            }
        });
    }
    
    private void confirmarSalida() {
        int respuesta = JOptionPane.showConfirmDialog(this,
            "¿Seguro que quieres salir? ",
            "Confirmar salida",
            JOptionPane.YES_NO_OPTION);
        
        if (respuesta == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
}