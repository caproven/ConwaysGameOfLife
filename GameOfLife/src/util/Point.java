package util;

public class Point {
    private int x, y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    
    public int getY() { return y; }
    
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
            Point oP = (Point)o;
            return x == oP.x && y == oP.y;
        }
        return false;
    }
}
