import java.util.*;

/**
 * The Player class represents a player in the game.
 *
 * @author William Huang
 */
public class Player {

  /**
   * The list of cards held by the player.
   */
  private List<Card> cards;

  /**
   * The character controlled by the player.
   */
  private Character character;

  /**
   * A worksheet for displaying known cards to the player.
   */
  private Worksheet worksheet;

  /**
   * The player's name, serving as their ID.
   */
  private String name;

  /**
   * Flag indicating whether the player is eligible (has not used their solve attempt).
   */
  private boolean isEligible;

  /**
   * Constructor for the `Player` class.
   *
   * @param aCharacter   The character controlled by the player.
   * @param aWorksheet   A worksheet for displaying known cards to the player.
   * @param aName        The player's name, serving as their ID.
   * @param aIsEligible  Flag indicating whether the player is eligible (has not used their solve attempt).
   */
  public Player(Character aCharacter, Worksheet aWorksheet, String aName, boolean aIsEligible) {
    cards = new ArrayList<Card>();
    character = aCharacter;
    worksheet = aWorksheet;
    name = aName;
    isEligible = aIsEligible;
  }

  /**
   * Adds a card to the player's hand.
   *
   * @param aCard The card to be added.
   * @return True if the card was added successfully, false otherwise.
   */
  public boolean addCard(Card aCard) {
    return cards.add(aCard);
  }

  /**
   * Sets the player's eligibility status.
   *
   * @param aIsEligible Flag indicating whether the player is eligible.
   * @return True if the eligibility status was set successfully, false otherwise.
   */
  public boolean setIsEligible(boolean aIsEligible) {
    isEligible = aIsEligible;
    return true;
  }

  /**
   * Gets a card from the player's hand at the specified index.
   *
   * @param index The index of the card to retrieve.
   * @return The card at the specified index.
   */
  public Card getCard(int index) {
    return cards.get(index);
  }

  /**
   * Gets an array of all the cards in the player's hand.
   *
   * @return An array of cards in the player's hand.
   */
  public Card[] getCards() {
    return cards.toArray(new Card[cards.size()]);
  }

  /**
   * Finds the index of a specific card in the player's hand.
   *
   * @param aCard The card to search for.
   * @return The index of the card if found, or -1 if not found.
   */
  public int indexOfCard(Card aCard) {
    return cards.indexOf(aCard);
  }

  /**
   * Gets the character controlled by the player.
   *
   * @return The character controlled by the player.
   */
  public Character getCharacter() {
    return character;
  }

  /**
   * Gets the player's worksheet for displaying known cards.
   *
   * @return The player's worksheet.
   */
  public Worksheet getWorksheet() {
    return worksheet;
  }

  /**
   * Gets the player's name.
   *
   * @return The player's name.
   */
  public String getName() {
    return name;
  }

  /**
   * Checks whether the player is eligible (has not used their solve attempt).
   *
   * @return True if the player is eligible, false otherwise.
   */
  public boolean getIsEligible() {
    return isEligible;
  }

  /**
   * Prints the player's hand, displaying their held cards.
   */
  public void printHand() {
    System.out.print(String.format("%s's cards are:\n", getName()));
    for (Card card : getCards()) {
      System.out.println(card);
    }
  }

  /**
   * Returns a string representation of the player's information.
   *
   * @return A string representation of the player.
   */
  public String toString() {
    String res = String.format("[character: %s | player name: %s | is eligible: %s]", getCharacter(), getName(), getIsEligible());
    if (getWorksheet() != null) {
      res += getWorksheet();
    }
    return res;
  }
}