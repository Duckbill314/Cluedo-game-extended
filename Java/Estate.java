import java.util.*;

/**
 * The Estate class for HobbyDetectives.
 * It is an abstract logical representation of Tiles.
 * It contains a List of GameTimes, which comprise the domain of the Estate.
 * Items such as Characters and Weapons can be stored in these GameTiles.
 * It also contains a List of EntranceTiles, which mark the entrances of the Estate.
 * When a player attempts to enter or exit the Estate, it will be through these EntranceTiles.
 * 
 * @author William Huang
 */
public class Estate {
    private String name;
    private List<Item> items = new ArrayList<Item>();
    private List<GameTile> estateTiles = new ArrayList<GameTile>();
    private List<EntranceTile> EntranceTiles = new ArrayList<>();

    /**
     * Constructor for the Estate class.
     * 
     * @param aName the name of the estate
     */
    public Estate(String aName) {
        name = aName;
    }

    // Every estate will have four entrance tiles corresponding to the best fitting entrances of North, East, South, and West
    public void addEntrance(EntranceTile entrance) {
        this.EntranceTiles.add(entrance);
    }

    // These tiles are purely for storing weapons and characters that end up inside of the estate
    public void addEstateTile(GameTile estateTile) {
        this.estateTiles.add(estateTile);
    }

    /**
     * This method couples the list of items inside the estate, to the estate's storage tiles.
     * It is called any time the contents of the estate changes.
     */
    public void updateContents() {
        for (int i = 0; i < estateTiles.size(); i++) {
            if (i < items.size()) {
                estateTiles.get(i).setStored(items.get(i));
            } else {
                estateTiles.get(i).clearStored();
            }
        }
    }

    public void addItem(Item item) {
        this.items.add(item);
        updateContents();
    }

    public void removeItem(Item item) {
        this.items.remove(this.items.indexOf(item));
        updateContents();
    }

    public String getName() {
        return this.name;
    }

    public List<Item> getItems() {
        return this.items;
    }

    public List<EntranceTile> getEntranceTiles() {
        return this.EntranceTiles;
    }
}
