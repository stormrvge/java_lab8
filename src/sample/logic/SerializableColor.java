package sample.logic;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class SerializableColor implements Serializable {
    private final double red;
    private final double green;
    private final double blue;

    public SerializableColor(double red, double green, double blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color getColor() {
        double alpha = 1.0;
        return new Color(red, green, blue, alpha);
    }
}
