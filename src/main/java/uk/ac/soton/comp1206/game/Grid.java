package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Random;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 * <p>
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 * <p>
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 * <p>
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    private ArrayList<Integer> previous;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    public Grid(int cols, int rows, int[][] a) {
        this.rows = rows;
        this.cols = cols;
        grid = new SimpleIntegerProperty[cols][rows];
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(a[cols][rows]);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     *
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     *
     * @param x     column
     * @param y     row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     *
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            logger.info("Get" + x + "," + y + " returns " + grid[x][y].get());
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            logger.info("Get" + x + "," + y + " returns -1");
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     *
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     *
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Randomizes the alive squares on the current board
     */
    public void randomizeGrid() {
        var rand = new Random();
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                if (rand.nextInt(3) == 1) {
                    set(x, y, 1);
                }
            }
        }
    }

    /**
     * Clears the grid of all alive cells
     */
    public void clearGrid() {
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                set(x, y, 0);
            }
        }
    }

    /**
     * Converts the current grid into a 1d array list starting from 0,0 and ending at cols,rows and stores it in the
     * previous variable {@link ArrayList}
     */
    public void storeGridAsArrayList() {
        previous = new ArrayList<>();
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                previous.add(get(x, y));
            }
        }
    }

    /**
     * Switches the grid back to the previously saved one (grid is saved everytime the simulation is paused)
     */
    public void revertGrid() {
        int i = 0;
        for (var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                set(x, y, previous.get(i));
                i++;
            }
        }
    }
}
