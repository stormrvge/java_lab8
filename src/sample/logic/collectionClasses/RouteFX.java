package sample.logic.collectionClasses;

import sample.commands.exceptions.OutOfBoundsException;

import java.io.Serializable;


/**
 * The general class in our collection. Collection contains elements of routes.
 * Class route contains coordinate and location classes.
 */
public class RouteFX implements Comparable<Route>, Serializable {
    private int id = 0; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name;
    private double coordX;
    private double coordY;
    private float fromX;
    private int fromY;
    private int fromZ;
    private float toX;
    private int toY;
    private int toZ;
    private float distance;
    private String owner;

    RouteFX(int id, String name, double coordX, double coordY, float fromX, int fromY, int fromZ, float toX,
            int toY, int toZ, float distance, String owner) throws NullPointerException, OutOfBoundsException {
        if (distance < 1) throw new OutOfBoundsException();

        this.id = id;
        this.name = name;
        this.coordX = coordX;
        this.coordY = coordY;
        this.fromX = fromX;
        this.fromY = fromY;
        this.fromZ = fromZ;
        this.toX = toX;
        this.toY = toY;
        this.toZ = toZ;
        this.distance = distance;
        this.owner = owner;
    }

    public void setName(String name) {
        if (name == null || name.trim().equals("")) throw new NullPointerException("Name cant be null");
        this.name = name;
    }

    public void setDistance(float distance) throws OutOfBoundsException {
        if (distance < 1) throw new OutOfBoundsException();
        this.distance = distance;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {return this.id;}

    public String getName() {return this.name;}

    public float getDistance() {return distance;}

    public String getOwner() {return owner;}

    public double getCoordX() {
        return coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public float getFromX() {
        return fromX;
    }

    public float getFromY() {
        return fromY;
    }

    public float getFromZ() {
        return fromZ;
    }

    public float getToX() {
        return toX;
    }

    public float getToY() {
        return toY;
    }

    public float getToZ() {
        return toZ;
    }

    @Override
    public int compareTo(Route route) {
        return Float.compare(distance, route.getDistance());
    }
}