import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.*;
import java.util.*;

/**
 * The GUI for HobbyDetectives. 
 * 
 * Has four component sections:
 * - A static menu bar
 * - A main view which is dynamically updated based on state
 * - A sidebar which is dynamically updated based on state
 * - A bottom bar which is dynamically updated based on state
 * 
 * Uses state machine logic to control what is displayed to the user.
 * Has widgets, which are components with functional behaviour attached to them,
 * and presets, which are groups of widgets that represent the state.
 *
 * @author William Huang
 * @author Finley Neilson
 * @author Mazen Khallaf
 * @author James Goode
 */
public class Gui extends JFrame {
    private Game game;
    // the three key frames that lie on the game frame
    private SidePanel sidebar;
    private BottomPanel bottomBar;
    private MidPanel mainView;

    /** Width of each game square. */
    public int squareWidth = 0;

    /** Height of each game square. */
    public int squareHeight = 0;

    /**
     * Map that stores positions of game characters.
     * The keys are character names, and the values are their positions.
     */
    private Map<String, CPosition> people = new HashMap<>();

    // state logic
    private boolean textOrBoardPanel = true; // true for text panel, false for board panel

    private enum State {PlayerCount, PlayerName, PassTablet, PlayerTurn, DiceRoll, Movement, Guess, PassTabletRefute, Refute, GameOver}

    private State currentState = State.PlayerCount;

    // global variables (need to be accessible across the file)
    private RadioPanel charSelRadio;

    /**
     * Constructs a `Gui` object for the "Hobby Detectives" game.
     *
     * @param game The game logic associated with the GUI.
     */
    public Gui(Game game) {
        super("Hobby Detectives");
        this.game = game;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a JSplitPane for the sidebar and bottomBar
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(1.0); // Sidebar takes all the space

        // Create a MenuBar for switching views
        JMenuBar menuBar = new JMenuBar();
        JMenu switchMenu = new JMenu("Switch Panels");
        JMenuItem textItem = new JMenuItem("Text Panel");
        JMenuItem boardItem = new JMenuItem("Board Panel");
        textItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOrBoardPanel = true;
                triggerStateOnce(currentState);
            }
        });
        boardItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textOrBoardPanel = false;
                triggerStateOnce(currentState);
            }
        });
        menuBar.add(switchMenu);
        switchMenu.add(textItem);
        switchMenu.add(boardItem);
        setJMenuBar(menuBar);

        // Set up the initial layout using the JSplitPane
        sidebar = sideWidget();
        sidebar.setPreferredSize(new Dimension(150, 0));
        bottomBar = bottomWidget();
        bottomBar.setPreferredSize(new Dimension(0, 50));
        mainView = midWidget();
        splitPane.setTopComponent(mainView);
        splitPane.setBottomComponent(bottomBar);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(sidebar, BorderLayout.EAST);

        pack();
        setSize(600, 600);
        setVisible(true);

        setPlayerCountPreset();
    }

    // PRESETS

    /**
     * Changes only the bottom bar, good for quick and easy replacement.
     * If replacing multiple components, do not use this method.
     *
     * @param widget The JPanel to set in the bottom bar.
     */
    private void setSingleWidgetPreset(JPanel widget) {
        //getContentPane().removeAll();
        bottomBar.clearPanel();
        bottomBar.add(widget, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Changes the bottom bar to the desired widget,
     * and clears the main view and sidebar.
     *
     * @param widget The JPanel to set in the bottom bar.
     */
    private void setTripleWidgetPreset(JPanel widget) {
        bottomBar.clearPanel();
        mainView.clearPanel();
        sidebar.clearPanel();
        bottomBar.add(widget, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Resets all three main panels with a default main view and sidebar.
     * The bottom bar is dependent on the input.
     *
     * @param widget The JPanel to set in the bottom bar.
     */
    private void setTripleWidgetWorksheetPreset(JPanel widget) {
        bottomBar.clearPanel();
        mainView.clearPanel();
        sidebar.clearPanel();
        bottomBar.add(widget, BorderLayout.CENTER);
        if (textOrBoardPanel) {
            mainView.add(textWidget(), BorderLayout.CENTER);
        } else {
            squareWidth = mainView.width()/24;
            squareHeight = mainView.height()/24;
            mainView.addPanel(boardWidget());
        }
        revalidate();
        repaint();
    }

    /**
     * Switches the game interface to the player count selection menu.
     */
    private void setPlayerCountPreset() {
        currentState = State.PlayerCount;
        setSingleWidgetPreset(getPlayerCountWidget());
    }

    /**
     * Switches the game interface to the player name input menu.
     */
    private void setPlayerNamePreset() {
        currentState = State.PlayerName;
        bottomBar.clearPanel();
        sidebar.clearPanel();
        charSelRadio = characterSelectRadioWidget();
        sidebar.add(charSelRadio, BorderLayout.CENTER);
        bottomBar.add(playerEnterNamesWidget(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Switches the game interface to the tablet pass menu.
     */
    private void setPassTabletPreset() {
        currentState = State.PassTablet;
        setTripleWidgetPreset(tabletPassNextTurnWidget());
    }

    /**
     * Switches the game interface to the player's turn menu.
     */
    private void setPlayerTurnPreset() {
        currentState = State.PlayerTurn;
        setTripleWidgetWorksheetPreset(playerMoveOrGuessWidget());
    }

    /**
     * Switches the game interface to the dice roll menu.
     */
    private void setDiceRollPreset() {
        currentState = State.DiceRoll;
        setTripleWidgetWorksheetPreset(rollDiceWidget());
    }

    /**
     * Switches the game interface to the movement menu.
     */
    private void setMovementPreset() {
        currentState = State.Movement;
        setTripleWidgetWorksheetPreset(movementWidget());
    }

    /**
     * Switches the game interface to the guessing menu.
     */
    private void setGuessPreset() {
        currentState = State.Guess;
        setTripleWidgetWorksheetPreset(guessWidget());
    }

    /**
     * Switches the game interface to the tablet pass menu specific to refuting.
     */
    private void setPassTabletRefutePreset() {
        currentState = State.PassTabletRefute;
        setTripleWidgetPreset(tabletPassRefuteWidget());
    }

    /**
     * Switches the game interface to the refuting menu.
     */
    private void setRefutePreset() {
        currentState = State.Refute;
        setTripleWidgetPreset(refuteWidget());
    }

    /**
     * Switches the game interface to the game over menu.
     */
    private void setGameOverPreset() {
        currentState = State.GameOver;
        setTripleWidgetPreset(gameOverWidget());
    }

    // WIDGETS

    /**
     * Creates and returns a JPanel for selecting the number of players.
     *
     * @return A JPanel containing the components to select the number of players.
     */
    private JPanel getPlayerCountWidget() {
        JLabel instructionLabel = new JLabel("Select number of players");
        Integer[] playerCounts = {3, 4};
        JComboBox<Integer> playerCountSelect = new JComboBox<>(playerCounts);
        JButton OKButton = new JButton("OK");

        // action listener for button
        OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int i = (int) playerCountSelect.getSelectedItem();
                    game.setPlayerCount(i);
                    setPlayerNamePreset();
                }
            });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(playerCountSelect);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Asks for the user to enter their name.
     *
     * @return A JPanel for entering the player's name.
     */
    private JPanel playerEnterNamesWidget() {
        JLabel instructionLabel = new JLabel(String.format("Player %d, please enter your name:", game.getPlayerInitCount() + 1));
        JButton OKButton = new JButton("OK");
        JTextField insertNameTextField = new JTextField(10);
        ArrayList<JRadioButton> radioButtons = charSelRadio.getRadioButtons();
        // action listener for button
        OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Character selected = null;
                    for (int i = 0; i < radioButtons.size(); i++) {
                        if (radioButtons.get(i).isSelected()) {
                            selected = game.getModifiableCharacterList().get(i);
                            game.removeFromModifiableCharacterList(i);
                        }
                    }
                    if (selected == null) {
                        errorMessagePopup();
                    }
                    else {
                        game.addPlayer(new Player(selected, new Worksheet(), insertNameTextField.getText(), true));
                        game.incrementPlayerInitCount();
                        sidebar.clearPanel();
                        if (game.getPlayerInitCount() < game.getPlayerCount()) {
                            setPlayerNamePreset();
                        } else {
                            game.setupGame();
                            setPassTabletPreset();
                        }
                    }
                }
            });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(insertNameTextField);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Checks whether the tablet has been passed to the next player.
     *
     * @return A JPanel to indicate the tablet passing to the next player.
     */
    private JPanel tabletPassNextTurnWidget() {
        String name = game.getCurrentPlayer().getName();
        JButton OKButton = new JButton("OK");
        JLabel instructionLabel = new JLabel("Press OK when the tablet has been passed to " + name);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listener for button
        OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.setDiceTotal(0);
                    game.setDiceRolled(false);
                    game.setGuessMade(false);
                    game.setRefuteCount(0);
                    game.clearUsedTiles();
                    setPlayerTurnPreset();
                }
            });

        panel.add(instructionLabel);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Asks for the tablet to be passed to the refuter.
     *
     * @return A JPanel to prompt passing the tablet to the refuter.
     */
    private JPanel tabletPassRefuteWidget() {
        game.incrementRefuteCount();
        int guesserId = game.getPlayers().indexOf(game.getCurrentPlayer());
        int refuterId = guesserId + game.getRefuteCount();
        if (refuterId > game.getPlayerCount() - 1) {
            refuterId -= game.getPlayerCount();
        }
        game.setRefuter(refuterId);
        String name = game.getPlayers().get(refuterId).getName();
        JButton OKButton = new JButton("OK");
        JLabel instructionLabel = new JLabel("Pass the tablet to " + name + " so they can refute");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listener for button
        OKButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setRefutePreset();
                }
            });

        panel.add(instructionLabel);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Player can choose to move, guess, or end their turn.
     *
     * @return A JPanel to allow the player to choose their action.
     */
    private JPanel playerMoveOrGuessWidget() {
        JLabel instructionLabel = new JLabel(String.format("You are playing as %s (%s)", game.getCurrentPlayer().getCharacter().getName(),
                    game.getCurrentPlayer().getCharacter().getDisplayIcon()));
        JButton moveButton = new JButton("Move");
        JButton guessButton = new JButton("Guess/Solve");
        JButton endTurnButton = new JButton("End turn");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listeners for buttons
        moveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getCurrentPlayer().getIsEligible()) {
                        if (!game.getDiceRolled()) {
                            setDiceRollPreset();
                        } else {
                            setMovementPreset();
                        }
                    } else {
                        errorMessagePopup();
                        setPlayerTurnPreset();
                    }
                }
            });
        guessButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getCurrentPlayer().getIsEligible() && game.getCurrentPlayer().getCharacter().isInEstate() && !game.getGuessMade()) {
                        setGuessPreset();
                    } else {
                        errorMessagePopup();
                        setPlayerTurnPreset();
                    }
                }
            });
        endTurnButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.updateTurn();
                    setPassTabletPreset();
                }
            });

        panel.add(instructionLabel);
        panel.add(moveButton);
        panel.add(guessButton);
        panel.add(endTurnButton);

        return panel;
    }

    /**
     * Player can roll the dice or return to the previous menu.
     *
     * @return A JPanel to roll the dice or return to the previous menu.
     */
    private JPanel rollDiceWidget() {
        JLabel instructionLabel = new JLabel("Roll the dice!");
        JButton rollButton = new JButton("Roll!");
        JButton returnButton = new JButton("Go back");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listeners for buttons
        rollButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.setDiceRolled(true);
                    game.setDiceTotal(game.rollDice() + game.rollDice());
                    setMovementPreset();
                }
            });
        returnButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPlayerTurnPreset();
                }
            });

        panel.add(instructionLabel);
        panel.add(rollButton);
        panel.add(returnButton);

        return panel;
    }

    /**
     * Player can move in one of four directions or return to the previous menu.
     *
     * @return A JPanel to allow the player to move or return.
     */
    private JPanel movementWidget() {
        JLabel instructionLabel = new JLabel(String.format("You have %d moves remaining", game.getDiceTotal()));
        JButton upButton = new JButton("Up");
        JButton rightButton = new JButton("Right");
        JButton downButton = new JButton("Down");
        JButton leftButton = new JButton("Left");
        JButton returnButton = new JButton("Go back");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listeners for buttons
        upButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Up);
                        game.decrementDiceTotal(i);
                    } else {
                        errorMessagePopup();
                    }
                    setMovementPreset();
                }
            });
        rightButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Right);
                        game.decrementDiceTotal(i);
                    } else {
                        errorMessagePopup();
                    }
                    setMovementPreset();
                }
            });
        downButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Down);
                        game.decrementDiceTotal(i);
                    } else {
                        errorMessagePopup();
                    }
                    setMovementPreset();
                }
            });
        leftButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Left);
                        game.decrementDiceTotal(i);
                    } else {
                        errorMessagePopup();
                    }
                    setMovementPreset();
                }
            });
        returnButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPlayerTurnPreset();
                }
            });

        panel.add(instructionLabel);
        panel.add(upButton);
        panel.add(rightButton);
        panel.add(downButton);
        panel.add(leftButton);
        panel.add(returnButton);

        return panel;
    }

    /**
     * Player can make a guess using dropboxes of the characters and weapons.
     * The estate is predetermined by the player's current estate.
     * The player can choose to make a normal guess, a solve attempt, or return to the previous menu.
     *
     * @return A JPanel to allow the player to make a guess or solve.
     */
    private JPanel guessWidget() {
        JLabel estateLabel = new JLabel(String.format("Estate: %s", game.getCurrentPlayer().getCharacter().getEstate().getName()));
        JLabel characterLabel = new JLabel("Character: ");
        String[] characterArray = {"Lucilla", "Bert", "Malina", "Percy"};
        JComboBox<String> characterBox = new JComboBox<>(characterArray);
        JLabel weaponLabel = new JLabel("Weapon: ");
        String[] weaponArray = {"Broom", "Scissors", "Knife", "Shovel", "iPad"};
        JComboBox<String> weaponBox = new JComboBox<>(weaponArray);
        JButton guessButton = new JButton("Guess");
        JButton solveButton = new JButton("Solve");
        JButton returnButton = new JButton("Go back");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // action listeners for buttons
        guessButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.guess((String) characterBox.getSelectedItem(), (String) weaponBox.getSelectedItem(), false);
                    setPassTabletRefutePreset();
                }
            });
        solveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int result = game.guess((String) characterBox.getSelectedItem(), (String) weaponBox.getSelectedItem(), true);
                    if (result == 1) {
                        setGameOverPreset();
                    } else {
                        boolean gameOver = true;
                        for (Player p : game.getPlayers()) {
                            if (p.getIsEligible()) {
                                gameOver = false;
                            }
                        }
                        if (gameOver) {
                            setGameOverPreset();
                        } else {
                            game.getCurrentPlayer().setIsEligible(false);
                            setPlayerTurnPreset();
                        }
                    }
                }
            });
        returnButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setPlayerTurnPreset();
                }
            });

        panel.add(estateLabel);
        panel.add(characterLabel);
        panel.add(characterBox);
        panel.add(weaponLabel);
        panel.add(weaponBox);
        panel.add(guessButton);
        panel.add(solveButton);
        panel.add(returnButton);

        return panel;
    }

    /**
     * Creates a full board by creating a board object and then adding cells.
     *
     * @return A JPanel representing the game board.
     */
    private JPanel boardWidget() {
        BoardPanel board = new BoardPanel();
        board.setLayout(new GridLayout(24, 24, 0, 0));
        for (int row = 0; row < 24; row++) {
            for (int col = 0; col < 24; col++) {
                Tile tile = game.getBoard().getTile(row, col);

                if (tile instanceof WallTile) {
                    board.add(board.wallTile(row, col));
                } else if (tile instanceof GameTile) {
                    String displayIcon = ((GameTile) tile).getStored().getDisplayIcon();
                    board.add(board.gameTile(row, col, displayIcon));
                } else {
                    board.add(board.enteranceTile(row, col));
                }
            }
        }
        return board;
    }

    /**
     * JPanel class for displaying the game board.
     *
     * @author Finley Neilson
     * @author William Huang
     * @author James Goode
     */
    class BoardPanel extends JPanel {
        private final Color wallTileColor = Color.BLACK;
        private final Color enteranceTileColor = Color.DARK_GRAY;
        private final Color tileBorderColor = Color.BLACK;
        private final Color gameTileColor = Color.GRAY;
        private final Color itemLetterColor = Color.CYAN;

        /**
         * Create a JPanel for a wall tile.
         *
         * @param row The row index of the tile.
         * @param col The column index of the tile.
         * @return The JPanel representing a wall tile.
         */
        public JPanel wallTile(int row, int col) {
            JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(wallTileColor);
                        g.fillRect(0, 0, squareWidth, squareHeight);
                    }
                };

            panel.setBorder(BorderFactory.createLineBorder(tileBorderColor));
            panel.setPreferredSize(new Dimension(squareWidth, squareHeight));
            return panel;
        }

        /**
         * Create a JPanel for a game tile with an item letter.
         *
         * @param row    The row index of the tile.
         * @param col    The column index of the tile.
         * @param letter The letter to display on the game tile.
         * @return The JPanel representing a game tile with an item letter.
         */
        public JPanel gameTile(int row, int col, String letter) {
            JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {

                        super.paintComponent(g);
                        g.setColor(gameTileColor);
                        g.fillRect(0, 0, squareWidth, squareHeight);

                        if (!letter.isEmpty()) {

                            // Set a distinct color for the letter
                            g.setColor(itemLetterColor);

                            // Drawing the letter
                            g.setFont(new Font("Arial", Font.BOLD, 20));
                            FontMetrics fm = g.getFontMetrics();
                            int x = (squareWidth - fm.charWidth(letter.charAt(0))) / 2;  // Use charAt(0) to get the first character from the string
                            int y = (squareHeight + fm.getAscent() - fm.getDescent()) / 2;

                            people.put(letter,new CPosition(x+(col*squareWidth),y+(row*squareHeight)));

                            g.drawString(letter, x, y);
                        }

                    }
                };
            panel.setBorder(BorderFactory.createLineBorder(tileBorderColor));
            panel.setPreferredSize(new Dimension(squareWidth, squareHeight));
            return panel;
        }

        /**
         * Create a JPanel for an entrance tile.
         *
         * @param row The row index of the tile.
         * @param col The column index of the tile.
         * @return The JPanel representing an entrance tile.
         */
        public JPanel enteranceTile(int row, int col) {
            JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(enteranceTileColor);
                        g.fillRect(0, 0, squareWidth, squareHeight);
                    }
                };

            panel.setBorder(BorderFactory.createLineBorder(tileBorderColor));
            panel.setPreferredSize(new Dimension(squareWidth, squareHeight));
            return panel;
        }

    }


    /**
     * Represents a position on the board.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     */
    public record CPosition(int x, int y) {}

    /**
     * Creates a JPanel for the refute widget, allowing players to select a refutable card if possible.
     *
     * @return A JPanel for the refute widget.
     */
    private JPanel refuteWidget() {
        int refuter = game.getRefuter();
        java.util.List<String> cards = game.getRefuteableCards(game.getPlayers().get(refuter));
        String[] cardArray = new String[cards.size()];
        for (int i = 0; i < cards.size(); i++) {
            cardArray[i] = cards.get(i);
        }

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        if (!cards.isEmpty()) {
            JLabel refuteLabel = new JLabel("Refute using this card: ");
            JComboBox<String> cardBox = new JComboBox<>(cardArray);
            JButton OKButton = new JButton("OK");

            OKButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        for (Card c : game.getCards()) {
                            if (c.getName().equals((String) cardBox.getSelectedItem())) {
                                game.getCurrentPlayer().getWorksheet().addShownCard(c);
                            }
                        }
                        setPlayerTurnPreset();
                    }
                });

            panel.add(refuteLabel);
            panel.add(cardBox);
            panel.add(OKButton);
        } else {
            JLabel refuteLabel = new JLabel("You have no cards to refute with");
            JButton OKButton = new JButton("OK");

            OKButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (game.getRefuteCount() < game.getPlayerCount() - 1) {
                            setPassTabletRefutePreset();
                        } else {
                            setPlayerTurnPreset();
                        }
                    }
                });

            panel.add(refuteLabel);
            panel.add(OKButton);
        }
        return panel;
    }

    /**
     * Creates a JPanel for the game over screen, displaying the winner.
     *
     * @return A JPanel for the game over screen.
     */
    private JPanel gameOverWidget() {
        JLabel messageLabel = new JLabel(String.format("Game over! The winner is: %s", game.getWinner()));

        JPanel panel = new JPanel();

        panel.add(messageLabel);

        return panel;
    }

    /**
     * Creates a JPanel for displaying text information.
     *
     * @return A TextPanel for displaying text information.
     */
    public TextPanel textWidget() {
        TextPanel textPanel = new TextPanel();
        textPanel.addText(game.getCurrentPlayer().getWorksheet().toString());
        return textPanel;
    }

    /**
     * A JPanel used for displaying text.
     * 
     * @author James Goode
     */
    public class TextPanel extends JPanel {
        private JTextArea displayTextArea;

        /**
         * Constructor for the TextPanel class.
         */
        public TextPanel() {
            setLayout(new GridBagLayout());

            displayTextArea = new JTextArea(20, 20);
            displayTextArea.setEditable(false);
            JScrollPane displayScrollPane = new JScrollPane(displayTextArea);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.anchor = GridBagConstraints.NORTH;

            add(displayScrollPane, gbc);
        }

        /**
         * Adds text to be displayed.
         * @param text
         */
        public void addText(String text) {
            displayTextArea.append(text + "\n");
        }

        /**
         * Clears the display text.
         */
        public void clearText() {
            displayTextArea.setText("");
            ;
        }
    }

    /**
     * Creates a MidPanel.
     * 
     * @return the MidPanel.
     */
    public MidPanel midWidget() {
        return new MidPanel();
    }

    /**
     * JPanel class for managing the middle section of the GUI.
     *
     * @author William Huang
     * @author Finley Neilson
     * @author James Goode
     * 
     */
    class MidPanel extends JPanel {
        /**
         * Constructor for the MidPanel class.
         */
        public MidPanel() {
            addComponentListener(new ResizeListener());

            addMouseMotionListener(new MouseMotionAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                      
                        int mouseX = e.getX();
                        int mouseY = e.getY();

                        for (Map.Entry<String, CPosition> entry : people.entrySet()) {
                            CPosition position = entry.getValue();
                            int Cx =  position.x();
                            int Cy =  position.y();

                            // System.out.println(entry.getKey()+" is at " +position.x() +" "+position.y());

                            double ac = Math.abs(Cy - mouseY);
                            double cb = Math.abs(Cx - mouseX);

                            double distance = Math.hypot(ac, cb);

                            if((distance<20) && (!textOrBoardPanel)){

                                JFrame frame = new JFrame();
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                                JDialog dialog = new JDialog(frame, "Item Info", true);

                                String shortenedName = entry.getKey();
                                
                                JLabel label = new JLabel(game.findFullName(shortenedName));
                                
                                label.setHorizontalAlignment(JLabel.CENTER);

                                JButton exitButton = new JButton("Exit");
                                exitButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            dialog.dispose();
                                            frame.dispose();
                                        }
                                    });
                                JPanel panel = new JPanel(new BorderLayout());
                                panel.add(label, BorderLayout.CENTER);
                                panel.add(exitButton, BorderLayout.SOUTH);

                                dialog.getContentPane().add(panel);
                                dialog.pack();
                                dialog.setLocationRelativeTo(frame);
                                dialog.setVisible(true);

                            }
                        }

                    }
                });
        }

        public int width(){
            return this.getWidth();
        }

        public int height(){
            return this.getHeight();
        }

        /**
         * Local class to MidPanel that handles resizing of the board in response
         * to the GUI being resized.
         * 
         * @author James Goode
         */
        private class ResizeListener implements ComponentListener {
            @Override
            public void componentResized(ComponentEvent e) {
                triggerStateOnce(currentState);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                // to stop abstract
            }

            @Override
            public void componentShown(ComponentEvent e) {
                // to stop abstract
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                // to stop abstract
            }

        }

        /**
         * Adds a JPanel to the middle GUI section.
         * 
         * @param adding the JPanel to be added.
         */
        public void addPanel(JPanel adding) {
            this.add(adding);
            revalidate();
            repaint();
        }

        /**
         * Clears the middle GUI section.
         */
        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }

    /**
     * creates a Panel that store other panels on the right hand side
     */
    public SidePanel sideWidget() {
        return new SidePanel();
    }

    /**
     * JPanel class for managing the right-hand sidebar of the GUI.
     *
     * @author James Goode
     */
    class SidePanel extends JPanel {

        /**
         * Adds a JPanel to the sidebar.
         * 
         * @param adding the JPanel being added
         * @param b the BorderLayout position used
         */
        public void addPanel(JPanel adding, BorderLayout b) {
            add(adding, b);
            revalidate();
            repaint();
        }

        /**
         * Clears the sidebar.
         */
        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }

    /**
     * creates a panel that can store other panels on the bottom of the screen
     */
    public BottomPanel bottomWidget() {
        return new BottomPanel();
    }

    /**
     * JPanel class for managing the bottom section of the GUI.
     *
     * @author James Goode
     */
    class BottomPanel extends JPanel {
        /**
         * Adds a JPanel to the bottom bar.
         * 
         * @param adding the JPanel being added
         * @param b the BorderLayout position used
         */
        public void addPanel(JPanel adding, BorderLayout b) {
            add(adding, b);
            revalidate();
            repaint();
        }

        /**
         * Clears the bottom bar.
         */
        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }

    /**
     * Creates a RadioPanel for character selection.
     *
     * @return A RadioPanel for character selection.
     */
    private RadioPanel characterSelectRadioWidget() {
        RadioPanel radioPanel = new RadioPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));

        for (Character c : game.getModifiableCharacterList()) {
            JRadioButton radioButton = new JRadioButton(c.getName());
            radioPanel.addButton(radioButton);
            radioButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        for (JRadioButton b : radioPanel.getRadioButtons()) {
                            b.setSelected(false);
                        }
                        radioButton.setSelected(true);
                    }
                });
            radioPanel.add(radioButton);
        }
        return radioPanel;
    }

    /**
     * RadioPanel class for managing radio buttons.
     *
     * @author William Huang
     * @author Finley Neilson
     */
    class RadioPanel extends JPanel {
        /**
         * An ArrayList of the JRadioButtons in the RadioPanel.
         */
        public ArrayList<JRadioButton> radioButtons = new ArrayList<>();

        /**
         * Adds a JRadioButton to the panel.
         * @param button
         */
        public void addButton(JRadioButton button) {
            radioButtons.add(button);
        }
        public ArrayList<JRadioButton> getRadioButtons() {
            return this.radioButtons;
        }
    }

    /**
     * Displays an error message in a popup dialog.
     */
    private void errorMessagePopup() {
        JOptionPane.showMessageDialog(this, "Invalid action", "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Used for triggering state transitions when switching between Text and Board panels.
     * Normally, the state machine will operate by itself with each user action, but the
     * menu item toggle action exists outside of the state machine and needs another
     * way to trigger state events. Consider it a universal way to trigger a state.
     *
     * @param state The state to switch to.
     */
    private void triggerStateOnce(State state) {
        switch (currentState) {
            case PlayerCount -> {
                    setPlayerCountPreset();
                }
            case PlayerName -> {
                    setPlayerNamePreset();
                }
            case PassTablet -> {
                    setPassTabletPreset();
                }
            case PlayerTurn -> {
                    setPlayerTurnPreset();
                }
            case DiceRoll -> {
                    setDiceRollPreset();
                }
            case Movement -> {
                    setMovementPreset();
                }
            case Guess -> {
                    setGuessPreset();
                }
            case PassTabletRefute -> {
                    setPassTabletRefutePreset();
                }
            case Refute -> {
                    setRefutePreset();
                }
            case GameOver -> {
                    setGameOverPreset();
                }
        }
    }
}
