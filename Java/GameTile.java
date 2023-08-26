public class GameTile extends Tile
{
  private Item stored = null;

  public GameTile(int yCoord, int xCoord)
  {
    super(yCoord, xCoord);
  }

  public boolean setStored(Item aStored)
  {
    boolean wasSet = false;
    stored = aStored;
    wasSet = true;
    return wasSet;
  }

  public Item getStored()
  {
    return stored;
  }

  public void clearStored() {
    stored = null;
  }

  public String draw() {
    if(stored == null) {
      return getOutput();
    } else {
      return stored.getDisplayIcon();
    }
  }
}
