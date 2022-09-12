package uk.ac.soton.comp1206.game;

import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    private Timer tickTimer;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        //Get the new value for this block
        int previousValue = grid.get(x,y);
        int newValue = previousValue + 1;
        if (newValue  > 1) {
            newValue = 0;
        }

        //Update the grid with the new value
        grid.set(x,y,newValue);
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    private void tick() {
        int[][] newGrid = new int[rows][cols];
        for(var x = 0; x < rows; x++) {
            for (var y = 0; y < cols; y++) {
                var neighbours = getNeighbours(x, y);
                if (neighbours == 2 || neighbours == 3) {
                    newGrid[x][y] = 1;
                } else {
                    newGrid[x][y] = 0;
                }
            }
        }

        for(var x = 0; x < rows; x++) {
            for (var y = 0; y < cols; y++) {
                    grid.set(x, y, newGrid[x][y]);
                }
            }


    }

    private int getNeighbours(int x, int y) {
        int neighbours = 0;
        if (grid.get(x - 1, y) == 1) neighbours++;
        if (grid.get(x + 1, y) == 1) neighbours++;
        if (grid.get(x, y + 1) == 1) neighbours++;
        if (grid.get(x, y - 1) == 1) neighbours++;
        logger.info("block: " + x + "," + y + " has " + neighbours + " neighbours");
        return neighbours;
    }

    public void play() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> tick());
            }
        };
        tickTimer = new Timer("Timer");
        tickTimer.schedule(task, 0, 150);
    }

    public void pause() {
        tickTimer.cancel();
    }
}
