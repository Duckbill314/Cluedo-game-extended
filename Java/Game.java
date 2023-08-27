import java.util.*;

public class Game {
    public enum TurnOrder {Lucilla, Bert, Malina, Percy}
    public enum Direction {Up, Right, Down, Left}

    private int playerCount;
    private int playerInitCount = 0;
    private List<String> names = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();

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
    private TurnOrder invalidCharacter;

    private String winner = "Nobody";

    public Game() {}

    // GETTERS AND SETTERS/ADDERS
    public int getPlayerCount() {
        return this.playerCount;
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

    /**
     * Sets up the game by initializing the number of players, collecting player names,
     * determining starting player, allocating roles, and starting the game
     */
    public void setupGame() {
        // making the players
        assignCharacters(names);

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
        do {
            int startingPlayerIndex = random.nextInt(playerCount);
            currentTurn = TurnOrder.values()[startingPlayerIndex];
        } while (currentTurn == invalidCharacter);
        for (Player p : players) {
            if (Objects.equals(p.getCharacter().getName(), currentTurn.name())) {
                turn = p;
            }
        }

        // Update the tiles that contain characters on the board
        for (Player p : players) {
            Tile t = board.getTile(p.getCharacter().getY(), p.getCharacter().getX());
            if (t instanceof GameTile) {
                ((GameTile) t).setStored(p.getCharacter());
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
     * Assigns each player a character randomly (must be called after makeCards())
     * @param names The list of player names
     */
    private void assignCharacters(List<String> names) {
        List<Character> availableCharacters = new ArrayList<>(Arrays.asList(new Character("Lucilla", "L", 11, 1), new Character("Bert", "B", 1, 9), new Character("Malina", "M", 9, 22), new Character("Percy", "P", 22, 11)));
        
        // Creates the player objects and randomly assigns them a character
        for (int i = 0; i < playerCount; i++) {
            int randomIndex = new Random().nextInt(availableCharacters.size());
            Character character = availableCharacters.remove(randomIndex);
            players.add(new Player(character, new Worksheet(), names.get(i), true));
        }

        // Reorders the players list according to the sequence L->B->M->P
        List<Player> orderedPlayers = new ArrayList<>();
        for (TurnOrder character : TurnOrder.values()) {
            for (Player player : players) {
                if (player.getCharacter().getName().equals(character.toString())) {
                    orderedPlayers.add(player);
                    break;
                }
            }
        }
        players = orderedPlayers;

        // Finds the TurnOrder enum value for the invalid character (if playing with only 3 characters)
        if(playerCount == 3) {

            for (TurnOrder character : TurnOrder.values()) {
                boolean characterFound = false;

                // Check each player to see if one of them has this character
                for (Player player : players) {
                    if (player.getCharacter().getName().equals(character.toString())) {
                        characterFound = true;
                        break;
                    }
                }

                // If no players have this character, it's the invalid character
                if (!characterFound) {
                    invalidCharacter = character;
                    break;
                }
            }
        }
    }

    /**
     * Create and distribute all the weapons to random estates
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
     * Creates the game card instances
     */
    private void initialiseCards() {
        String[][] cardData = {{"Lucilla", "Character"}, {"Bert", "Character"}, {"Malina", "Character"}, {"Percy", "Character"}, {"Broom", "Weapon"}, {"Scissors", "Weapon"}, {"Knife", "Weapon"}, {"Shovel", "Weapon"}, {"iPad", "Weapon"}, {"Haunted House", "Estate"}, {"Manic Manor", "Estate"}, {"Visitation Villa", "Estate"}, {"Calamity Castle", "Estate"}, {"Peril Palace", "Estate"}};

        for (String[] data : cardData) {
            cards.add(new Card(false, null, data[0], data[1]));
        }
    }

    /**
     * Picks cards from the game cards and notes them as the murder cards
     * Ensures that one of each type of card (character, weapon, estate) is a murder card
     */
    private void pickMurderCards() {
        List<String> typesPickedForMurder = new ArrayList<>();
        System.out.println();
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
     * Distributes the (non-murder) cards from the game among the players and updates their worksheets
     * Uses the 'round-robin' style of dealing to ensure equally distributed cards
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
     * let the player do a guess (assumed check for in a estate has already passed)
     * return 1 for a successful solve, 0 otherwise
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
        for (Player player : players) {
            if (player.getCharacter().getName().equals(character)) {
                teleportItem(player.getCharacter(), p.getCharacter().getEstate());
            }              
        }

        // teleport the guessed weapon to the current estate
        for (Weapon w : weapons) {
            if (w.getName().equals(weapon)) {
                teleportItem(w, p.getCharacter().getEstate());
            }
        }

        // forfeiting all remaining player moves
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
     * Checks to see whether a player's guess was correct
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
     * Method to randomly return a number 1-6
     */
    public int rollDice() {
        double max = 6;
        double min = 1;
        return (int) (Math.random() * (max - min + 1) + min);
    }

    private void addItemToEstate(Item item, Estate estate) {
        estate.addItem(item);
        estate.updateContents();
        item.setEstate(estate);
    }

    private void removeItemFromEstate(Item item, Estate estate) {
        estate.removeItem(item);
        estate.updateContents();
    }

    private void teleportItem(Item item, Estate toEstate) {
        Estate fromEstate = item.getEstate();
        if(fromEstate != null){
            removeItemFromEstate(item, fromEstate);
        }
        addItemToEstate(item, toEstate);
    }

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


    // OLD GAME LOGIC MANAGER, DEFUNCT NOW

    /* 
    public void gameManager(Scanner scanner) {
        String input = "0";
        while (isInProgress) {
            if (turn.getIsEligible()) {
                boolean diceRolled = false;
                while (diceTotal != 0 || !diceRolled) {
                    System.out.print('\u000C');
                    board.draw();
                    //work sheet print
                    turn.getWorksheet().printWorksheet();
                    displayLocations();

                    //normal movement or action
                    if (diceRolled && !input.equals("2")) {

                        System.out.println("You have " + diceTotal + " moves remaining!");
                        System.out.println("Do you want to move your character or perform an action?");
                        System.out.println();

                        System.out.println("Enter 1 to open the movement menu.");
                        System.out.println("Enter 2 to perform an action.");

                        input = scanner.nextLine();
                        if (input.equals("1")) {
                            takePlayerInput(scanner, turn);
                            input = "0";
                        }
                        if (!input.equals("0")) {
                            System.out.print('\u000C');
                            board.draw();
                            //work sheet print
                            turn.getWorksheet().printWorksheet();
                            displayLocations();
                        }
                    }

                    //roll dice since dice has not been rolled yet
                    if (input.equals("1") && !diceRolled) {
                        int dice1 = rollDice();
                        int dice2 = rollDice();
                        diceTotal = dice1 + dice2;
                        diceRolled = true;
                    }

                    if (input.equals("2")) {//open action menu

                        System.out.println("What action do you want to do?");
                        System.out.println();

                        System.out.println("Enter 1 to make a guess.");
                        System.out.println("Enter 2 to make a solve attempt.");
                        System.out.println("Enter 3 to toggle grid on / grid off.");
                        System.out.println("Enter 4 to return to the previous menu.");
                        System.out.print("Enter number here: ");

                        input = scanner.nextLine();

                        if (input.equals("1") && turn.getCharacter().getEstate() != null) {
                            int i = guess(turn, scanner);
                            if(i != 2){
                                diceRolled = true;
                            }
                        }
                        if (input.equals("2") && turn.getCharacter().getEstate() != null) {
                            int i = guess(turn, scanner);
                            if (i == 1) {
                                System.out.print('\u000C');
                                System.out.println(turn.getName() + " guessed correctly!");
                                System.out.println(turn.getName() + " Wins the game!");
                                System.out.println("\ngame will end in 30 seconds");
                                try {
                                    Thread.sleep(30000);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                                isInProgress = false;
                            } else if (i == 0) {
                                turn.setIsEligible(false);
                            }
                        }
                        if (input.equals("3")) {
                            board.gridOn();
                        }
                        if (input.equals("4")) {
                            input = "0";
                        } else {
                            input = "2";
                        }

                    } else if (!diceRolled) {//default role dice or action assuming dice have not been rolled yet
                        System.out.println("Do you want to roll your dice or do an action?");
                        System.out.println();

                        System.out.println("Enter 1 to roll your dice.");
                        System.out.println("Enter 2 to do an action.");

                        input = scanner.nextLine();
                    }

                }
            }
            // tun update
            int index = players.indexOf(turn);
            index++;
            if(index == players.size()){
                index = 0;
            }
            switch (index) {
                case 1:
                    currentTurn = TurnOrder.Bert;
                    turn = players.get(index);
                    break;
                case 2:
                    currentTurn = TurnOrder.Malina;
                    turn = players.get(index);
                    break;
                case 3:
                    currentTurn = TurnOrder.Percy;
                    turn = players.get(index);
                    break;
                case 0:
                    currentTurn = TurnOrder.Lucilla;
                    turn = players.get(index);
                    break;
            }
            for (GameTile t : usedGameTiles) {
                t.setStored(null);
            }
            boolean lose = true;
            for(Player p : players){
                if(p.getIsEligible()){
                    lose = false;
                }
            }
            while (!input.equals("1")) {
                System.out.print('\u000C');
                if(lose){
                    System.out.println("Game is over! All players failed to guess the murderer incorrectly.");
                    System.out.println("Restart game?\n");
                    System.out.println("Enter 1 for yes.");
                }
                else{
                    board.draw();
                    System.out.println("Turn is over! It is now " + turn.getName() + "'s turn.\n");
                    System.out.println("Begin " + turn.getName() + "'s turn?\n");
                    System.out.println("Enter 1 for yes.");
                }
                input = scanner.nextLine();
            }
            input = "0";
        }
    } */
}
