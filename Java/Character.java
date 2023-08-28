/**
 * The `Character` class represents a character in a game.
 * It extends the `Item` class, inheriting properties such as name, display icon, and position.
 */
public class Character extends Item {

  /**
   * Constructor for the Character class.
   *
   * @param aName         The name of the character.
   * @param aDisplayIcon  The display icon or image representing the character.
   * @param aX            The X-coordinate position of the character.
   * @param aY            The Y-coordinate position of the character.
   */
  public Character(String aName, String aDisplayIcon, int aX, int aY) {
    super(aName, aDisplayIcon, aX, aY);
  }
}