import java.util.*;

/**
 * The model for a game of HobbyDetectives, and central hub of all game activity.
 * Contains all the necessary functions for game actions.
 * Intended for use by the Gui class, wherein many of the Game methods are
 * used in the Listeners, allowing for user control through the GUI.
 * Also contains all the necessary flags, counters, and other
 * variables for the game logic.
 * 
 * @author William Huang
 * @author James Goode
 * @author Finley Neilson
 */
public class Game {
    // Local variables for game logic
    public enum TurnOrder {Lucilla, Bert, Malina, Percy}
    public enum Direction {Up, Right, Down, Left}

    private int playerCount;
    private int playerInitCount = 0;
    private List<String> names = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();
    private List<Character> characters = new ArrayList<>(Arrays.asList(
        new Character("Lucilla", "L", 11, 1), 
        new Character("Bert", "B", 1, 9), 
        new Character("Malina", "M", 9, 22), 
        new Character("Percy", "P", 22, 11)));
    private List<Character> modifiableCharacterList = new ArrayList<>(characters);
    private Board board = new Board();
    private List<GameTile> usedGameTiles = new ArrayList<>();
    private List<String> currentGuess = new ArrayList<>();

    private Player turn = new Player(null,null,null,false);
    private TurnOrder currentTurn = TurnOrder.Lucilla;
    private int diceTotal = 0;
    private boolean diceRolled = false;
    private boolean guessMade = false;
    private int refuteCount = 0;
    private int refuter = 0;

    private String winner = "Nobody";

    // Getters and setters/adders
    public int getPlayerCount() {
        return this.playerCount;
    }
    
    public List<Character> getModifiableCharacterList(){
        return modifiableCharacterList;
    }
    
    public void removeFromModifiableCharacterList(int i){
        modifiableCharacterList.remove(i);
    }
    
    public void setPlayerCount(int i) {
        this.playerCount = i;
    }
    public int getPlayerInitCount() {
        return this.playerInitCount;
    }
    public void incrementPlayerInitCount() {
        this.playerInitCount++;
    }
    public List<String> getNames() {
        return this.names;
    }
    public void addName(String name) {
        this.names.add(name);
    }
    public List<Player> getPlayers() {
        return this.players;
    }
    public void addPlayer(Player player) {
        this.players.add(player);
    }
    public List<Card> getCards() {
        return this.cards;
    }
    public void addCard(Card card) {
        this.cards.add(card);
    }
    public List<Weapon> getWeapons() {
        return this.weapons;
    }
    public void addWeapon(Weapon weapon) {
        this.weapons.add(weapon);
    }
    public Board getBoard() {
        return this.board;
    }
    public List<String> getCurrentGuess() {
        return this.currentGuess;
    }
    public Player getCurrentPlayer() {
        return this.turn;
    }
    public int getDiceTotal() {
        return this.diceTotal;
    }
    public void decrementDiceTotal(int amount) {
        this.diceTotal -= amount;
    }
    public boolean getDiceRolled() {
        return this.diceRolled;
    }
    public void setDiceRolled(boolean rolled) {
        this.diceRolled = rolled;
    }
    public void setDiceTotal(int i) {
        this.diceTotal = i;
    }
    public boolean getGuessMade() {
        return this.guessMade;
    }
    public void setGuessMade(boolean guessed) {
        this.guessMade = guessed;
    }
    public int getRefuteCount() {
        return this.refuteCount;
    }
    public void setRefuteCount(int i) {
        this.refuteCount = i;
    }
    public void incrementRefuteCount() {
        this.refuteCount++;
    }
    public int getRefuter() {
        return this.refuter;
    }
    public void setRefuter(int i) {
        this.refuter = i;
    }
    public String getWinner() {
        return this.winner;
    }
    public void clearUsedTiles() {
        for (GameTile t : usedGameTiles) {
            t.clearStored();
        }
        this.usedGameTiles.clear();
    }
    public String findFullName(String shortName){
        for(Character c : characters){
            if(shortName.equals(c.getDisplayIcon())){
                return c.getName(); 
            }
        }
        for(Weapon w : weapons){
            if(shortName.equals(w.getDisplayIcon())){
                return w.getName(); 
            }
        }
        return null;
    }

    /**
     * Sets up the game by initializing the number of players, collecting player names,
     * determining starting player, allocating roles, and starting the game.
     */
    public void setupGame() {
       

        // get the unused character in 3 player scenario
        List<Player> orderedPlayers = new ArrayList<>();
        String nullCharacter = "";
        List<String> orderedCharacters = Arrays.asList("Lucilla","Bert","Malina","Percy");
        if(playerCount == 3){
            List<String> remainingCharacters = setPlayerString(orderedCharacters);
            nullCharacter = remainingCharacters.get(0);
        }

        // structure the characters
        for(int i = 0; i<4;i++){
            if(!nullCharacter.equals(orderedCharacters.get(i))){
                for(Player p : players){
                    if(p.getCharacter().getName().equals(orderedCharacters.get(i))){
                        orderedPlayers.add(p);
                    }
                }
            }
        }
        players = orderedPlayers;

        // Randomly decides starting player
        Random random = new Random();
        Player startingPlayer = null;

        while (startingPlayer == null) {
            int startingPlayerIndex = random.nextInt(playerCount);
            currentTurn = TurnOrder.values()[startingPlayerIndex];

            for (Player p : players) {
                // If valid starting player found
                if (Objects.equals(p.getCharacter().getName(), currentTurn.name())) {
                    startingPlayer = p;
                    break;
                }
            }
        }

        turn = startingPlayer;

        // Update the tiles that contain characters on the board
        for (Character c : characters) {
            Tile t = board.getTile(c.getY(), c.getX());
            if (t instanceof GameTile) {
                ((GameTile) t).setStored(c);
            }
        }

        // place weapons on board
        initialiseWeapons();

        // Set up and manage the cards
        initialiseCards();
        pickMurderCards();
        distributeCardsToPlayers();
    }

    private List<String> setPlayerString(List<String> orderedCharacters) {
        List<String> remainingCharacters = new ArrayList<>();
        remainingCharacters.add("Lucilla");
        remainingCharacters.add("Bert");
        remainingCharacters.add("Malina");
        remainingCharacters.add("Percy");

        for(int i = 0; i<3;i++){
            for(Player p : players){
                if(p.getCharacter().getName().equals(orderedCharacters.get(i))){
                    remainingCharacters.remove(orderedCharacters.get(i));
                }
            }
        }
        return remainingCharacters;
    }

    /**
     * Create and distribute all the weapons to random estates.
     */
    private void initialiseWeapons() {
        String[][] weaponData = {{"Broom", "b"}, {"Scissors", "s"}, {"Knife", "k"}, {"Shovel", "v"}, {"iPad", "i"}};
        for (String[] data : weaponData) {
            weapons.add(new Weapon(data[0], data[1], 0, 0));
        }
        List<Estate> estates = board.getEstates();
        for (Weapon weapon : weapons) {
            boolean isAdded = false;
            while (!isAdded) {
                int randomIndex = new Random().nextInt(estates.size());
                Estate selectedEstate = estates.get(randomIndex);
                if (selectedEstate.getItems().isEmpty()) {
                    addItemToEstate(weapon, selectedEstate);
                    isAdded = true;
                }
            }
        }
    }

    /**
     * Creates the game card instances.
     */
    private void initialiseCards() {
        String[][] cardData = {{
            "Lucilla", "Character"}, {"Bert", "Character"}, {"Malina", "Character"}, {"Percy", "Character"}, 
            {"Broom", "Weapon"}, {"Scissors", "Weapon"}, {"Knife", "Weapon"}, {"Shovel", "Weapon"}, {"iPad", "Weapon"}, 
            {"Haunted House", "Estate"}, {"Manic Manor", "Estate"}, {"Visitation Villa", "Estate"}, {"Calamity Castle", "Estate"}, {"Peril Palace", "Estate"}};

        for (String[] data : cardData) {
            cards.add(new Card(false, null, data[0], data[1]));
        }
    }

    /**
     * Picks cards from the game cards and notes them as the murder cards
     * Ensures that one of each type of card (character, weapon, estate) is a murder card.
     */
    private void pickMurderCards() {
        List<String> typesPickedForMurder = new ArrayList<>();
        while (typesPickedForMurder.size() != 3) {
            Random random = new Random();
            int randomIndex = random.nextInt(cards.size());
            if (!typesPickedForMurder.contains(cards.get(randomIndex).getType())) {
                cards.get(randomIndex).setIsMurder(true);
                typesPickedForMurder.add(cards.get(randomIndex).getType());
            }
        }
    }

    /**
     * Distributes the (non-murder) cards from the game among the players and updates their worksheets.
     * Uses the 'round-robin' style of dealing to ensure equally distributed cards.
     */
    private void distributeCardsToPlayers() {

        // Gets a list of the non-murder cards that'll be distributed to the players
        List<Card> nonMurderCards = new ArrayList<>(cards);
        nonMurderCards.removeIf(Card::getIsMurder);

        // Assigns those cards to players randomly
        Random random = new Random();
        while (!nonMurderCards.isEmpty()) {
            for (Player player : players) {
                if (nonMurderCards.isEmpty()) {
                    break;  // No need to continue if there are no more cards to distribute
                }

                int randomCardIndex = random.nextInt(nonMurderCards.size());
                Card cardToAssign = nonMurderCards.remove(randomCardIndex);

                player.addCard(cardToAssign);
                player.getWorksheet().addCard(cardToAssign);
                cardToAssign.setOwner(player);
            }
        }
    }

    /**
     * Handles player guesses.
     * Sets the input strings as part of the game's current guess.
     * Can also handle player solve attempts.
     * 
     * @param character the guessed character
     * @param weapon the guessed weapon
     * @param solve whether this is a solve attempt
     * 
     * @return 1 for a successful solve, 0 otherwise
     */
    public int guess(String character, String weapon, boolean solve) {
        guessMade = true;
        Player p = getCurrentPlayer();
        String estate = p.getCharacter().getEstate().getName();

        // updating the current guess 
        currentGuess = new ArrayList<String>();
        currentGuess.add(estate);
        currentGuess.add(character);
        currentGuess.add(weapon);

        // teleport the guessed character to the current estate
        for (Character c : characters) {
            if (c.getName().equals(character)) {
                teleportItem(c, p.getCharacter().getEstate());
            }              
        }

        // teleport the guessed weapon to the current estate
        for (Weapon w : weapons) {
            if (w.getName().equals(weapon)) {
                teleportItem(w, p.getCharacter().getEstate());
            }
        }

        // forfeiting all remaining player moves
        diceRolled = true;
        setDiceTotal(0);

        // determining if the player won (only if they made a solve attempt)
        if (solve) {
            if (didPlayerWin()) {
                winner = p.getName();
                return 1;
            }
        }

        return 0;
    }

    /**
     * Checks to see whether a player's guess was correct.
     * 
     * @return true if correct, false otherwise
     */
    private boolean didPlayerWin() {
        boolean win = true;
            for (Card c : cards) {
                if (c.getIsMurder()) {
                    switch (c.getType()) {
                        case "Estate" -> {
                            if (!c.getName().equals(currentGuess.get(0))) {
                                win = false;
                            }
                        }
                        case "Character" -> {
                            if (!c.getName().equals(currentGuess.get(1))) {
                                win = false;
                            }
                        }
                        case "Weapon" -> {
                            if (!c.getName().equals(currentGuess.get(2))) {
                                win = false;
                            }
                        }
                    }
                }
            }
        return win;
    }

    /**
     * A refuteable card is a card in the guess attempt that a player (the refuter) possesses.
     * Gets all refuteable cards of the refuter.
     * 
     * @param p the refuter
     * 
     * @return the list of all refuteable cards
     */
    public List<String> getRefuteableCards(Player p) {
        List<String> refuteableCards = new ArrayList<>();
        for (Card c : p.getCards()) {
            if (currentGuess.contains(c.getName())) {
                refuteableCards.add(c.getName());
            }
        }
        return refuteableCards;
    }

    /**
     * Method to randomly return a number 1-6.
     * 
     * @return the outcome of rolling the dice
     */
    public int rollDice() {
        double max = 6;
        double min = 1;
        return (int) (Math.random() * (max - min + 1) + min);
    }

    /**
     * Adds an Item to an Estate, ensuring that this action is properly
     * reflected in the fields of the Item and Estate involved.
     * 
     * @param item the Item being added
     * @param estate the Estate being added to
     */
    private void addItemToEstate(Item item, Estate estate) {
        estate.addItem(item);
        estate.updateContents();
        item.setEstate(estate);
    }

    /**
     * Removes an Item from an Estate, ensuring that this action is properly
     * reflected in the fields of the Item and Estate involved.
     * 
     * @param item the Item being removed
     * @param estate the Estate being removed from
     */
    private void removeItemFromEstate(Item item, Estate estate) {
        estate.removeItem(item);
        estate.updateContents();
        item.setEstate(null);
    }

    /**
     * Quick transport of Items into Estates.
     * Mainly used to teleport Characters and Weapons into an Estate
     * when they have been guessed by a player in that Estate.
     * 
     * @param item the Item being transported
     * @param toEstate the Estate being transported to
     */
    private void teleportItem(Item item, Estate toEstate) {
        GameTile current = (GameTile) board.getTile(item.getY(), item.getX());
        current.clearStored();
        Estate fromEstate = item.getEstate();
        if(fromEstate != null){
            removeItemFromEstate(item, fromEstate);
        }
        addItemToEstate(item, toEstate);
    }

    /**
     * Controls player movement action of their Character into an Estate.
     * Because the inside of an Estate can be treated as a single movement space,
     * this movement is instead treated as the addition of a Character into
     * the contents of the Estate.
     * 
     * @param character the character being moved
     * @param estate the estate being moved into
     * @return 1 if the movement successfully occurred, 0 otherwise
     */
    public int moveCharToEstate(Character character, Estate estate) {
        if(character.getEstate() != estate){
            estate.addItem(character);
            estate.updateContents();
            character.setEstate(estate);
            GameTile current = (GameTile) board.getTile(character.getY(), character.getX());
            current.setStored(new Item("Used", "+", character.getX(), character.getY()));
            usedGameTiles.add(current);
            return 1;
        }
        return 0;
    }

    /**
     * Controls player movement action of their Character out of an Estate.
     * Because the inside of an Estate can be treated as a single movement space,
     * this movement is instead treated as the removal of a Character from
     * the contents of the Estate.
     * Additionally, the Character must be moved to the Estate's dedicated exit point,
     * otherwise, glitching through the EntraceTile and free movement within the Estate's
     * Tiles can occur.
     * 
     * @param character the character being moved
     * @param estate the estate being moved out of
     * @return 1 if the movement successfully occurred, 0 otherwise
     */
    public int moveCharOutOfEstate(Character character, EntranceTile exit) {
        if (board.isSafeMove(exit.getExitY(), exit.getExitX())) {
            GameTile next = (GameTile) board.getTile(exit.getExitY(), exit.getExitX());
            next.setStored(character);
            character.setX(exit.getExitX());
            character.setY(exit.getExitY());
            exit.getEstate().removeItem(character);
            exit.getEstate().updateContents();
            character.setEstate(null);
            return 1;
        }
        return 0;
    }

    /**
     * Movement of a character. This is done by updating the 
     * coordinates of the character.
     * 
     * @param character the character being moved
     * @param newY the new Y coordinate
     * @param newX the new X coordinate
     * 
     * @return 1 if the movement successfully occurred, 0 otherwise
     */
    private int moveChar(Character character, int newY, int newX) {
        if (board.isSafeMove(newY, newX)) {
            GameTile next = (GameTile) board.getTile(newY, newX);
            next.setStored(character);
            GameTile current = (GameTile) board.getTile(character.getY(), character.getX());
            current.setStored(new Item("Used", "+", character.getX(), character.getY()));
            usedGameTiles.add(current);
            character.setX(newX);
            character.setY(newY);
            return 1;
        }
        return 0;
    }

    /**
     * Directional movement of a character.
     * Uses the moveChar method but only to a fixed number of Tiles - 
     * the Tiles immediately adjacent to the character.
     * 
     * @param character the character being moved
     * @param directionthe direction being moved in
     * 
     * @return 1 if the movement successfully occurred, 0 otherwise
     */
    public int moveInDirection(Character character, Direction direction) {

        // Find coordinates of next direction, or which exit to take
        int newY = character.getY();
        int newX = character.getX();
        int exit = switch (direction) {
            case Up -> {
                newY -= 1;
                yield 0;
            }
            case Down -> {
                newY += 1;
                yield 2;
            }
            case Left -> {
                newX -= 1;
                yield 3;
            }
            case Right -> {
                newX += 1;
                yield 1;
            }
        };

        // If in estate, leave
        if (character.getEstate() != null) {
            return moveCharOutOfEstate(character, character.getEstate().getEntranceTiles().get(exit));
        }

        if(board.isSafeMove(newY,newX)){
            Tile next = board.getTile(newY, newX);

            // Check for estate entrance
            if (next instanceof EntranceTile) {
                return moveCharToEstate(character, ((EntranceTile) next).getEstate());
            }

            return moveChar(character, newY, newX);
        }
        return 0;
    }

    /**
     * Updates the turn order of the game.
     */
    public void updateTurn() {
        int index = players.indexOf(turn);
            index++;
            if(index == players.size()){
                index = 0;
            }
        switch (index) {
            case 1 -> {
                currentTurn = TurnOrder.Bert;
                turn = players.get(index);
            }
            case 2 -> {
                currentTurn = TurnOrder.Malina;
                turn = players.get(index);
            }
            case 3 -> {
                currentTurn = TurnOrder.Percy;
                turn = players.get(index);
            }
            case 0 -> {
                currentTurn = TurnOrder.Lucilla;
                turn = players.get(index);
            }
        }
    }
}
