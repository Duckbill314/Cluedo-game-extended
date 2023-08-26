import javax.swing.*;
import java.awt.*;

public class Gui extends JFrame {
	public Gui() {
        super("Hobby Detectives");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();setSize(screenSize.width, screenSize.height);
        setSize(screenSize.width, screenSize.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        menuPreset();

        setVisible(true);
    }

    // PRESETS

    // main menu preset
    private void menuPreset() {
        getContentPane().removeAll();
        JPanel userInputPanel = playerCountSelector();
        add(userInputPanel, BorderLayout.NORTH);
    }

    // WIDGETS

    // main menu player count selector
    private JPanel playerCountSelector() {
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
}

