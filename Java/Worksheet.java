import java.util.*;

/**
 * The `Worksheet` class represents a worksheet in the game, which holds known information for a given player.
 * It contains two lists: `cards` and `shownCards`, and provides methods for adding cards to these lists
 * and displaying the contents of the worksheet.
 *
 * @author William Huang
 */
public class Worksheet {

  /**
   * The list of cards in the worksheet.
   */
  private List<Card> cards;

  /**
   * The list of shown cards in the worksheet.
   */
  private List<Card> shownCards;

  /**
   * Constructor for the `Worksheet` class.
   * Initializes the `cards` and `shownCards` lists.
   */
  public Worksheet() {
    cards = new ArrayList<Card>();
    shownCards = new ArrayList<Card>();
  }

  /**
   * Adds a card to the list of cards in the worksheet.
   *
   * @param card The card to be added.
   */
  public void addCard(Card card) {
    if (!cards.contains(card)) {
      cards.add(card);
    }
  }

  /**
   * Adds a shown card to the list of shown cards in the worksheet.
   *
   * @param card The shown card to be added.
   */
  public void addShownCard(Card card) {
    if (!shownCards.contains(card)) {
      shownCards.add(card);
    }
  }

  /**
   * Returns a string representation of the worksheet, including the list of cards and shown cards.
   *
   * @return A string representation of the worksheet.
   */
  public String toString() {
    String res = "\n/======MY CARDS=====/\n";
    for (Card c : cards) {
      res += String.format("%s card: %s\n", c.getType(), c.getName());
    }
    res += "/====SHOWN CARDS====/\n";
    if (!shownCards.isEmpty()) {
      for (Card c : shownCards) {
        res += String.format("%s card: %s\n", c.getType(), c.getName());
      }
    } else {
      res += ("I have not been shown any cards yet.\n");
    }
    res += "/===================/\n";
    return res;
  }

  /**
   * Prints the contents of the worksheet to the console.
   */
  public void printWorksheet() {
    System.out.println(this.toString());
  }
}