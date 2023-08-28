/**
 * The Tile class represents a basic tile in a game.
 * It includes information about its coordinates and an output string for display purposes.
 *
 * @author William Huang
 * @author Finley Neilson
 */
public class Tile {

    /**
     * The X-coordinate of the tile.
     */
    public int xCoord;

    /**
     * The Y-coordinate of the tile.
     */
    public int yCoord;

    /**
     * The output string associated with the tile for display purposes.
     */
    public String output;

    /**
     * Constructor for the Tile class.
     *
     * @param yCoord The Y-coordinate of the tile.
     * @param xCoord The X-coordinate of the tile.
     */
    public Tile(int yCoord, int xCoord) {
        this.yCoord = yCoord;
        this.xCoord = xCoord;
        this.output = "";
    }

    /**
     * Gets the output string associated with the tile.
     *
     * @return The output string of the tile.
     */
    public String getOutput() {
        return this.output;
    }

    /**
     * Returns a string representation of the tile's output.
     *
     * @return The tile's output string.
     */
    public String draw() {
        return getOutput();
    }
}