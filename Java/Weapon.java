/**
 * The `Weapon` class represents a weapon item in the game.
 * It extends the `Item` class, inheriting properties such as name, display icon, position, and estate association.
 * @author William Huang
 */
public class Weapon extends Item {

  /**
   * Constructor for the Weapon class.
   *
   * @param aName        The name of the weapon.
   * @param aDisplayIcon The display icon representing the weapon.
   * @param aX           The X-coordinate position of the weapon.
   * @param aY           The Y-coordinate position of the weapon.
   */
  public Weapon(String aName, String aDisplayIcon, int aX, int aY) {
    super(aName, aDisplayIcon, aX, aY);
  }
}