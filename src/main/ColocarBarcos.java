package main;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

public class ColocarBarcos extends JFrame {

    private static final long serialVersionUID = 1L;

    // Componentes
    private JTable tabla;
    private ModeloTablero modeloDatos;
    private JRadioButton orientacionHorizontal;
    private JRadioButton orientacionVertical;
    // Datos
    private Map<Integer, Integer> barcosDisponibles;
    private Map<Integer, Integer> barcosOriginales;
    private Map<Integer, JLabel> labelsDisponibilidad = new HashMap<>();
    
    // Imagen
    private BufferedImage imagenBarcoOriginal;

    public ColocarBarcos(int numeroJugador, Map<Integer, Integer> configBarcos, Consumer<boolean[][]> onGuardar) {
        
        this.barcosDisponibles = new HashMap<>(configBarcos);
        this.barcosOriginales = new HashMap<>(configBarcos);

        cargarImagenBarco();

        setTitle("Colocar Barcos (Modo Tabla) - Jugador " + numeroJugador);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

       
        JPanel panelSuperior = new JPanel();
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JLabel titulo = new JLabel("Arrastra los barcos a la tabla");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        panelSuperior.add(titulo);
        add(panelSuperior, BorderLayout.NORTH);

        
        // Modelo de datos personalizado (10x10)
        modeloDatos = new ModeloTablero();
        
        tabla = new JTable(modeloDatos);
        tabla.setRowHeight(40); // Altura de celda
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setCellSelectionEnabled(true);
        tabla.setShowGrid(true);
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setIntercellSpacing(new Dimension(1, 1)); // Espacio para ver la rejilla
        
        // Asignar nuestro RENDERER personalizado
        tabla.setDefaultRenderer(Object.class, new BarcoRenderer());
        
        // Habilitar Drop en la tabla
        tabla.setTransferHandler(new TableDropHandler());

        // Panel contenedor para centrar la tabla
        JPanel panelTablaContainer = new JPanel(new GridBagLayout());
        panelTablaContainer.setBackground(new Color(240, 240, 240));
        
        // Ajustar ancho de columnas a 40px
        for (int i = 0; i < 10; i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(40);
            tabla.getColumnModel().getColumn(i).setMinWidth(40);
            tabla.getColumnModel().getColumn(i).setMaxWidth(40);
        }
        
        // Envolver en ScrollPane (sin barras si ajustamos bien el tamaño)
        JScrollPane scrollPane = new JScrollPane(tabla);
        scrollPane.setPreferredSize(new Dimension(403, 403)); // 10*40 + bordes
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        panelTablaContainer.add(scrollPane);
        add(panelTablaContainer, BorderLayout.CENTER);

       
        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelDerecho.setPreferredSize(new Dimension(280, 0));

        panelDerecho.add(new JLabel("1. Orientación:"));
        ButtonGroup grupo = new ButtonGroup();
        orientacionHorizontal = new JRadioButton("Horizontal", true);
        orientacionVertical = new JRadioButton("Vertical");
        grupo.add(orientacionHorizontal);
        grupo.add(orientacionVertical);
        panelDerecho.add(orientacionHorizontal);
        panelDerecho.add(orientacionVertical);
        
        panelDerecho.add(Box.createVerticalStrut(20));
        panelDerecho.add(new JLabel("2. Arrastra el nombre:"));
        panelDerecho.add(Box.createVerticalStrut(10));

        // Crear lista de barcos arrastrables
        configBarcos.forEach((tamano, cantidad) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            
            JLabel lblBarco = new JLabel(" ⚓ Barco (" + tamano + ") ");
            lblBarco.setOpaque(true);
            lblBarco.setBackground(new Color(220, 220, 220));
            lblBarco.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            lblBarco.setFont(new Font("SansSerif", Font.PLAIN, 14));
            lblBarco.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Lógica de arrastre
            lblBarco.setTransferHandler(new TransferHandler() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public int getSourceActions(JComponent c) { return COPY; }
                
                @Override
                protected Transferable createTransferable(JComponent c) {
                    return new StringSelection(String.valueOf(tamano));
                }
            });
            
            lblBarco.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JComponent c = (JComponent) e.getSource();
                    c.getTransferHandler().exportAsDrag(c, e, TransferHandler.COPY);
                }
            });

            p.add(lblBarco);
            
            JLabel lblCount = new JLabel("x" + cantidad);
            labelsDisponibilidad.put(tamano, lblCount);
            p.add(lblCount);
            
            panelDerecho.add(p);
        });

        panelDerecho.add(Box.createVerticalGlue());
        
        JButton btnReiniciar = new JButton("Reiniciar");
        btnReiniciar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnReiniciar.addActionListener(e -> reiniciarTablero());
        panelDerecho.add(btnReiniciar);
        
        panelDerecho.add(Box.createVerticalStrut(10));

        JButton btnGuardar = new JButton("GUARDAR");
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.setBackground(new Color(100, 180, 100));
        btnGuardar.addActionListener(e -> {
            if (modeloDatos.contarBarcosColocados() == 0) {
                JOptionPane.showMessageDialog(this, "Coloca algún barco antes.");
                return;
            }
            dispose();
            if (onGuardar != null) onGuardar.accept(modeloDatos.getTableroBooleano());
        });
        panelDerecho.add(btnGuardar);

        add(panelDerecho, BorderLayout.EAST);
        
        actualizarLabels(); // Estado inicial
        setVisible(true);
    }
    
    private void cargarImagenBarco() {
        try {
            File f = new File("resources/images/ShipCruiserHull.png");
            if (f.exists()) imagenBarcoOriginal = ImageIO.read(f);
            else {
                // Imagen fallback generada en código
                imagenBarcoOriginal = new BufferedImage(100, 40, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = imagenBarcoOriginal.createGraphics();
                g.setColor(Color.DARK_GRAY);
                g.fillRect(0, 0, 100, 40);
                g.setColor(Color.WHITE);
                g.drawRect(0,0,99,39);
                g.dispose();
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void reiniciarTablero() {
        modeloDatos.limpiar();
        barcosDisponibles = new HashMap<>(barcosOriginales);
        actualizarLabels();
        tabla.repaint();
    }
    
    private void actualizarLabels() {
        barcosDisponibles.forEach((tam, cant) -> {
            JLabel l = labelsDisponibilidad.get(tam);
            if (l != null) {
                l.setText("x" + cant);
                l.setForeground(cant > 0 ? new Color(0, 128, 0) : Color.RED);
            }
        });
    }

    // Tabla de datos
   
    // Guardamos objetos 'PiezaBarco' en cada celda. Si es null, es agua.
    class ModeloTablero extends DefaultTableModel {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PiezaBarco[][] datos = new PiezaBarco[10][10];

        public ModeloTablero() {
            super(10, 10);
        }

        @Override // Siempre devuelve nuestros objetos PiezaBarco
        public Object getValueAt(int row, int column) {
            return datos[row][column];
        }

        @Override // No editable por teclado
        public boolean isCellEditable(int row, int column) {
            return false;
        }
        
        public void colocarPieza(int r, int c, PiezaBarco p) {
            datos[r][c] = p;
            fireTableCellUpdated(r, c);
        }
        
        public boolean hayBarco(int r, int c) {
            if (r<0 || r>=10 || c<0 || c>=10) return false;
            return datos[r][c] != null;
        }
        
        public void limpiar() {
            datos = new PiezaBarco[10][10];
            fireTableDataChanged();
        }
        
        public int contarBarcosColocados() {
            int c = 0;
            for(int i=0;i<10;i++) for(int j=0;j<10;j++) if(datos[i][j]!=null) c++;
            return c;
        }

        public boolean[][] getTableroBooleano() {
            boolean[][] b = new boolean[10][10];
            for(int i=0;i<10;i++) for(int j=0;j<10;j++) b[i][j] = (datos[i][j] != null);
            return b;
        }
    }

    // Clase auxiliar para saber qué pintar en cada celda
    class PiezaBarco {
        int idBarco;    // Para diferenciar barcos
        int indiceParte; // 0=inicio, 1=medio, 2=fin, etc.
        int tamanoTotal;
        boolean horizontal;

        public PiezaBarco(int id, int idx, int total, boolean horiz) {
            this.idBarco = id;
            this.indiceParte = idx;
            this.tamanoTotal = total;
            this.horizontal = horiz;
        }
    }


    // Renderer
  
    class BarcoRenderer extends JPanel implements TableCellRenderer {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private PiezaBarco piezaActual;

        public BarcoRenderer() {
            setOpaque(true); // Necesario para pintar el fondo
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, 
                                                       int row, int column) {
            // Guardamos el valor para usarlo en paintComponent
            this.piezaActual = (PiezaBarco) value;
            
            // Fondo base (Agua)
            setBackground(new Color(0, 150, 200)); 
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // Pinta el fondo azul primero
            
            if (piezaActual == null) return; // Si es agua, terminamos

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            if (imagenBarcoOriginal != null) {
           
                // Calculamos qué trozo de la imagen original corresponde a ESTA celda.
                // Si el barco es de tamaño 3, y esta celda es el índice 1 (medio):
                // Tenemos que pintar el tercio central de la imagen.

                if (piezaActual.horizontal) {
                    // El ancho total virtual del barco sería: w * tamanoTotal
                    // Queremos dibujar la imagen estirada a ese ancho virtual,
                    // pero desplazada para que solo se vea nuestra parte.
                    
                    int anchoTotalVirtual = w * piezaActual.tamanoTotal;
                    int desplazamientoX = -(piezaActual.indiceParte * w);
                    
                    // Dibujamos la imagen completa desplazada hacia la izquierda
                    // El componente (JPanel) actúa como una "máscara" que solo deja ver lo que cae dentro
                    g2d.drawImage(imagenBarcoOriginal, desplazamientoX, 0, anchoTotalVirtual, h, null);
                    
                } else {
                    // Guardar estado original
                    AffineTransform backup = g2d.getTransform();
                    
                   
                    // Para simplificar, rotamos el lienzo:
                    g2d.rotate(Math.toRadians(90), w/2.0, h/2.0);
                    
                    // Restauramos para usar un método manual de dibujo vertical si la rotación falla visualmente
                    g2d.setTransform(backup);
                    
                    // Creamos una transformación que coloque la imagen verticalmente
                     g2d.translate(w/2.0, h/2.0);
                     g2d.rotate(Math.toRadians(90));
                     g2d.translate(-h/2.0, -w/2.0); // Intercambiamos w/h por la rotación
                     
                     // Ahora pinto "horizontalmente" en el sistema rotado
                     // ancho virtual = h * tamano
                     int anchoVirt = h * piezaActual.tamanoTotal;
                     int desp = -(piezaActual.indiceParte * h);
                     
                     g2d.drawImage(imagenBarcoOriginal, desp, 0, anchoVirt, w, null);
                     
                     g2d.setTransform(backup);
                }
            }
        }
    }

    // Lógica de soltar
  
    class TableDropHandler extends TransferHandler {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		

		@Override
        public boolean canImport(TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferSupport support) {
            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                int tamano = Integer.parseInt(data);
                
                // Verificar Stock
                if (barcosDisponibles.get(tamano) <= 0) {
                    JOptionPane.showMessageDialog(ColocarBarcos.this, "No quedan barcos de tamaño " + tamano);
                    return false;
                }

                JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
                int row = dl.getRow();
                int col = dl.getColumn();
                boolean horiz = orientacionHorizontal.isSelected();

                // Validar si cabe y si no hay colisión
                if (validarColocacion(row, col, tamano, horiz)) {
                    // Colocar en el modelo
                    int idBarco = (int)(Math.random() * 10000); // ID único simple
                    for (int i = 0; i < tamano; i++) {
                        int r = row + (horiz ? 0 : i);
                        int c = col + (horiz ? i : 0);
                        modeloDatos.colocarPieza(r, c, new PiezaBarco(idBarco, i, tamano, horiz));
                    }
                    
                    // Actualizar stock
                    int stock = barcosDisponibles.get(tamano) - 1;
                    barcosDisponibles.put(tamano, stock);
                    actualizarLabels();
                    
                    tabla.repaint(); // Forzar repintado del renderer
                    return true;
                } else {
                    JOptionPane.showMessageDialog(ColocarBarcos.this, "Posición inválida o ocupada");
                    return false;
                }
            } catch (Exception e) { e.printStackTrace(); }
            return false;
        }

        private boolean validarColocacion(int r, int c, int tam, boolean horiz) {
            if (horiz) {
                if (c + tam > 10) return false;
                for (int i = 0; i < tam; i++) if (modeloDatos.hayBarco(r, c + i)) return false;
            } else {
                if (r + tam > 10) return false;
                for (int i = 0; i < tam; i++) if (modeloDatos.hayBarco(r + i, c)) return false;
            }
            return true;
        }
    }
}