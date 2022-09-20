package uk.ac.soton.comp1206.utilities;

import javafx.scene.control.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;

/**
 * This class is used to create and save board configuration files using the file extension .gol
 */
public class GoLProcessor {

    private static final Logger logger = LogManager.getLogger(GoLProcessor.class);

    private final String path;

    public GoLProcessor(String path) {
        this.path = path;
    }

    /**
     * Saves the board configuration file to the specified path, and name
     *
     * @param name the name of the file
     * @param size the size of the board being saved
     * @param grid an arraylist of the grid configuration
     * @throws IOException if an exception is thrown when creating a file it is thrown to the SimulatorScene so that
     *                     the error can be displayed to the user
     */
    public void saveFile(String name, int size, ArrayList<Integer> grid) throws IOException {
        logger.info("Write Path: " + path + File.separator + name + ".gol");
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
            var alert = new Alert(Alert.AlertType.CONFIRMATION, "Board Configuration Saved Successfully");
            alert.showAndWait();
        } else {
            var alert = new Alert(Alert.AlertType.ERROR, "Error: Failed to Save Board Configuration\n\n" +
                    "Please try again");
            alert.showAndWait();
        }
    }

    /**
     * Converts a .gol file to a 1d arraylist containing the configuration of a saved grid including its size and the
     * value of every cell
     *
     * @param loadFile the file object of the config file selected to be loaded
     * @return a 1d arraylist containing the grid configuration
     * @throws IOException if an exception is thrown when finding or reading the file then it is thrown to the
     *                     SimulatorScene so that the error can be displayed to the user
     */
    public ArrayList<Integer> loadFile(File loadFile) throws IOException {
        logger.info("Read Path: " + path + File.separator + loadFile.getName());
        var gridConfig = new ArrayList<Integer>();
        var reader = new BufferedReader(new FileReader(loadFile));
        gridConfig.add(Integer.parseInt(reader.readLine()));
        String grid = reader.readLine();
        for (char cell : grid.toCharArray()) {
            gridConfig.add(Integer.parseInt(String.valueOf(cell)));
        }
        if ((gridConfig.get(0) ^ 2) == gridConfig.size() - 1) {
            return null;
        }
        return gridConfig;
    }
}
