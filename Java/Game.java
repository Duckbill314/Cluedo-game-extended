import java.util.Scanner;
import java.util.*;

public class Game {
    public boolean isInProgress = true;

    public enum TurnOrder {Lucilla, Bert, Malina, Percy}
    public enum Direction {Up, Right, Down, Left}

    private TurnOrder currentTurn = TurnOrder.Lucilla;
    private int diceTotal = 0;
    private Board board = new Board();
    private Player turn = new Player(null,null,null,false);
    private int playerCount;
    private TurnOrder invalidCharacter;

    private List<Player> players = new ArrayList<>();
    private final List<Card> cards = new ArrayList<>();
    private List<GameTile> usedGameTiles = new ArrayList<>();
    private List<Weapon> weapons = new ArrayList<>();

    /**
     * Clears the console screen
     */
    private void clearConsole() {
        System.out.print('\u000C');
    }

    /**
     * Displays the menu of options to the user and requests that they choose a valid option by number
     * @param scanner The scanner object to get user input
     * @param prompt The prompt to display before the choices
     * @param options The list of choices to display
     * @return the selected option (as an int)
     */
    private int promptUserForChoice(Scanner scanner, String prompt, List<String> options) {

        // Prints the prompt
        System.out.println();
        System.out.println();
        System.out.println("/===================/");
        System.out.println();
        System.out.println(prompt);
        System.out.println();
        System.out.println("/===================/");
        System.out.println();

        // Prints the options
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  - " + (i + 1) + ". " + options.get(i));
        }

        // Receives the selected option from the user
        System.out.println();
        System.out.println();

        int choice = -1;
        boolean validChoice = false;

        while (!validChoice) {
            try {
                System.out.print("Enter your choice: ");
                choice = Integer.parseInt(scanner.nextLine());

                if (choice >= 1 && choice <= options.size()) {
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice. Please enter a valid number.");
                }
            } catch (Exception e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        return choice;

    }

    /**
     * Ends the game session
     * @throws InterruptedException if the sleep is interrupted
     */
    private void endGame() {
        isInProgress = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Game(Scanner scanner) {
        board.draw();

        System.out.print('\u000C');
        System.out.println("Welcome to Hobby Detectives!");

        switch (promptUserForChoice(scanner, "Do you want to start the game?",  Arrays.asList("Yes", "No"))) {
            case 1:
                setupGame(scanner);
                gameManager(scanner);
            case 2:
                System.out.print("Game aborted. Goodbye!");
                endGame();
        }
    }

    /**
     * Collects the required number of player names for the game
     * @param scanner The scanner used for user input
     * @return A list of the collected player names
     */
    private List<String> collectPlayerNames(Scanner scanner) {
        List<String> names = new ArrayList<>();

        while (playerCount != names.size()) {

            clearConsole();

            System.out.println("Name allocation : ");
            for (String n : names) {
                System.out.println(n);
            }

            System.out.print("Player " + (names.size() + 1) + ", please enter your name: ");
            String name = scanner.nextLine();

            if (name.length() > 15) {
                System.out.println("Sorry, your name can't exceed 15 characters");
            } else {
                names.add(name);
            }
        }

        return names;
    }

    /**
     * Sets up the game by initializing the number of players, collecting player names,
     * determining starting player, allocating roles, and starting the game
     * @param scanner The scanner used for user input
     */
    private void setupGame(Scanner scanner) {

        // Prompts user for number of players (prompt will return 1 or 2 for 3 players or 4 players respectively)
        playerCount = 2 +  promptUserForChoice(scanner, "Please select the number of players", Arrays.asList("Three players", "Four players"));


        // Prompt users for their names
        List<String> names = collectPlayerNames(scanner);

        // making the players
        assignCharacters(names);
        // get the unused character in 3 player scenario
        List<Player> orderedPlayers = new ArrayList<>();
        String nullCharacter = "";
        List<String> orderedCharacters = Arrays.asList("Lucilla","Bert","Malina","Percy");
        if(playerCount == 3){

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

            nullCharacter =  remainingCharacters.get(0);
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

        clearConsole();
        System.out.println("Allocating roles for " + playerCount + " players.");
        System.out.println();

        for (Player p : players) {
            System.out.println(p.getName() + " will be playing as " + p.getCharacter().getName());
            if (p.getCharacter().getName() == currentTurn.name()) {
                turn = p;
            }
        }

        System.out.println();
        System.out.println(currentTurn.name() + " will be starting first, please pass the tablet to " + turn.getCharacter().getName() + ".\n");

        promptUserForChoice(scanner, "Begin the first round?", Arrays.asList("Yes"));

        System.out.print('\u000C');

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

    // JAMES' WORKING CODE

    /**
     * Assigns each player a character randomly (must be called after makeCards())
     * @param names The list of player names
     */
    // line 85 "model.ump"
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

    public void displayLocations() {
        //locational infomation
        System.out.println("Location information:");
        for (Player p : players) {
            String s = " is on the main board";
            if (p.getCharacter().getEstate() != null) {
                s = " is in " + p.getCharacter().getEstate().getName();
            }
            System.out.println(p.getCharacter().getName() + s);
        }
        System.out.println("");
    }

    /**
     * let the player do a guess (assumed check for in a estate has already passed)
     * 0 for failed guess and 1 for correct guess
     */
    // line 66 "model.ump"
    private int guess(Player p, Scanner scanner) {
        //guess UI

        String input = "";
        boolean characterSelected = false;
        boolean weaponSelected = false;
        String character = "";
        String weapon = "";

        while (!characterSelected) {
            System.out.print('\u000C');
            System.out.println("WARNING, by guessing you forfeit any remaining moves you have. Your turn will end once you guess.\n");
            System.out.println("Estate: " + p.getCharacter().getEstate().getName());
            System.out.println("\nSelect a Character to guess.\n");

            System.out.println("Enter 1 to select Lucilla.");
            System.out.println("Enter 2 to select Bert.");
            System.out.println("Enter 3 to select Malina.");
            System.out.println("Enter 4 to select Percy.");

            System.out.println("\nEnter 5 to cancel this guess.");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    character = "Lucilla";
                    break;
                case "2":
                    character = "Bert";
                    break;
                case "3":
                    character = "Malina";
                    break;
                case "4":
                    character = "Percy";
                    break;
                case "5":
                    return 2;
            }
            if (!character.equals("")) {
                characterSelected = true;
            }
        }
        while (!weaponSelected) {
            System.out.print('\u000C');
            System.out.println("Estate: " + p.getCharacter().getEstate().getName());
            System.out.println("Character: " + character);
            System.out.println("\nSelect a Weapon to guess.\n");

            System.out.println("Enter 1 to select Broom.");
            System.out.println("Enter 2 to select Scissors.");
            System.out.println("Enter 3 to select Knife.");
            System.out.println("Enter 4 to select Shovel.");
            System.out.println("Enter 5 to select iPad.");

            System.out.println("\nEnter 6 to cancel this guess.");
            input = scanner.nextLine();

            switch (input) {
                case "1":
                    weapon = "Broom";
                    break;
                case "2":
                    weapon = "Scissors";
                    break;
                case "3":
                    weapon = "Knife";
                    break;
                case "4":
                    weapon = "Shovel";
                    break;
                case "5":
                    weapon = "iPad";
                    break;
                case "6":
                    return 2;
            }
            if (!weapon.equals("")) {
                weaponSelected = true;
            }
        }
        input = "0";
        while (!input.equals("1") || !input.equals("2")) {
            System.out.print('\u000C');
            System.out.println("Estate: " + p.getCharacter().getEstate().getName());
            System.out.println("Character: " + character);
            System.out.println("Weapon: " + weapon);
            System.out.println("\nGuess: " + character + " commited the murder in the " + p.getCharacter().getEstate().getName() + " with the " + weapon.toLowerCase() + ".\n");
            System.out.println("place guess?\n");
            System.out.println("Enter 1 for yes.");
            System.out.println("Enter 2 to cancel this guess.");
            input = scanner.nextLine();
            switch (input) {

                case "1":
                    // teleport the guessed character to the current estate
                    for(Player player : players){
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
                    
                    diceTotal = 0;
                    // if win (only works on final guesses)
                    boolean win = true;
                    for (Card c : cards) {
                        if (c.getIsMurder()) {
                            switch (c.getType()) {
                                case "Estate":
                                    if (!c.getName().equals(p.getCharacter().getEstate().getName())) {
                                        win = false;
                                    }
                                    break;
                                case "Character":
                                    if (!c.getName().equals(character)) {
                                        win = false;
                                    }
                                    break;
                                case "Weapon":
                                    if (!c.getName().equals(weapon)) {
                                        win = false;
                                    }
                                    break;
                            }
                        }
                    }
                    if (win) {
                        return 1;
                    }
                    // first, we need to find the ordering of player turns at the moment, dont modify the enum beacause these are not real turns
                    int turn = 0;
                    input = "0";
                    String cardName = "";
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).getCharacter().getName().equals(p.getCharacter().getName())) {
                            turn = i;
                        }
                    }
                    // then we need to pass this guess onto the refute method for the next 3 players
                    // but only if the next player actually has a card in the guess
                    for (int i = 0; i < 3; i++) {
                        int playerId = i + turn + 1;
                        if (playerId > 3) {
                            playerId = playerId - 4;
                        }

                        for (Card c : players.get(playerId).getCards()) {
                            if (c.getName().equals(weapon) || c.getName().equals(character) || c.getName().equals(p.getCharacter().getEstate().getName())) {
                                cardName = refute(players.get(playerId), p, character, weapon, scanner);
                                input = "0";
                                while (!input.equals("1")) {
                                    System.out.print('\u000C');
                                    System.out.println(players.get(playerId).getName() + " revealed the " + cardName + " card was in their hand.\n");
                                    p.getWorksheet().addShownCard(c);
                                    System.out.println("Please pass the tablet back to " + p.getName());
                                    System.out.println("\nEnter 1 to continue.");
                                    input = scanner.nextLine();
                                }
                                return 0;
                            }
                        }

                    }
                    System.out.print('\u000C');
                    input = "0";
                    while (!input.equals("1")) {
                        System.out.print('\u000C');
                        System.out.println("No other player is holding any of those cards.");
                        System.out.println("\nEnter 1 to continue.");
                        input = scanner.nextLine();
                    }
                    return 0;
                case "2":
                    return 2;
            }
        }
        return 0;
    }

    private String refute(Player p, Player guesser, String character, String weapon, Scanner scanner) {
        String input = "0";

        while (!input.equals("1")) {
            System.out.print('\u000C');
            System.out.println(guesser.getName() + " has called a guess!\n");
            //System.out.println("The guess was : "+character+" commited the murder in the "+p.getCharacter().getEstate().getName()+" with the "+weapon.toLowerCase()+".\n");
            System.out.println("Pass the tablet to " + p.getName() + " so they can refute\n");
            System.out.println("Enter 1 to start your refute.");
            input = scanner.nextLine();
        }
        input = "0";
        System.out.print('\u000C');
        p.getWorksheet().printWorksheet();
        System.out.println("The guess was: " + character + " commited the murder in the " + guesser.getCharacter().getEstate().getName() + " with the " + weapon.toLowerCase() + ".\n");
        int count = 1;
        List<String> refuteableCards = new ArrayList<>();
        for (Card c : p.getCards()) {
            if (c.getName().equals(weapon) || c.getName().equals(character) || c.getName().equals(guesser.getCharacter().getEstate().getName())) {

                System.out.println("Enter " + count + " to refute with " + c.getName() + ".");
                refuteableCards.add(c.getName());
                count++;
            }
        }
        boolean enteredValidNumber = false;
        while (!enteredValidNumber) {
            input = scanner.nextLine();
            for (int i = 0; i < refuteableCards.size(); i++) {
                if (i + 1 == Integer.parseInt(input)) {
                    enteredValidNumber = true;
                }
            }
        }
        return refuteableCards.get(Integer.parseInt(input) - 1);
    }

    /**
     * Method to randomly return a number 1-6
     */
    private int rollDice() {
        System.out.println("Rolling ");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.print(". ");
        }
        double max = 6;
        double min = 1;
        int dice = (int) (Math.random() * (max - min + 1) + min); // implement random number 1-6 to simulate dice roll
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("\nROLLED " + dice);
        return dice;
    }

    // END OF JAMES' WORKING CODE

    /**
     * Contains the primary gameLoop mechanics. Fails and returns 0 if error is detected, 1 if the game concludes successfully
     * Is the central hub for method calling and contains the main loop.
     */

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
    }

    // WILL'S CODE 

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

    private int moveInDirection(Character character, Direction direction) {

        // Find coordinates of next direction, or which exit to take
        int newY = character.getY();
        int newX = character.getX();
        int exit = 0;
        switch (direction) {
            case Up:
                newY -= 1;
                exit = 0;
                break;
            case Down:
                newY += 1;
                exit = 2;
                break;
            case Left:
                newX -= 1;
                exit = 3;
                break;
            case Right:
                newX += 1;
                exit = 1;
                break;
        }

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
     * Method to get the next player input for movement.
     */
    private void takePlayerInput(Scanner scanner, Player p) {

        Direction dir;
        int choice;
        while (diceTotal > 0) {
            clearConsole();
            board.draw();
            p.getWorksheet().printWorksheet();
            displayLocations();

            System.out.println(String.format("You have %d moves remaining. You are playing as %s (%s).", diceTotal, p.getCharacter().getName(), p.getCharacter().getDisplayIcon()));

            // Requests input from user
            List<String> directionOptions = Arrays.asList("Move Up", "Move Right", "Move Down", "Move Left", "Return to Previous Menu");
            choice = promptUserForChoice(scanner, "What direction will you move?\n", directionOptions);

            // Cancels move action
            if (choice == 5) {
                return;
            }

            // Moves in given direction and reduces dice total
            dir = Direction.values()[choice - 1];
            diceTotal -= moveInDirection(p.getCharacter(), dir);
        }
    }
}
