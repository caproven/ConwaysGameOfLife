package model;

/**
 * Represents a single point on the screen, with x and y coordinates.
 * @author caproven
 */
public class Point {
    /** Coordinates of the Point on the screen. */
    private int x, y;

    /**
     * Constructs the Point with the given coordinates.
     * @param x X coordinate of the Point
     * @param y Y coordinate of the Point
     */
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Retrieves the X coordinate of the Point.
     * @return X coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Retrieves the Y coordinate of the Point.
     * @return Y coordinate
     */
    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "X:" + x + ";Y:" + y;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Point) {
            Point oP = (Point) o;
            return x == oP.x && y == oP.y;
        }
        return false;
    }
}
