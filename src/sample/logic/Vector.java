package sample.logic;

import javafx.scene.layout.Pane;

public class Vector {
    private final double x;
    private double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public static Vector toPixels(Pane pane, int pixelStep, double x, double y) {
        double width = pane.getPrefWidth();
        double height = pane.getPrefHeight();

        double newX = width / 2 + x * pixelStep;
        double newY = height / 2 - y * pixelStep;

        return new Vector(newX, newY);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
