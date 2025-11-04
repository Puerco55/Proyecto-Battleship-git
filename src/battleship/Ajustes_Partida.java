package battleship;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

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
	
	// Variables para guardar los valores seleccionados
	public static Integer barcos1;
	public static Integer barcos2;
	public static Integer barcos3;

	
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

        // Crear los spinners y bloquear escritura manual
        JSpinner spinnerBarcos1 = new JSpinner(barcos1);
        ((JSpinner.DefaultEditor) spinnerBarcos1.getEditor()).getTextField().setEditable(false);

        JSpinner spinnerBarcos2 = new JSpinner(barcos2);
        ((JSpinner.DefaultEditor) spinnerBarcos2.getEditor()).getTextField().setEditable(false);

        JSpinner spinnerBarcos3 = new JSpinner(barcos3);
        ((JSpinner.DefaultEditor) spinnerBarcos3.getEditor()).getTextField().setEditable(false);

        JSpinner spinnerSuper = new JSpinner(superDisparo);
        ((JSpinner.DefaultEditor) spinnerSuper.getEditor()).getTextField().setEditable(false);

        JSpinner spinnerMega = new JSpinner(megaDisparo);
        ((JSpinner.DefaultEditor) spinnerMega.getEditor()).getTextField().setEditable(false);

        // Añadimos los selectores y nombres s
        panelOpciones.add(new JLabel("Barcos de 1 de largo:"));
        panelOpciones.add(spinnerBarcos1);

        panelOpciones.add(new JLabel("Barcos de 2 de largo:"));
        panelOpciones.add(spinnerBarcos2);

        panelOpciones.add(new JLabel("Barcos de 3 de largo:"));
        panelOpciones.add(spinnerBarcos3);

        panelOpciones.add(new JLabel("Super Disparo (+) :"));
        panelOpciones.add(spinnerSuper);

        panelOpciones.add(new JLabel("Mega Disparo (3x3):"));
        panelOpciones.add(spinnerMega);
        
        // Centramos
        add(panelOpciones, BorderLayout.CENTER);
        
        // Creamos boton para continuar
        JButton botonCotinuar = new JButton("Continuar");
        JButton botonAtras = new JButton("Atras");
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBoton.add(botonAtras);
        panelBoton.add(botonCotinuar);
        add(panelBoton, BorderLayout.SOUTH);
        Map<Integer, Integer> configBarcos = new HashMap<>();
        configBarcos.put(1, (int) spinnerBarcos1.getValue());
        configBarcos.put(2, (int) spinnerBarcos2.getValue());
        configBarcos.put(3, (int) spinnerBarcos3.getValue());
        Jugador1_Barcos jugador1barcos = new Jugador1_Barcos(configBarcos);
        jugador1barcos.setVisible(true);
        dispose(); 
        
        // Creamos el boton para ir atras
        botonAtras.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Atras");
                MainMenu mainMenu = new MainMenu();
                mainMenu.setVisible(true);
                dispose(); 
            }
        });
        // Guardamos todo los valores puestos
        botonCotinuar.addActionListener(e -> {
        	
        	// Obtenemos valores al clickar en el boton de continuar
        	
            int barco1 = (int) barcos1.getValue();
            Ajustes_Partida.barcos1 = (Integer) spinnerBarcos1.getValue(); // Asignar el valor seleccionado a la variable estática barcos1
            int barco2 = (int) barcos2.getValue();
            Ajustes_Partida.barcos2 = (Integer) spinnerBarcos2.getValue(); // Asignar el valor seleccionado a la variable estática barcos2
            int barco3 =(int) barcos3.getValue();
            Ajustes_Partida.barcos3 = (Integer) spinnerBarcos3.getValue(); // Asignar el valor seleccionado a la variable estática barcos3
            int superdisparo = (int) superDisparo.getValue();
            int megadisparo = (int) megaDisparo.getValue();

            System.out.println("Configuración:");
            System.out.println("Barcos 1: " + barco1 + ", Barcos 2: " + barco2 + ", Barcos 3: " + barco3);
            System.out.println("SuperDisparo: " + superdisparo + ", MegaDisparo: " + megadisparo);
           
            dispose();
            
        });
    
    }
	
}