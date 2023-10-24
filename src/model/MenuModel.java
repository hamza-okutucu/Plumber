package model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import util.AssetsUtil;

/**
 * The `MenuModel` class represents the model for the game menu, providing access to the available levels.
 *
 * @author hamza-okutucu
 */
public class MenuModel {
    
    private List<String> levelFiles;


    /**
     * Constructs a new `MenuModel` instance and initializes the list of level file names by loading available levels.
     */
    public MenuModel() {
        levelFiles = new ArrayList<>();
        loadLevelFiles();
    }

    /**
     * Loads the level file names from the game's assets.
     */
    private void loadLevelFiles() {
        File[] levelFiles = AssetsUtil.getLevelFiles();
        
        for (File levelFile : levelFiles) {
            this.levelFiles.add(getFileNameWithoutExtension(levelFile.getName()));
        }
    }

    /**
     * Retrieves the file name without its extension.
     *
     * @param fileName The name of the file including the extension.
     * @return The file name without the extension.
     */
    private String getFileNameWithoutExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        
        return fileName;
    }


    /**
     * Gets a list of available level file names in the menu.
     *
     * @return A list of level file names.
     */
    public List<String> getLevelFileNames() {
        return levelFiles;
    }
}