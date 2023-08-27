import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Gui extends JFrame {
    private Game game;
    // the three key frames that lie on the game frame
    private SidePanel sidebar;
    private BottomPanel bottomBar;
    private MidPanel mainView;

    // state logic
    private boolean textOrBoardPanel = true; // true for text panel, false for board panel
    private enum State {PlayerCount, PlayerName, PassTablet, PlayerTurn, DiceRoll, Movement, Guess, PassTabletRefute, Refute, GameOver}
    private State currentState = State.PlayerCount;

    // global variables (need to be accessible across the file)
    private RadioPanel charSelRadio;
    
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
     * @param widget
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
     * Resets all three main panels with a default mainView and sidebar.
     * The bottomBar is dependent on the input.
     */
    private void setTripleWidgetWorksheetPreset(JPanel widget) {
        bottomBar.clearPanel();
        mainView.clearPanel();
        sidebar.clearPanel();
        bottomBar.add(widget, BorderLayout.CENTER);
        if (textOrBoardPanel) {
            mainView.add(textWidget(), BorderLayout.CENTER);
        } else {
            mainView.add(boardWidget(), BorderLayout.CENTER);
        }
        sidebar.add(worksheetWidget(), BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    /**
     * Switches the game interface to the player count selection menu
     */
    private void setPlayerCountPreset() {
        currentState = State.PlayerCount;
        setSingleWidgetPreset(getPlayerCountWidget());
    }

    /**
     * Switches the game interface to the player name input menu
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
     * Switches the game interface to the tablet pass menu
     */
    private void setPassTabletPreset() {
        currentState = State.PassTablet;
        setTripleWidgetPreset(tabletPassNextTurnWidget());
    }

    /**
     * Switches the game interface to the player's turn menu
     */
    private void setPlayerTurnPreset() {
        currentState = State.PlayerTurn;
        setTripleWidgetWorksheetPreset(playerMoveOrGuessWidget());
    }

    /**
     * Switches the game interface to the dice roll menu
     */
    private void setDiceRollPreset() {
        currentState = State.DiceRoll;
        setTripleWidgetWorksheetPreset(rollDiceWidget());
    }

    /**
     * Switches the game interface to the movement menu
     */
    private void setMovementPreset() {
        currentState = State.Movement;
        setTripleWidgetWorksheetPreset(movementWidget());
    }

    /**
     * Switches the game interface to the guessing menu
     */
    private void setGuessPreset() {
        currentState = State.Guess;
        setTripleWidgetWorksheetPreset(guessWidget());
    }

    /**
     * Switches the game interface to the tablet pass menu specific to refuting
     */
    private void setPassTabletRefutePreset() {
        currentState = State.PassTabletRefute;
        setTripleWidgetPreset(tabletPassRefuteWidget());
    }

    /**
     * Switches the game interface to the refuting menu
     */
    private void setRefutePreset() {
        currentState = State.Refute;
        setTripleWidgetPreset(refuteWidget());
    }

    /**
     * Switches the game interface to the game over menu
     */
    private void setGameOverPreset() {
        currentState = State.GameOver;
        setTripleWidgetPreset(gameOverWidget());
    }

    // WIDGETS

    /**
     * Creates and returns a JPanel for selecting the number of players
     *
     * @return A JPanel containing the components to select the number of players
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
     * Asks for the user to enter their name
     *
     * @return
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
                    for (int i = 0; i<radioButtons.size();i++) {
                        if (radioButtons.get(i).isSelected()) {
                            selected = game.getModifiableCharacterList().get(i);
                            game.removeFromModifiableCharacterList(i);
                        }
                    }
                    if (selected != null) {
                        game.addPlayer(new Player(selected,new Worksheet(),insertNameTextField.getText(),true));
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
     * Checks whether the tablet has been passed to the next player
     *
     * @return
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
     * Asks for the tablet to be passed to the refuter
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
     * Player can choose to move, guess, or end their turn
     *
     * @return
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
                    } else { // TODO: THIS IS A GOOD OPPORTUNITY TO IMPLEMENT A JDialog error message
                        setPlayerTurnPreset();
                    }
                }
            });
        guessButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getCurrentPlayer().getIsEligible() && game.getCurrentPlayer().getCharacter().isInEstate() && !game.getGuessMade()) {
                        setGuessPreset();
                    } else { // TODO: THIS IS A GOOD OPPORTUNITY TO IMPLEMENT A JDialog error message
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
     * Player can roll the dice, or return to the previous menu
     *
     * @return
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
     * Player can move in one of four directions, or return to the previous menu
     *
     * @return
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
                    }
                    setMovementPreset();
                }
            });
        rightButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Right);
                        game.decrementDiceTotal(i);
                    }
                    setMovementPreset();
                }
            });
        downButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Down);
                        game.decrementDiceTotal(i);
                    }
                    setMovementPreset();
                }
            });
        leftButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (game.getDiceTotal() > 0) {
                        int i = game.moveInDirection(game.getCurrentPlayer().getCharacter(), Game.Direction.Left);
                        game.decrementDiceTotal(i);
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
     * The player can choose to make a normal guess, or a solve attempt (or return to the previous menu);
     *
     * @return
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

    /* private JPanel boardWidgetOld() {
    // for some reason, Swing uses html text formatting, but this is just a patchwork solution and won't be in the final result
    // when we do the actual board
    String boardString = game.getBoard().draw().replace("\n", "<br/>").replace(" ", "&nbsp&nbsp&nbsp");
    boardString = "<html>" + boardString + "</html>";
    JLabel boardLabel = new JLabel(boardString);

    JPanel panel = new JPanel();

    panel.add(boardLabel);

    return panel;
    } */

    

    
    /**
     * Creates full board by creating a board object and then adding cells
     *
     * @return
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
                    String displayIcon = ((GameTile)tile).getStored().getDisplayIcon();
                    board.add(board.gameTile(row, col, displayIcon));
                }
                else {
                    //TODO change to board.entranceTile so that its more clean
                    board.add(board.gameTile(row, col, ""));
                }
            }
        }
        return board;
    }

    class BoardPanel extends JPanel {
        private final int squareSize = 20;
        private final Color wallTileColor = Color.BLACK;
        private final Color tileBorderColor = Color.BLACK;
        private final Color gameTileColor = Color.GRAY;
        private final Color itemLetterColor = Color.CYAN;

        public JPanel wallTile(int row, int col) {
            JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(wallTileColor);
                        g.fillRect(0, 0, squareSize, squareSize);
                    }
                };

            panel.setBorder(BorderFactory.createLineBorder(tileBorderColor));
            panel.setPreferredSize(new Dimension(squareSize, squareSize));
            return panel;
        }

        public JPanel gameTile(int row, int col, String letter) {
            JPanel panel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(gameTileColor);
                        g.fillRect(0, 0, squareSize, squareSize);

                        if (!letter.isEmpty()) {
                            // Set a distinct color for the letter
                            g.setColor(itemLetterColor);

                            // Drawing the letter
                            g.setFont(new Font("Arial", Font.BOLD, 20));
                            FontMetrics fm = g.getFontMetrics();
                            int x = (squareSize - fm.charWidth(letter.charAt(0))) / 2;  // Use charAt(0) to get the first character from the string
                            int y = (squareSize + fm.getAscent() - fm.getDescent()) / 2;
                            g.drawString(letter, x, y);
                        }
                    }
                };
            panel.setBorder(BorderFactory.createLineBorder(tileBorderColor));
            panel.setPreferredSize(new Dimension(squareSize, squareSize));
            return panel;
        }
    }
    
    /**
     * Shows the player's worksheet
     *
     * @return
     */
    private JPanel worksheetWidget() {
        String worksheetString = game.getCurrentPlayer().getWorksheet().toString().replace("\n", "<br/>");
        worksheetString = "<html>" + worksheetString + "</html>";
        JLabel worksheetLabel = new JLabel(worksheetString);

        JPanel panel = new JPanel();

        panel.add(worksheetLabel);

        return panel;
    }

    /**
     * Allows a player to select which refuteable card to use to refute.
     * If they make a refutation, it goes back to the guesser, to end their turn.
     * If they cannot make a refutation, it is passed around to all the other players.
     *
     * @return
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
     * The end of the game. Shows who the winner is - either the name of the person who solved correctly,
     * or if all players fail to solve, then "Nobody".
     *
     * @return
     */
    private JPanel gameOverWidget() {
        JLabel messageLabel = new JLabel(String.format("Game over! The winner is: %s", game.getWinner()));

        JPanel panel = new JPanel();

        panel.add(messageLabel);

        return panel;
    }
 
    /**
     *  creates a text box that can be written to for user communication 
     */
    public TextPanel textWidget() {
        return new TextPanel();
    }

    public class TextPanel extends JPanel {
        private JTextArea displayTextArea;

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

        public void addText(String text) {
            displayTextArea.append(text + "\n");
        }

        public void clearText() {
            displayTextArea.setText("");;
        }
    }

    public MidPanel midWidget() {
        return new MidPanel();
    }
    
    class MidPanel extends JPanel {
        public MidPanel() {}

        public void addPanel(JPanel adding, BorderLayout b) {
            add(adding,b);
            revalidate();
            repaint();
        }

        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }

    /**
     *  creates a Panel that store other panels on the right hand side
     */
    public SidePanel sideWidget() {
        return new SidePanel();
    }
    
    class SidePanel extends JPanel {
        public SidePanel() {}

        public void addPanel(JPanel adding, BorderLayout b) {
            add(adding,b);
            revalidate();
            repaint();
        }

        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }

    /**
     *  creates a panel that can store other panels on the bottom of the screen 
     */
    public BottomPanel bottomWidget() {
        return new BottomPanel();
    }
    
    class BottomPanel extends JPanel {
        public BottomPanel() {}

        public void addPanel(JPanel adding, BorderLayout b) {
            add(adding,b);
            revalidate();
            repaint();
        }

        public void clearPanel() {
            removeAll();
            revalidate();
            repaint();
        }
    }
    
    private RadioPanel characterSelectRadioWidget() {
        RadioPanel radioPanel = new RadioPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
       
        for(Character c : game.getModifiableCharacterList()){
            JRadioButton radioButton = new JRadioButton(c.getName());
            radioPanel.addButton(radioButton);
            radioButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for(JRadioButton b : radioPanel.getRadioButtons()) {
                        b.setSelected(false);
                    }  
                    radioButton.setSelected(true);
                }
            });
            radioPanel.add(radioButton);
        }
        return radioPanel;
    }

    class RadioPanel extends JPanel {
        public RadioPanel() {}
        public ArrayList<JRadioButton> radioButtons = new ArrayList<>();
        public void addButton(JRadioButton button) {
            radioButtons.add(button);
        }
        public ArrayList<JRadioButton> getRadioButtons() {
            return this.radioButtons;
        }
    }

    /**
     * Only to be used when switching between the Text and Board panels.
     * Normally, the state machine will operate by itself with each user action, but the
     * menu item toggle action exists outside of the state machine and needs another
     * way to trigger state events. Consider it a universal way to trigger a state.
     * @param state
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
