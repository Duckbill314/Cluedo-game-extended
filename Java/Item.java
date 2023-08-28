/**
 * The Item class represents an item in a game, which can be placed on the game board.
 * It includes properties such as name, display icon, position, and an associated estate.
 * 
 * @author William Huang
 * @author James Goode
 */
public class Item {

  /**
   * The name of the item.
   */
  private String name;

  /**
   * The display icon or image representing the item.
   */
  private String displayIcon;

  /**
   * The X-coordinate position of the item.
   */
  private int x;

  /**
   * The Y-coordinate position of the item.
   */
  private int y;

  /**
   * The estate associated with this item.
   * Used for teleporting objects between estates.
   */
  private Estate estate;

  /**
   * Constructor for the Item class.
   *
   * @param aName        The name of the item.
   * @param aDisplayIcon The display icon or image representing the item.
   * @param aX           The X-coordinate position of the item.
   * @param aY           The Y-coordinate position of the item.
   */
  public Item(String aName, String aDisplayIcon, int aX, int aY) {
    name = aName;
    displayIcon = aDisplayIcon;
    x = aX;
    y = aY;
    estate = null;
  }

  /**
   * Sets the X-coordinate position of the item.
   *
   * @param aX The new X-coordinate position.
   * @return true if the X-coordinate was set successfully; otherwise, false.
   */
  public boolean setX(int aX) {
    x = aX;
    return true;
  }

  /**
   * Sets the Y-coordinate position of the item.
   *
   * @param aY The new Y-coordinate position.
   * @return true if the Y-coordinate was set successfully; otherwise, false.
   */
  public boolean setY(int aY) {
    y = aY;
    return true;
  }

  /**
   * Gets the name of the item.
   *
   * @return The name of the item.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the display icon or image representing the item.
   *
   * @return The display icon of the item.
   */
  public String getDisplayIcon() {
    return displayIcon;
  }

  /**
   * Gets the X-coordinate position of the item.
   *
   * @return The X-coordinate position of the item.
   */
  public int getX() {
    return x;
  }

  /**
   * Gets the Y-coordinate position of the item.
   *
   * @return The Y-coordinate position of the item.
   */
  public int getY() {
    return y;
  }

  /**
   * Sets the estate associated with this item.
   *
   * @param aEstate The estate to associate with the item.
   * @return true if the estate was set successfully; otherwise, false.
   */
  public boolean setEstate(Estate aEstate) {
    estate = aEstate;
    return true;
  }

  /**
   * Gets the estate associated with this item.
   *
   * @return The estate associated with the item.
   */
  public Estate getEstate() {
    return estate;
  }

  /**
   * Checks if the item is currently inside an estate.
   *
   * @return true if the item is inside an estate; otherwise, false.
   */
  public boolean isInEstate() {
    return (estate != null);
  }

  /**
   * Returns a string representation of the item, including its name, display icon, X and Y coordinates.
   *
   * @return A string representing the item.
   */
  public String toString() {
    return String.format("[name: %s | display icon: %s | x: %d | y: %d]", getName(), getDisplayIcon(), getX(), getY());
  }
}
