import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {
    private Game game;

    public Gui(Game game) {
        super("Hobby Detectives");
        this.game = game;

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        playerCountPreset();

        setVisible(true);
    }

    // PRESETS

    /**
     * Clears the current contents of the game's main frame and replaces it with the given
     * widget in the center of the frame
     * @param widget The JPanel to be displayed in the center of the game frame
     */
    private void singleWidgetPreset(JPanel widget) {
        getContentPane().removeAll();
        add(widget, BorderLayout.CENTER);
    }

    /**
     * Switches the game interface to the player count selection menu
     */
    private void playerCountPreset() {
        singleWidgetPreset(getPlayerCountWidget());
    }

    /**
     * Switches the game interface to the player name input menu
     */
    private void playerNamePreset() {
        singleWidgetPreset(getPlayerNameWidget());
    }

    private JPanel mapWidget(){
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int side = 100;

                int x = (getWidth() - side) / 2;
                int y = (getHeight() - side) / 2;

                g.setColor(Color.BLUE);
                g.fillRect(x, y, side, side);
            }
        };
        return panel;
    }

    /**
     * Asks at the start of the game for the number of players in the game
     * 3 or 4
     *
     * @return
     */
    private JPanel getPlayerCountWidget() {
        JLabel instructionLabel = new JLabel("Select number of players");
        String[] playerCounts = {"3", "4"};
        JComboBox<String> playerCountSelect = new JComboBox<>(playerCounts);
        JButton OKButton = new JButton("OK");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(playerCountSelect);
        panel.add(OKButton);

        return panel;
    }

    private JPanel getPlayerNameWidget() {
        return new JPanel();
    }

    /**
     * Asks for the user to enter their name
     *
     * @return
     */
    private JPanel playerEnterNames(int playerNumber) {
        JLabel instructionLabel = new JLabel("Player number " + playerNumber + " please enter your name:");
        JButton OKButton = new JButton("OK");
        JTextField insertNameTextField = new JTextField(10);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(insertNameTextField);
        panel.add(OKButton);

        //TODO add character select here

        return panel;
    }

    /**
     * Checks whether the tablet has been passed to the next player
     *
     * @param player
     * @return
     */
    private JPanel tabletPassNextTurn(Player player) {
        //String name = player.getName();
        String name = "bob";
        JButton OKButton = new JButton("OK");
        JLabel instructionLabel = new JLabel("Press OK when the tablet has been passed to " + name);

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Does the refute
     *
     * @param player
     * @return
     */
    private JPanel tabletPassRefute(Player player) {
        //String name = player.getName();
        String name = "bob";
        JButton OKButton = new JButton("OK");
        JLabel instructionLabel = new JLabel("Pass the tablet to " + name + " so they can refute");

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(instructionLabel);
        panel.add(OKButton);

        return panel;
    }

    /**
     * Player chooses whether they move or guess
     *
     * @return
     */
    private JPanel playerMoveOrGuess() {
        JLabel instructionLabel = new JLabel("");
        JPanel panel = new JPanel();
        return panel;
    }
}

