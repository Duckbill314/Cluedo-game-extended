/**
 * The HobbyDetectives class serves as the entry point of the Hobby Detectives game.
 * It initializes the game logic by creating an instance of the Game class
 * and starts the graphical user interface (GUI) by creating an instance of the Gui class.
 *
 * @author William Huang
 */
public class HobbyDetectives {

    /**
     * The main method is the entry point of the program.
     * It creates a new game instance and initializes the GUI.
     *
     * @param args Command-line arguments (not used in this program).
     */
    public static void main(String... args) {
        // Create a new game instance
        Game game = new Game();

        // Initialize the graphical user interface (GUI)
        new Gui(game);
    }
}