/**
 * The Card class represents a card in Hobby Detective.
 * It can be a murder card or a regular card, and can be either an estate card, a weapon card, or a character card.
 *
 * @author William Huang
 */
public class Card {

    /**
     * Indicates whether this card is one of the three murder cards.
     */
    private boolean isMurder;

    /**
     * The player who holds this card.
     */
    private Player owner;

    /**
     * The name of the card.
     */
    private final String name;

    /**
     * The type of the card (character, estate, weapon).
     */
    private final String type;

    /**
     * Constructor for the Card class.
     *
     * @param aIsMurder Indicates whether this card is a murder card.
     * @param aOwner    The player who owns this card.
     * @param aName     The name of the card.
     * @param aType     The type or category of the card.
     */
    public Card(boolean aIsMurder, Player aOwner, String aName, String aType) {
        isMurder = aIsMurder;
        owner = aOwner;
        name = aName;
        type = aType;
    }

    /**
     * Sets the owner of the card.
     *
     * @param aOwner The player who will be the new owner of the card.
     */
    public void setOwner(Player aOwner) {
        owner = aOwner;
    }

    /**
     * Sets whether this card is one of the three murder cards.
     *
     * @param aIsMurder true if this card is a murder card; otherwise, false.
     */
    public void setIsMurder(boolean aIsMurder) {
        isMurder = aIsMurder;
    }

    /**
     * Checks if this card is one of the three murder cards.
     *
     * @return true if this card is a murder card; otherwise, false.
     */
    public boolean getIsMurder() {
        return isMurder;
    }

    /**
     * Gets the player who holds this card.
     *
     * @return The player who owns this card.
     */
    public Player getOwner() {
        return owner;
    }

    /**
     * Gets the name of the card.
     *
     * @return The name of the card.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the type or category of the card.
     *
     * @return The type of the card.
     */
    public String getType() {
        return type;
    }

    /**
     * Returns a string representation of the card, including its name, type, owner, and murder status.
     *
     * @return A string representing the card.
     */
    public String toString() {
        return String.format("[name: %s | type: %s | owner: %s | murder: %s]", getName(), getType(), getOwner(), getIsMurder());
    }
}
