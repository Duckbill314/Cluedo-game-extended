public class GameTile extends Tile {
    private Item stored = new Item("empty", "", 0, 0);

    public GameTile(int yCoord, int xCoord) {
        super(yCoord, xCoord);
    }

    public void setStored(Item aStored) {
        stored = aStored;
    }

    public Item getStored() {
        return stored;
    }

    public void clearStored() {
        stored = new Item("empty", "", 0, 0);
    }

    public String draw() {
        if (stored.getName().equals("empty")) {
            return getOutput();
        } else {
            return stored.getDisplayIcon();
        }
    }
}
