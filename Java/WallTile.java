/**
 * The `WallTile` class represents a wall tile in a game, which blocks movement.
 * It extends the `Tile` class and sets the output to "X" to visually represent a wall.
 *
 * @author William Huang
 */
public class WallTile extends Tile {

    /**
     * Constructor for the WallTile class.
     *
     * @param yCoord The Y-coordinate of the wall tile.
     * @param xCoord The X-coordinate of the wall tile.
     */
    public WallTile(int yCoord, int xCoord) {
        super(yCoord, xCoord);
        this.output = "X"; // Set the output to "X" to represent a wall visually.
    }
}