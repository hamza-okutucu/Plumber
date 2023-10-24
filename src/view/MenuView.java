package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * The MenuView class represents the graphical user interface for selecting a level in the game.
 * It displays a list of available levels as buttons that the player can choose from.
 *
 * @author hamza-okutucu
 */
public class MenuView extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private List<JButton> levelButtons;

    /**
     * Constructs a new MenuView instance to create the level selection menu.
     */
    public MenuView() {
        setTitle("Select a level");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setVisible(true);
        
        levelButtons = new ArrayList<>();
    }

    /**
     * Sets up the level selection menu with a list of available levels.
     *
     * @param levelFileNames A list of level file names that will be displayed as buttons.
     */
    public void setMenu(List<String> levelFileNames) {
        for (String levelFileName : levelFileNames) {
            JButton levelButton = new JButton(levelFileName);
            Font buttonFont = new Font("Arial", Font.BOLD, 18);
            levelButton.setBackground(new Color(245, 245, 220));
            levelButton.setFont(buttonFont);
            levelButtons.add(levelButton);
            add(levelButton);
        }

        setLayout(new GridLayout(levelButtons.size(), 1));
    }

    /**
     * Returns a list of buttons representing the available levels in the menu.
     *
     * @return A list of JButton objects, each corresponding to a level button.
     */
    public List<JButton> getLevelButtons() {
        return levelButtons;
    }
}