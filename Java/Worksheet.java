/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.32.1.6535.66c005ced modeling language!*/


import java.util.*;

// line 229 "model.ump"
// line 307 "model.ump"
public class Worksheet
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------
  
  private List<Card> shownCards;
  
  //Worksheet Associations
  private List<Card> cards;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Worksheet()
  {
    cards = new ArrayList<Card>();
    shownCards = new ArrayList<Card>();
  }

  public void addCard(Card card) {
    if (!cards.contains(card)) {
      cards.add(card);
    }
  }

  public void addShownCard(Card card) {
    if (!shownCards.contains(card)) {
      shownCards.add(card);
    }
  }

  public String toString() {
    String res = "\n/======MY CARDS=====/\n";
    for(Card c : cards) {
      res += String.format("%s card: %s\n", c.getType(), c.getName());
    }
    res += "/====SHOWN CARDS====/\n";
    if(!shownCards.isEmpty()) {
      for(Card c : shownCards) {
        res += String.format("%s card: %s\n", c.getType(), c.getName());
      }
    } else {
      res += ("I have not been shown any cards yet.\n"); 
    }
    res += "/===================/\n";
    return res;
  }
  
  public void printWorksheet() {
    System.out.println(this.toString());
  }
}