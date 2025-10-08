package battleship;

import javax.swing.JFrame;

public class Menu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public Menu() {
		setTitle("Battleship");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(420,420);
		
		
		
		setVisible(true);
	}

	public static void main(String[] args) {
		new Menu();

	}

}
