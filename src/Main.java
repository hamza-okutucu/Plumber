import javax.swing.SwingUtilities;

import controller.MenuController;
import model.MenuModel;
import view.MenuView;

/**
 * The Main class serves as the entry point for the game application.
 * It creates and initializes the necessary components of the game's main menu.
 *
 * @author hamza-okutucu
 */
public class Main {
	
    /**
     * The main method of the application. It creates the menu model, view, and controller, and starts the game.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MenuModel menuModel = new MenuModel();
                MenuView menuView = new MenuView();
                new MenuController(menuModel, menuView);
            }
        });
    }
}