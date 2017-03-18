/**
 * Represents a taxi which will appear on the map
 */
public class Taxi {

    private int id;
    private int xPosition;
    private int yPosition;

    /**
     * Ctor
     */
    public Taxi(int id) {

        this.id = id;
        this.xPosition = 0;
        this.yPosition = 0;
    }

    /**
     * Sets the taxi's coordinates.
     *
     * @param x coordinate
     * @param y coordinate
     */
    public void setPosition(int x, int y) {

        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * Returns the taxi's id.
     *
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * Returns the taxi's X coordinate.
     *
     * @return int
     */
    public int getxPosition() {
        return this.xPosition;
    }

    /**
     * Returns the taxi's X coordinate.
     *
     * @return int
     */
    public int getyPosition() {
        return this.yPosition;
    }
}
