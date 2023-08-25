import java.util.*;
public class Player
{
  private List<Card> cards;
  private Character character;
  private Worksheet worksheet;
  private String name;
  private boolean isEligible;

  public Player(Character aCharacter, Worksheet aWorksheet, String aName, boolean aIsEligible)
  {
    cards = new ArrayList<Card>();
    character = aCharacter;
    worksheet = aWorksheet;
    name = aName;
    isEligible = aIsEligible;
  }

  public boolean addCard(Card aCard)
  {
    boolean wasAdded = false;
    wasAdded = cards.add(aCard);
    return wasAdded;
  }

  public boolean setIsEligible(boolean aIsEligible)
  {
    boolean wasSet = false;
    isEligible = aIsEligible;
    wasSet = true;
    return wasSet;
  }

  public Card getCard(int index)
  {
    Card aCard = cards.get(index);
    return aCard;
  }

  public Card[] getCards()
  {
    Card[] newCards = cards.toArray(new Card[cards.size()]);
    return newCards;
  }

  public int indexOfCard(Card aCard)
  {
    int index = cards.indexOf(aCard);
    return index;
  }

  /**
   * the character the player controls
   */
  public Character getCharacter()
  {
    return character;
  }

  /**
   * visual tool to display known cards to the player
   */
  public Worksheet getWorksheet()
  {
    return worksheet;
  }

  /**
   * the Player name (serves as ID)
   */
  public String getName()
  {
    return name;
  }

  /**
   * whether the player has used their solve attempt or not
   */
  public boolean getIsEligible()
  {
    return isEligible;
  }

   public void printHand(){
    System.out.print(String.format("%s's cards are:\n", getName()));
    for (Card card : getCards()) {
      System.out.println(card);
    }
  }

  public String toString()
  {
    String res = String.format("[character: %s | player name: %s | is eligible: %s]", getCharacter(), getName(), getIsEligible());
    if (getWorksheet() != null) {
      res += getWorksheet();
    }
    return res;
  }
}