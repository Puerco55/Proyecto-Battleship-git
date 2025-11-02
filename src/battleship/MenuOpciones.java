package battleship;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MenuOpciones extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MenuOpciones() {
        setTitle("Opciones");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(320, 320); 
        setLocationRelativeTo(null); 
        setResizable(false);
        
        //Panel princ
        JPanel mainPanel = new JPanel();

        //Panel botones
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel);
        
        //Para determinar cantidad d elementos q se muestran pa q el scroll funce
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        //Generador d botones d muestra sobrepasando el limite pa comprobar scroll
        for (int i = 0; i < 20; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel("lorem"), gbc);

            gbc.gridx = 1;
            JButton boton = new JButton("ipsum");
            boton.setPreferredSize(new Dimension(100, 30));
            panel.add(boton, gbc);
        }
        
        JScrollPane scrolPrinc = new JScrollPane(mainPanel);
        add(scrolPrinc);

	}

}
