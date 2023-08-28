/**
 * The EntranceTile class represents a tile on the game board that serves as an entrance to an estate.
 * It extends the Tile class and includes information about the associated estate and exit coordinates.
 * 
 * @author William Huang
 */
public class EntranceTile extends Tile {

    /**
     * The estate associated with this entrance tile.
     */
    public Estate estate;

    /**
     * The Y-coordinate of the exit tile, where a player will be placed upon taking the exit.
     */
    public int exitY;

    /**
     * The X-coordinate of the exit tile, where a player will be placed upon taking the exit.
     */
    public int exitX;

    /**
     * Constructor for the EntranceTile class.
     *
     * @param yCoord The Y-coordinate of the entrance tile.
     * @param xCoord The X-coordinate of the entrance tile.
     * @param estate The estate associated with this entrance tile.
     * @param exitY  The Y-coordinate of the exit tile.
     * @param exitX  The X-coordinate of the exit tile.
     */
    public EntranceTile(int yCoord, int xCoord, Estate estate, int exitY, int exitX) {
        super(yCoord, xCoord);
        this.estate = estate;
        this.exitY = exitY;
        this.exitX = exitX;
    }

    /**
     * Gets the estate associated with this entrance tile.
     *
     * @return The estate associated with the entrance tile.
     */
    public Estate getEstate() {
        return this.estate;
    }

    /**
     * Gets the Y-coordinate of the exit tile where a player will be placed upon taking the exit.
     *
     * @return The Y-coordinate of the exit tile.
     */
    public int getExitY() {
        return this.exitY;
    }

    /**
     * Gets the X-coordinate of the exit tile where a player will be placed upon taking the exit.
     *
     * @return The X-coordinate of the exit tile.
     */
    public int getExitX() {
        return this.exitX;
    }
}