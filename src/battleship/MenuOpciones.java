package battleship;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
        
        // --- PANEL PRINCIPAL ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        setContentPane(mainPanel);

        // --- PANEL DE BOTONES ---
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(panel);
        
        JLabel calcio = new JLabel("algo");
        JButton sodio = new JButton("algo2");
        
        panel.add(calcio);
        panel.add(sodio);

	}

}
