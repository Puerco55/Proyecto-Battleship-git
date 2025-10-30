package battleship;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;




public class Ajustes_Partida extends JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public Ajustes_Partida() {
        setTitle("Ajustes de Partida");
        setSize(420, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Modificar y centrar el tituloo
        
        JLabel titulo = new JLabel("AJUSTES DE PARTIDA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 22));
        add(titulo, BorderLayout.NORTH);
        
        
        JPanel panelOpciones = new JPanel(new GridLayout(6, 2, 10, 10));
        panelOpciones.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        
        // Creamos el selector de los barcos y disparos
        
        SpinnerNumberModel barcos1 = new SpinnerNumberModel(1, 1, 3, 1);
        SpinnerNumberModel barcos2 = new SpinnerNumberModel(1, 1, 3, 1);
        SpinnerNumberModel barcos3 = new SpinnerNumberModel(1, 1, 3, 1);
        SpinnerNumberModel superDisparo = new SpinnerNumberModel(0, 0, 1, 1);
        SpinnerNumberModel megaDisparo = new SpinnerNumberModel(0, 0, 1, 1);
        
        // Añadimos los selectores y nombres
        
        panelOpciones.add(new JLabel("Barcos de 1 de largo:"));
        panelOpciones.add(new JSpinner(barcos1));
        panelOpciones.add(new JLabel("Barcos de 2 de largo:"));
        panelOpciones.add(new JSpinner(barcos2));
        panelOpciones.add(new JLabel("Barcos de 3 de largo:"));
        panelOpciones.add(new JSpinner(barcos3));
        panelOpciones.add(new JLabel("Super Disparo (+):"));
        panelOpciones.add(new JSpinner(superDisparo));
        panelOpciones.add(new JLabel("Mega Disparo (3x3):"));
        panelOpciones.add(new JSpinner(megaDisparo));
        
        // Centramos
        
        add(panelOpciones, BorderLayout.CENTER);
        // Creamos boton para continuar
        
        JButton botonCotinuar = new JButton("Continuar");
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(botonCotinuar);
        add(panelBoton, BorderLayout.SOUTH);
        // Guardamos 
        botonCotinuar.addActionListener(e -> {
        	
        	// Obtenemos valores al clickar en el boton de continuar
        	
            int barco1 = (int) barcos1.getValue();
            int barco2 = (int) barcos2.getValue();
            int barco3 =(int) barcos3.getValue();
            int superdisparo = (int) superDisparo.getValue();
            int megadisparo = (int) megaDisparo.getValue();

            System.out.println("Configuración:");
            System.out.println("Barcos 1: " + barco1 + ", Barcos 2: " + barco2 + ", Barcos 3: " + barco3);
            System.out.println("SuperDisparo: " + superdisparo + ", MegaDisparo: " + megadisparo);
           
            dispose();
        });
    }
}