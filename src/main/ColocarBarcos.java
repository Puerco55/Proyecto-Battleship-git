package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ColocarBarcos extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable tabla;
	private ModeloTablero modeloDatos;
	private JRadioButton orientacionHorizontal;
	private JRadioButton orientacionVertical;

	private Map<Integer, Integer> barcosDisponibles;
	private Map<Integer, Integer> barcosOriginales;
	private Map<Integer, JLabel> labelsDisponibilidad = new HashMap<>();

	private BufferedImage imagenBarcoOriginal;
	private String nombreEquipo;

	public ColocarBarcos(int numeroJugador, Map<Integer, Integer> configBarcos, String nombreEquipo,
			Consumer<int[][]> onGuardar) {

		this.nombreEquipo = nombreEquipo;
		this.barcosDisponibles = new HashMap<>(configBarcos);
		this.barcosOriginales = new HashMap<>(configBarcos);

		cargarImagenBarco();

		setTitle("Despliegue - Jugador " + numeroJugador + " (" + nombreEquipo + ")");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1000, 700);
		setLocationRelativeTo(null);
		setResizable(false);

		GradientPanel mainPanel = new GradientPanel();
		mainPanel.setLayout(new BorderLayout());
		setContentPane(mainPanel);

		// Título
		JPanel panelSuperior = new JPanel();
		panelSuperior.setOpaque(false);
		panelSuperior.setBorder(new EmptyBorder(20, 0, 10, 0));
		JLabel titulo = new JLabel("DESPLIEGUE: JUGADOR " + numeroJugador, SwingConstants.CENTER);
		titulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
		titulo.setForeground(Color.WHITE);
		panelSuperior.add(titulo);
		mainPanel.add(panelSuperior, BorderLayout.NORTH);

		// --- 1. CONFIGURACIÓN DE LA TABLA ---
		modeloDatos = new ModeloTablero();
		tabla = new JTable(modeloDatos);
		tabla.setRowHeight(45); // 10 filas * 45px = 450px altura cuerpo
		tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // CRÍTICO para control total
		tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tabla.setCellSelectionEnabled(true);
		tabla.setShowGrid(true);
		tabla.setGridColor(new Color(255, 255, 255, 50));
		// Pinta el fondo sobrante (si lo hubiera por error de pantalla) del color del agua
		tabla.setFillsViewportHeight(true); 
		tabla.setBackground(new Color(0, 105, 148)); 
		tabla.setDefaultRenderer(Object.class, new BarcoRenderer());
		tabla.setTransferHandler(new TableDropHandler());

		// --- 2. BLOQUEO TOTAL DEL HEADER ---
		javax.swing.table.JTableHeader header = tabla.getTableHeader();
		header.setResizingAllowed(false);   // Bloqueo general
		header.setReorderingAllowed(false); // Bloqueo de arrastrar columnas
		header.setFont(new Font("Segoe UI", Font.BOLD, 12));

		// Bloqueo individual POR COLUMNA (Impide que salga el cursor de redimensionar)
		for (int i = 0; i < 10; i++) {
		    javax.swing.table.TableColumn col = tabla.getColumnModel().getColumn(i);
		    col.setPreferredWidth(45);
		    col.setMinWidth(45);
		    col.setMaxWidth(45);
		    col.setResizable(false); // <--- ESTO congela el ancho
		}

		// --- 3. JSCROLLPANE "INVISIBLE" ---
		// Usamos JScrollPane para unir Header y Tabla perfectamente
		JScrollPane scroll = new JScrollPane(tabla);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(BorderFactory.createEmptyBorder()); // Sin borde interno
		scroll.getViewport().setBackground(new Color(0, 105, 148)); // Fondo de seguridad

		// --- 4. CONTENEDOR DE TAMAÑO EXACTO ---
		JPanel contenedorTabla = new JPanel(new BorderLayout());
		// Borde blanco semitransparente
		contenedorTabla.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2));

		// CÁLCULO MATEMÁTICO:
		// Altura = Altura real del Header + (10 filas * 45px) + (2px borde * 2 lados)
		int altoHeader = header.getPreferredSize().height;
		int altoCuerpo = 450; 
		int borde = 2;

		int altoTotal = altoHeader + altoCuerpo + (borde * 2);
		int anchoTotal = 450 + (borde * 2);

		Dimension dimensionExacta = new Dimension(anchoTotal, altoTotal);

		// Forzamos al contenedor a tener ESE tamaño y ni un píxel más
		contenedorTabla.setPreferredSize(dimensionExacta);
		contenedorTabla.setMaximumSize(dimensionExacta);
		contenedorTabla.setMinimumSize(dimensionExacta);

		contenedorTabla.add(scroll, BorderLayout.CENTER);

		// --- 5. PANEL CENTRADOR (GridBagLayout) ---
		// Usamos GridBagLayout en el panel padre para que el contenedorTabla se quede en el centro
		JPanel panelTabla = new JPanel(new GridBagLayout());
		panelTabla.setOpaque(false);
		panelTabla.add(contenedorTabla);

		mainPanel.add(panelTabla, BorderLayout.CENTER);

		// Panel Derecho (Controles)
		JPanel panelDer = new JPanel();
		panelDer.setLayout(new BoxLayout(panelDer, BoxLayout.Y_AXIS));
		panelDer.setOpaque(false);
		panelDer.setBorder(new EmptyBorder(20, 20, 20, 40));
		panelDer.setPreferredSize(new Dimension(300, 0));

		JLabel lblO = new JLabel("1. ORIENTACIÓN");
		lblO.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblO.setForeground(Color.CYAN);
		lblO.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDer.add(lblO);

		ButtonGroup grp = new ButtonGroup();
		orientacionHorizontal = new JRadioButton("Horizontal", true);
		orientacionVertical = new JRadioButton("Vertical");
		estilizarRadio(orientacionHorizontal);
		estilizarRadio(orientacionVertical);

		orientacionHorizontal.setAlignmentX(Component.LEFT_ALIGNMENT);
		orientacionVertical.setAlignmentX(Component.LEFT_ALIGNMENT);

		grp.add(orientacionHorizontal);
		grp.add(orientacionVertical);

		panelDer.add(orientacionHorizontal);
		panelDer.add(orientacionVertical);
		panelDer.add(Box.createVerticalStrut(20));

		JLabel lblA = new JLabel("2. ARRASTRAR BARCOS");
		lblA.setFont(new Font("Segoe UI", Font.BOLD, 16));
		lblA.setForeground(Color.CYAN);
		lblA.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelDer.add(lblA);
		panelDer.add(Box.createVerticalStrut(10));

		// Crear botones de barcos según configuración
		int[] fixedSizes = { 2, 3, 4, 5 };

		for (int tam : fixedSizes) {
			int buttonWidth = 60 + (tam * 30);
			int buttonHeight = 35;

			if (configBarcos.containsKey(tam)) {
				int cant = configBarcos.get(tam);

				JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
				p.setOpaque(false);
				p.setAlignmentX(Component.LEFT_ALIGNMENT);
				p.setMaximumSize(new Dimension(300, 35));

				JLabel lblB = new JLabel("Navío (" + tam + ")", SwingConstants.CENTER);
				lblB.setOpaque(true);
				lblB.setBackground(new Color(60, 90, 110));
				lblB.setForeground(Color.WHITE);
				lblB.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.WHITE),
						BorderFactory.createEmptyBorder(5, 0, 5, 0)));
				lblB.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lblB.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
				lblB.setMinimumSize(new Dimension(buttonWidth, buttonHeight));
				lblB.setMaximumSize(new Dimension(buttonWidth, buttonHeight));
				lblB.setAlignmentX(Component.CENTER_ALIGNMENT);
				lblB.setCursor(new Cursor(Cursor.HAND_CURSOR));

				lblB.setTransferHandler(new TransferHandler() {
					private static final long serialVersionUID = 1L;

					public int getSourceActions(JComponent c) {
						return COPY;
					}

					protected Transferable createTransferable(JComponent c) {
						return new StringSelection(String.valueOf(tam));
					}
				});
				lblB.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						JComponent c = (JComponent) e.getSource();
						c.getTransferHandler().exportAsDrag(c, e, TransferHandler.COPY);
					}
				});

				p.add(lblB);
				JLabel lblC = new JLabel("x" + cant);
				lblC.setFont(new Font("Segoe UI", Font.BOLD, 14));
				lblC.setForeground(Color.WHITE);
				labelsDisponibilidad.put(tam, lblC);
				p.add(lblC);
				panelDer.add(p);
			} else {
				JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
				p.setOpaque(false);
				p.setAlignmentX(Component.LEFT_ALIGNMENT);
				p.setMaximumSize(new Dimension(300, 35));
				p.add(Box.createRigidArea(new Dimension(buttonWidth, buttonHeight)));
				panelDer.add(p);
			}
			panelDer.add(Box.createVerticalStrut(5));
		}

		panelDer.add(Box.createVerticalStrut(60));

		JPanel botonera = new JPanel();
		botonera.setLayout(new BoxLayout(botonera, BoxLayout.Y_AXIS));
		botonera.setOpaque(false);
		botonera.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Boton para colocar barcos aleatoriamente
		JButton btnAleatorio = new JButton("COLOCAR ALEATORIAMENTE");
		estilizarBoton(btnAleatorio, new Color(255, 140, 0));
		btnAleatorio.addActionListener(e -> colocarBarcosAleatoriamente());
		botonera.add(btnAleatorio);

		botonera.add(Box.createVerticalStrut(10));

		JButton btnR = new JButton("REINICIAR");
		estilizarBoton(btnR, new Color(198, 40, 40));
		btnR.addActionListener(e -> reiniciarTablero());
		botonera.add(btnR);

		botonera.add(Box.createVerticalStrut(10));

		JButton btnG = new JButton("DESPLEGAR FLOTA");
		estilizarBoton(btnG, new Color(46, 125, 50));
		btnG.addActionListener(e -> {
			if (modeloDatos.contarBarcosColocados() == 0) {
				JOptionPane.showMessageDialog(this, "¡Comandante! Debe colocar al menos un barco.");
				return;
			}
			dispose();
			if (onGuardar != null)
				onGuardar.accept(modeloDatos.getTableroIDs());
		});
		botonera.add(btnG);

		// Ajuste fino para alinear con el fondo de la tabla (~480px de alto total)
		// La tabla tiene ~480px. Los botones de abajo ocupan ~150px.
		// El VerticalGlue empuja todo abajo.
		botonera.add(Box.createVerticalStrut(0));

		panelDer.add(botonera);

		mainPanel.add(panelDer, BorderLayout.EAST);
		actualizarLabels();
		setVisible(true);
	}

	// Logica para colocar barcos aleatoriamente
	private void colocarBarcosAleatoriamente() {
		reiniciarTablero();

		barcosOriginales.forEach((tam, cantidad) -> {
			for (int i = 0; i < cantidad; i++) {
				boolean colocado = false;
				int intentos = 0;

				while (!colocado && intentos < 200) {
					int r = (int) (Math.random() * 10);
					int c = (int) (Math.random() * 10);
					boolean horiz = Math.random() < 0.5;

					if (esPosicionValida(r, c, tam, horiz)) {
						int id = (int) (Math.random() * 100000);
						for (int k = 0; k < tam; k++) {
							modeloDatos.colocarPieza(r + (horiz ? 0 : k), c + (horiz ? k : 0),
									new PiezaBarco(id, k, tam, horiz));
						}
						colocado = true;
					}
					intentos++;
				}
			}
		});

		// 3. Actualizamos los contadores a 0 ya que se han colocado todos
		barcosDisponibles.replaceAll((k, v) -> 0);
		actualizarLabels();
		tabla.repaint();
	}

	// Verificar si la posicion del barco es valida
	private boolean esPosicionValida(int r, int c, int tam, boolean horiz) {
		if (horiz) {
			if (c + tam > 10)
				return false; // Se sale del ancho
			for (int k = 0; k < tam; k++) {
				if (modeloDatos.hayBarco(r, c + k))
					return false; // Choque
			}
		} else {
			if (r + tam > 10)
				return false; // Se sale del alto
			for (int k = 0; k < tam; k++) {
				if (modeloDatos.hayBarco(r + k, c))
					return false; // Choque
			}
		}
		return true;
	}

	// --- MÉTODOS AUXILIARES ---

	private void estilizarRadio(JRadioButton rb) {
		rb.setOpaque(false);
		rb.setForeground(Color.WHITE);
		rb.setFocusPainted(false);
		rb.setFont(new Font("Segoe UI", Font.PLAIN, 14));
	}

	private void estilizarBoton(JButton btn, Color bg) {
		btn.setBackground(bg);
		btn.setForeground(Color.WHITE);
		btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
		btn.setFocusPainted(false);
		btn.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn.setMaximumSize(new Dimension(250, 40));
		btn.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 100)));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(bg.brighter());
			}

			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});
	}

	private void cargarImagenBarco() {
		try {
			String nombreArchivo = this.nombreEquipo;
			if ("Submarine".equals(nombreArchivo))
				nombreArchivo = "SubMarine";
			String path = "resources/images/Ship/Ship" + nombreArchivo + "Hull.png";
			File f = new File(path);
			if (f.exists()) {
				imagenBarcoOriginal = ImageIO.read(f);
			} else {
				try {
					imagenBarcoOriginal = ImageIO
							.read(getClass().getResource("/images/Ship/Ship" + nombreArchivo + "Hull.png"));
				} catch (Exception e2) {
					imagenBarcoOriginal = new BufferedImage(100, 40, BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = imagenBarcoOriginal.createGraphics();
					g.setColor(Color.DARK_GRAY);
					g.fillRect(0, 0, 100, 40);
					g.dispose();
				}
			}
		} catch (Exception e) {
		}
	}

	private void reiniciarTablero() {
		modeloDatos.limpiar();
		barcosDisponibles = new HashMap<>(barcosOriginales);
		actualizarLabels();
		tabla.repaint();
	}

	private void actualizarLabels() {
		barcosDisponibles.forEach((t, c) -> {
			JLabel l = labelsDisponibilidad.get(t);
			if (l != null) {
				l.setText("x" + c);
				l.setForeground(c > 0 ? Color.GREEN : new Color(255, 100, 100));
			}
		});
	}

	// --- CLASES INTERNAS ---
	class ModeloTablero extends DefaultTableModel {
		private static final long serialVersionUID = 1L;
		private PiezaBarco[][] datos = new PiezaBarco[10][10];

		public ModeloTablero() {
			super(10, 10);
		}

		public Object getValueAt(int r, int c) {
			return datos[r][c];
		}

		public boolean isCellEditable(int r, int c) {
			return false;
		}

		public void colocarPieza(int r, int c, PiezaBarco p) {
			datos[r][c] = p;
			fireTableCellUpdated(r, c);
		}

		public boolean hayBarco(int r, int c) {
			if (r < 0 || r >= 10 || c < 0 || c >= 10)
				return false;
			return datos[r][c] != null;
		}

		public void limpiar() {
			datos = new PiezaBarco[10][10];
			fireTableDataChanged();
		}

		public int contarBarcosColocados() {
			int n = 0;
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					if (datos[i][j] != null)
						n++;
			return n;
		}

		public boolean[][] getTableroBooleano() {
			boolean[][] b = new boolean[10][10];
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					b[i][j] = (datos[i][j] != null);
			return b;
		}

		public int[][] getTableroIDs() {
			int[][] b = new int[10][10];
			for (int i = 0; i < 10; i++)
				for (int j = 0; j < 10; j++)
					b[i][j] = (datos[i][j] != null) ? datos[i][j].id : 0;
			return b;
		}
	}

	class PiezaBarco {
		int id, idx, total;
		boolean horiz;

		public PiezaBarco(int id, int idx, int total, boolean horiz) {
			this.id = id;
			this.idx = idx;
			this.total = total;
			this.horiz = horiz;
		}
	}

	class BarcoRenderer extends JPanel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;
		private PiezaBarco p;

		public BarcoRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
			this.p = (PiezaBarco) v;
			setBackground(new Color(0, 105, 148));
			return this;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (p == null)
				return;
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth(), h = getHeight();

			if (imagenBarcoOriginal != null) {
				if (p.horiz) {
					g2.drawImage(imagenBarcoOriginal, -(p.idx * w), 0, w * p.total, h, null);
				} else {
					AffineTransform at = g2.getTransform();
					g2.translate(w / 2.0, h / 2.0);
					g2.rotate(Math.toRadians(90));
					g2.translate(-h / 2.0, -w / 2.0);
					g2.drawImage(imagenBarcoOriginal, -(p.idx * h), 0, h * p.total, w, null);
					g2.setTransform(at);
				}
			} else {
				g2.setColor(Color.GRAY);
				g2.fillRect(2, 2, w - 4, h - 4);
			}
		}
	}

	class TableDropHandler extends TransferHandler {
		private static final long serialVersionUID = 1L;

		public boolean canImport(TransferSupport s) {
			return s.isDataFlavorSupported(DataFlavor.stringFlavor);
		}

		public boolean importData(TransferSupport s) {
			try {
				int tam = Integer.parseInt((String) s.getTransferable().getTransferData(DataFlavor.stringFlavor));
				if (barcosDisponibles.get(tam) <= 0)
					return false;
				JTable.DropLocation dl = (JTable.DropLocation) s.getDropLocation();
				int r = dl.getRow(), c = dl.getColumn();
				boolean h = orientacionHorizontal.isSelected();
				if (validar(r, c, tam, h)) {
					int id = (int) (Math.random() * 10000);
					for (int i = 0; i < tam; i++)
						modeloDatos.colocarPieza(r + (h ? 0 : i), c + (h ? i : 0), new PiezaBarco(id, i, tam, h));
					barcosDisponibles.put(tam, barcosDisponibles.get(tam) - 1);
					actualizarLabels();
					tabla.repaint();
					return true;
				} else {
					JOptionPane.showMessageDialog(ColocarBarcos.this, "¡El barco no cabe en esa posición!", "Error",
							JOptionPane.WARNING_MESSAGE);
				}
			} catch (Exception e) {
			}
			return false;
		}

		boolean validar(int r, int c, int tam, boolean h) {
			if (h) {
				if (c + tam > 10)
					return false;
				for (int i = 0; i < tam; i++)
					if (modeloDatos.hayBarco(r, c + i))
						return false;
			} else {
				if (r + tam > 10)
					return false;
				for (int i = 0; i < tam; i++)
					if (modeloDatos.hayBarco(r + i, c))
						return false;
			}
			return true;
		}
	}

	class GradientPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setPaint(new GradientPaint(0, 0, new Color(10, 40, 70), 0, getHeight(), new Color(5, 20, 30)));
			g2.fillRect(0, 0, getWidth(), getHeight());
		}
	}
}