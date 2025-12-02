package com.zombicide.missiongen.ui.components;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.zombicide.missiongen.ui.interfaces.AreaDrawerListener;

public class AreaDrawer extends MouseAdapter {
    private Point startPoint;
    private Point endPoint;
    private Rectangle currentDrawingArea;
    private boolean isDrawing;
    private AreaDrawerListener listener;
    private Component component;

    public AreaDrawer(AreaDrawerListener listener, Component component) {
        this.listener = listener;
        this.component = component;
        this.isDrawing = false;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) { // Botón izquierdo
            startPoint = e.getPoint();
            endPoint = startPoint;
            isDrawing = true;
            currentDrawingArea = null;
            component.repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (isDrawing) {
            endPoint = e.getPoint();
            updateDrawingArea();
            component.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && isDrawing) {
            endPoint = e.getPoint();
            updateDrawingArea();
            isDrawing = false;

            // Notificar al listener si el área es válida (tiene tamaño)
            if (currentDrawingArea != null &&
                    currentDrawingArea.width > 0 &&
                    currentDrawingArea.height > 0) {
                if (listener != null) {
                    listener.onAreaDrawn(currentDrawingArea.x, currentDrawingArea.y,
                            currentDrawingArea.x + currentDrawingArea.width,
                            currentDrawingArea.y + currentDrawingArea.height);
                }
            }

            // Limpiar el rectángulo temporal
            currentDrawingArea = null;
            component.repaint();
        }
    }

    private void updateDrawingArea() {
        if (startPoint != null && endPoint != null) {
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y);
            int width = Math.abs(endPoint.x - startPoint.x);
            int height = Math.abs(endPoint.y - startPoint.y);

            currentDrawingArea = new Rectangle(x, y, width, height);
        }
    }

    public Rectangle getCurrentDrawingArea() {
        return currentDrawingArea;
    }

    public boolean isDrawing() {
        return isDrawing;
    }

    public void reset() {
        startPoint = null;
        endPoint = null;
        currentDrawingArea = null;
        isDrawing = false;
        component.repaint();
    }
}
