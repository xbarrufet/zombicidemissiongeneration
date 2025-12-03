package com.zombicide.missiongen.model.helpers;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ComposicionConSombra3D extends JFrame {

    private BufferedImage fondo;
    private BufferedImage imagenCompuesta;

    // Clase auxiliar para manejar la imagen y su posición
    public static class ElementoConPosicion {
        public BufferedImage imagen;
        public int x;
        public int y;

        public ElementoConPosicion(BufferedImage imagen, int x, int y) {
            this.imagen = imagen;
            this.x = x;
            this.y = y;
        }
    }

    public ComposicionConSombra3D() {
        setTitle("Composición con Sombra 3D y AffineTransform");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600); 
        setLocationRelativeTo(null); 

        List<ElementoConPosicion> elementos = new ArrayList<>();

        // --- Cargar y preparar imágenes ---
        try {
            // Intenta cargar el fondo (sustituye la ruta si es necesario)
            fondo = ImageIO.read(new File("path/to/tu_fondo.png")); 
            if (fondo == null) {
                fondo = generarFondoDePrueba(800, 600);
            }
            
            // Generar o cargar elementos de prueba
            BufferedImage bola = generarElementoDePrueba(100, 100, Color.ORANGE, "Bola");
            BufferedImage cubo = generarElementoDePrueba(80, 80, new Color(50, 200, 150), "Cubo");
            
            // Añadir elementos a la lista con sus coordenadas
            elementos.add(new ElementoConPosicion(bola, 100, 400));
            elementos.add(new ElementoConPosicion(cubo, 350, 420));
            elementos.add(new ElementoConPosicion(bola, 600, 350));

        } catch (IOException e) {
            System.err.println("Error al cargar imágenes. Usando imágenes de prueba.");
            fondo = generarFondoDePrueba(800, 600);
        }

        // Realizar la composición
        imagenCompuesta = componerMultiplesElementos(fondo, elementos);

        // --- Panel para dibujar la imagen ---
        JPanel panelDibujo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenCompuesta != null) {
                    g.drawImage(imagenCompuesta, 0, 0, this);
                } else {
                    g.drawString("Cargando o error en imagen compuesta...", 50, 50);
                }
            }
        };

        add(panelDibujo);
        setVisible(true);
    }

    // ====================================================================
    // 1. MÉTODOS AUXILIARES PARA EL FILTRADO Y LA GENERACIÓN
    // ====================================================================

    /**
     * Aplica un filtro de desenfoque (blur) a una imagen usando ConvolveOp.
     */
    private BufferedImage aplicarDesenfoque(BufferedImage imagen, int radio) {
        if (radio < 1) return imagen;

        int size = radio * 2 + 1;
        float[] data = new float[size * size];
        float value = 1.0f / (size * size);
        for (int i = 0; i < data.length; i++) {
            data[i] = value;
        }
        
        Kernel kernel = new Kernel(size, size, data);
        BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
        BufferedImage destino = new BufferedImage(imagen.getWidth(), imagen.getHeight(), BufferedImage.TYPE_INT_ARGB);

        return op.filter(imagen, destino);
    }

    /**
     * Genera un fondo de prueba simple.
     */
    private BufferedImage generarFondoDePrueba(int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(new Color(173, 216, 230)); // Azul claro
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(new Color(100, 100, 100)); // Gris
        g2d.drawString("Fondo de Prueba (800x600)", width / 2 - 80, height / 2);
        g2d.dispose();
        return img;
    }

    /**
     * Genera un elemento de prueba (círculo) para la composición.
     */
    private BufferedImage generarElementoDePrueba(int width, int height, Color color, String nombre) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color);
        g2d.fillOval(0, 0, width, height); 
        g2d.setColor(Color.BLACK);
        g2d.drawString(nombre, width / 2 - 20, height / 2);
        g2d.dispose();
        return img;
    }
    
    // ====================================================================
    // 2. MÉTODO PRINCIPAL DE COMPOSICIÓN (con AffineTransform)
    // ====================================================================

    /**
     * Compone la imagen de fondo con múltiples elementos, aplicando una sombra 
     * desenfoqueada y proyectada con AffineTransform para simular solidez (3D).
     */
    public BufferedImage componerMultiplesElementos(
        BufferedImage fondo, 
        List<ElementoConPosicion> elementos
    ) {
        int ancho = fondo.getWidth();
        int alto = fondo.getHeight();
        
        // Propiedades de la Sombra y el Efecto 3D
        float opacidadSombra = 0.5f; 
        int offsetX = 0; // Se recomienda 0 o muy pequeño para sombras proyectadas
        int offsetY = 0; 
        int radioDesenfoque = 4; // Un desenfoque mayor ayuda al efecto 3D
        float perspectiva = 0.3f; // Sesgado (shear) para la proyección en el "suelo"

        BufferedImage resultado = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2dResultado = resultado.createGraphics();
        g2dResultado.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2dResultado.drawImage(fondo, 0, 0, null);
        
        // Iterar y dibujar la sombra y luego el elemento para cada objeto
        for (ElementoConPosicion item : elementos) {
            BufferedImage elemento = item.imagen;
            int x = item.x;
            int y = item.y;
            
            int elementoAncho = elemento.getWidth();
            int elementoAlto = elemento.getHeight();

            // --- Generación de la Sombra ---
            BufferedImage sombraTemp = new BufferedImage(elementoAncho, elementoAlto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2dSombra = sombraTemp.createGraphics();
            g2dSombra.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2dSombra.drawImage(elemento, 0, 0, null);

            g2dSombra.setColor(new Color(0, 0, 0, (int)(255 * opacidadSombra)));
            g2dSombra.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN));
            g2dSombra.fillRect(0, 0, elementoAncho, elementoAlto);
            g2dSombra.dispose(); 
            
            // Aplicar Desenfoque
            BufferedImage sombraBlur = aplicarDesenfoque(sombraTemp, radioDesenfoque);
            
            // --- Dibujar Sombra y Elemento en la Imagen Final ---
            
            // 1. Dibujar la Sombra con AffineTransform (Perspectiva)
            int sombraX = x + offsetX;
            int sombraY = y + offsetY;
            
            // Crear la transformación de sesgado (shear) para la proyección
            AffineTransform tx = new AffineTransform();
            
            // Mover (translate) la sombra a la posición de inicio
            tx.translate(sombraX, sombraY); 
            
            // Aplicar el sesgado (0.0 en X, y 'perspectiva' en Y para el efecto 3D aplanado)
            tx.shear(0.0, perspectiva); 
            
            g2dResultado.setComposite(AlphaComposite.SrcOver);
            g2dResultado.drawImage(sombraBlur, tx, null); // Dibuja la imagen transformada
            
            // 2. Dibujar el Elemento Original (encima de la sombra)
            g2dResultado.drawImage(elemento, x, y, null);
        }
        
        g2dResultado.dispose();

        return resultado;
    }

    // ====================================================================
    // 3. MAIN (Punto de entrada de la aplicación)
    // ====================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ComposicionConSombra3D());
    }
}