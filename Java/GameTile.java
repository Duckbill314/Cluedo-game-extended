/**
 * The `GameTile` class represents a tile in the game that can hold an item.
 * It extends the `Tile` class and has the ability to store an `Item`.
 * @author William Huang
 */
public class GameTile extends Tile {

    /**
     * The item stored on this game tile, initialized as an empty item.
     */
    private Item stored = new Item("empty", "", 0, 0);

    /**
     * Constructor for the `GameTile` class.
     *
     * @param yCoord The Y-coordinate of the game tile.
     * @param xCoord The X-coordinate of the game tile.
     */
    public GameTile(int yCoord, int xCoord) {
        super(yCoord, xCoord);
    }

    /**
     * Sets the item stored on this game tile.
     *
     * @param aStored The item to be stored.
     */
    public void setStored(Item aStored) {
        stored = aStored;
    }

    /**
     * Gets the item stored on this game tile.
     *
     * @return The item stored on the tile.
     */
    public Item getStored() {
        return stored;
    }

    /**
     * Clears the item stored on this game tile, setting it to an empty item.
     */
    public void clearStored() {
        stored = new Item("empty", "", 0, 0);
    }

    /**
     * Returns a string representation of the tile's content.
     * If the tile is empty, it returns the default output of the tile.
     * If the tile contains an item, it returns the display icon of the item.
     *
     * @return A string representation of the tile's content.
     */
    public String draw() {
        if (stored.getName().equals("empty")) {
            return getOutput();
        } else {
            return stored.getDisplayIcon();
        }
    }
}