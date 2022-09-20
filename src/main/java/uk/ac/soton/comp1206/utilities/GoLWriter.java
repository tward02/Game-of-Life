package uk.ac.soton.comp1206.utilities;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This object is used to create and save board configuration files using the file extension .gol
 */
public class GoLWriter {

    private final String path;

    public GoLWriter(String path) {
        this.path = path;
    }

    /**
     * Saves the board configuration file to the specified path, and name
     *
     * @param name the name of the file
     * @param size the size of the board being saved
     * @param grid an arraylist of the grid configuration
     * @throws IOException if an exception is thrown when creating a file it is thrown to the SimulatorScene so that
     * the error can be displayed to the user
     */
    public void saveFile(String name, int size, ArrayList<Integer> grid) throws IOException {
        System.out.println("Path: " + path + File.separator + name + ".gol");
        var dir = new File(path);
        if (!dir.exists()) {
             dir.mkdir();
        }
        var saveGame = new File(path + File.separator + name + ".gol");
        var created = saveGame.createNewFile();
        if (created) {
            var out = new PrintWriter(saveGame);
            out.println(size);
            for (int num : grid) {
                out.print(num);
            }
            out.close();
            //TODO add info message saying config saved successfully
        } else {
            //TODO add error message saying file didnt save
        }
    }
}
