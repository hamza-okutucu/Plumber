package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;

import model.LevelModel;
import model.MenuModel;
import util.AssetsUtil;
import view.LevelView;
import view.MenuView;

/**
 * The controller for the game menu, responsible for handling menu interactions
 * and launching selected game levels.
 *
 * @author hamza-okutucu
 */
public class MenuController {
    
    private MenuModel menuModel;
    private MenuView menuView;

    /**
     * Initializes a new menu controller with the associated model and view. Sets up
     * menu buttons with action listeners.
     *
     * @param model The menu model.
     * @param view  The menu view.
     */
    public MenuController(MenuModel model, MenuView view) {
        this.menuModel = model;
        this.menuView = view;
        
        menuView.setMenu(menuModel.getLevelFileNames());

        addActionListenerOnMenuButtons();
    }

    /**
     * Adds action listeners to menu buttons. Each button corresponds to a game level
     * and triggers the loading of the selected level upon click.
     */
    private void addActionListenerOnMenuButtons() {
        for (JButton levelButton : menuView.getLevelButtons()) {
            levelButton.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        String levelFileName = levelButton.getText() + ".p";

                        showLevelView(levelFileName);
                    }
                }
            );
        }
    }

    /**
     * Shows the game level associated with the provided level file name. Loads the
     * level, creates the model, view, and controller, and disposes of the menu view.
     *
     * @param levelFileName The name of the level file to be loaded.
     */
    private void showLevelView(String levelFileName) {
        File levelFile = getLevelFile(levelFileName);

        LevelModel levelModel = new LevelModel(levelFile);
        LevelView levelView = new LevelView(levelModel);
        new LevelController(levelModel, levelView);

        menuView.dispose();
    }

    /**
     * Gets the File object for the specified level file by combining the file name
     * with the assets directory path. Verifies that the file exists and is valid.
     *
     * @param levelFileName The name of the level file.
     * @return A File object representing the level file.
     */
    private File getLevelFile(String levelFileName) {
        String levelDirectoryPath = AssetsUtil.ASSETS_DIRECTORY_PATH;
        File levelFile = new File(levelDirectoryPath, levelFileName);

        if (!levelFile.exists() || !levelFile.isFile()) {
            try {
                throw new FileNotFoundException("Le fichier de niveau n'existe pas : " + levelFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return levelFile;
    }
}
