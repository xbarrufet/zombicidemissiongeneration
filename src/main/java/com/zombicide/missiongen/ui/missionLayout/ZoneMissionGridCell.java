package com.zombicide.missiongen.ui.missionLayout;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.zombicide.missiongen.config.ConfigLoader;
import com.zombicide.missiongen.ui.interfaces.GridClickListener;

public class ZoneMissionGridCell extends JPanel {

    private Image cellBackground;
    private Image emptyBackground;

    private boolean missionLayoutValid = true;

    public ZoneMissionGridCell(int row, int col, GridClickListener listener) {

        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        // setBackground(Color.LIGHT_GRAY);
        this.emptyBackground = ConfigLoader.getInstance().getNoTileYetImage();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    listener.onGridDoubleClick(col, row);
                    return;
                }
                if (e.getButton() == MouseEvent.BUTTON1) {
                    listener.onGridClick(col, row);
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    listener.onGridRightClick(col, row);
                }
            }
        });

    }

    public void setCellBackground(Image cellBackground) {
        this.cellBackground = cellBackground;
    }

    public void setEmptyBackground(Image emptyBackground) {
        this.emptyBackground = emptyBackground;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int width = getWidth();
        int height = getHeight();

        // g2d.rotate(Math.toRadians(rotation), width / 2.0, height / 2.0);
        if (cellBackground != null) {
            g2d.drawImage(this.cellBackground, 0, 0, width, height, this);
        } else {
            g2d.drawImage(this.emptyBackground, 0, 0, width, height, this);
        }
        if (!missionLayoutValid) {
            applyRedFilter(g2d);
        }
        g2d.dispose();
    }

    // --- Aplicar el Filtro Transparente Rojo ---
    private void applyRedFilter(Graphics2D g2d) {
        // 2. Definir el color: Rojo (255, 0, 0) y un valor alfa de 60 (más
        // transparente)
        int nivelAlfa = 125; // 0 (transparente) a 255 (opaco)
        Color colorFiltro = new Color(255, 0, 0, nivelAlfa);

        // 3. Configurar el AlphaComposite para la transparencia
        float alpha = (float) nivelAlfa / 255f;
        // Establece el modo SRC_OVER para superponer el nuevo dibujo
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // 4. Establecer el color y dibujar el rectángulo
        g2d.setColor(colorFiltro);
        // Dibuja el rectángulo cubriendo toda el área donde está la imagen
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    public void setLayouValid(boolean missionLayoutValid) {
        this.missionLayoutValid = missionLayoutValid;
    }

}
