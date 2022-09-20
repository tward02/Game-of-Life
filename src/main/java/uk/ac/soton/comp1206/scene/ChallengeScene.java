package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

import java.io.File;
import java.util.Optional;
//TODO add a way to save and load configurations, add way to calculate how
// many ticks until stabilization, rename/remove various methods and classes to suite theme of game

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    public static final String savePath = System.getProperty("user.home") + "Documents/GoLSaves";

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    private BorderPane mainPane;
    private TextField numInput;
    private Button start;
    private Button stop;
    private Button randomize;
    private Button generate;
    private Button clear;
    private Button revert;
    private Button save;
    private Button load;
    private GameBoard board;
    private TextField tickDisplay;
    private Slider tickSlider;

    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);
        tickDisplay = new TextField();

        newBoard(10);

        var bottomButtonBox = new HBox();
        bottomButtonBox.setSpacing(15);
        bottomButtonBox.setPadding(new Insets(10));
        bottomButtonBox.setAlignment(Pos.CENTER);

        start = new Button("Start");
        start.getStyleClass().add("menuItem");
        stop = new Button("Stop");
        stop.getStyleClass().add("menuItem");
        stop.setVisible(false);
        randomize = new Button("Randomize");
        randomize.getStyleClass().add("menuItem");
        generate = new Button("Generate");
        generate.getStyleClass().add("menuItem");
        numInput = new TextField();
        numInput.setMinSize(0, 60);
        numInput.setPromptText("Enter Size of Grid");
        clear = new Button("Clear");
        clear.getStyleClass().add("menuItem");
        revert = new Button("Revert");
        revert.getStyleClass().add("menuItem");
        revert.setVisible(false);
        bottomButtonBox.getChildren().add(start);
        bottomButtonBox.getChildren().add(stop);
        bottomButtonBox.getChildren().add(randomize);
        bottomButtonBox.getChildren().add(clear);
        bottomButtonBox.getChildren().add(revert);
        bottomButtonBox.getChildren().add(generate);
        bottomButtonBox.getChildren().add(numInput);

        var tickBox = new VBox();
        tickBox.setAlignment(Pos.TOP_RIGHT);
        tickBox.setPadding(new Insets(100, 5, 5, 10));
        tickBox.setSpacing(5);

        var tickText = new Text("Tick: ");

        tickDisplay.getStyleClass().add("tickCounter");
        tickDisplay.setEditable(false);
        tickText.getStyleClass().add("tickCounter");
        var tickBox2 = new HBox();
        tickBox2.setAlignment(Pos.CENTER);
        tickBox2.getChildren().add(tickText);
        tickBox2.getChildren().add(tickDisplay);
        tickBox.getChildren().add(tickBox2);

        var tickBox3 = new HBox();
        tickBox3.setAlignment(Pos.CENTER);
        tickBox3.setPadding(new Insets(5, 5, 5, 10));
        tickBox3.setSpacing(5);
        var tickLengthText = new Text("Time Between Ticks: ");
        var tickTimeDisplay = new TextField();
        tickTimeDisplay.setEditable(false);
        tickBox3.getStyleClass().add("tickCounter");
        tickLengthText.getStyleClass().add("tickCounter");
        tickBox3.getChildren().add(tickLengthText);
        tickBox3.getChildren().add(tickTimeDisplay);
        tickSlider = new Slider(50, 1000, 150);
        tickSlider.setMajorTickUnit(50);
        tickSlider.setSnapToTicks(true);
        tickBox.getChildren().add(tickBox3);
        tickBox.getChildren().add(tickSlider);
        tickTimeDisplay.textProperty().bind(tickSlider.valueProperty().asString());
        game.getTickTime().bind(tickSlider.valueProperty());

        var fileBox = new HBox();
        fileBox.setSpacing(40);
        fileBox.setPadding(new Insets(20, 5, 10, 10));
        fileBox.setAlignment(Pos.CENTER);
        save = new Button("Save");
        save.getStyleClass().add("menuItemSmall");
        load = new Button("Load");
        load.getStyleClass().add("menuItemSmall");
        fileBox.getChildren().add(save);
        fileBox.getChildren().add(load);
        tickBox.getChildren().add(fileBox);

        tickDisplay.textProperty().bind(game.getTickCount().asString());

        mainPane.setBottom(bottomButtonBox);
        mainPane.setRight(tickBox);

        generate.setOnAction(e -> Platform.runLater(this::updateBoardSize));
        numInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateBoardSize();
            }
        });
        randomize.setOnAction(e -> Platform.runLater(this::randomizeBoard));
        start.setOnAction(e -> Platform.runLater(this::startSim));
        stop.setOnAction(e -> Platform.runLater(this::pauseSim));
        clear.setOnAction(e -> Platform.runLater(this::clearBoard));
        revert.setOnAction(e -> Platform.runLater(this::revertBoard));
        save.setOnAction(e -> Platform.runLater(this::saveConfig));
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clicked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        setTickCounter0();
    }

    /**
     * Place holder so that nothing happens when a block is clicked during a sim
     *
     * @param block the game block that was clicked
     */
    private void blockClickedDuringSim(GameBlock block) {
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(10, 10);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

    /**
     * Creates and adds a new board to the main pane
     *
     * @param size x and y dimensions of the board
     */
    private void newBoard(int size) {
        logger.info("Setting up new board");
        game = new Game(size, size);
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 1.7f, gameWindow.getWidth() / 1.7f);
        mainPane.setCenter(board);
        board.setOnBlockClick(this::blockClicked);
        tickDisplay.textProperty().bind(game.getTickCount().asString());
    }

    /**
     * Updates the size of the board from an input field on the window
     */
    private void updateBoardSize() {
        logger.info("Updating Board Size...");
        var input = numInput.getText();
        try {
            var size = Integer.parseInt(input);
            if (size > 50) {
                Alert warning = new Alert(Alert.AlertType.WARNING, "Large grids will greatly reduce performance");
                warning.showAndWait();
            }
            if (size < 3) {
                Alert boardTooSmallError = new Alert(Alert.AlertType.ERROR, "Unable to create grid that small \n\n " +
                        "Please ensure grid size is at least 3");
                boardTooSmallError.showAndWait();
            } else {
                newBoard(size);
                setTickCounter0();
                logger.info("Board updated to size: " + size);
            }
        } catch (Exception e) {
            logger.info("Failed to update board size due to invalid input: " + input);
            Alert error = new Alert(Alert.AlertType.ERROR, "Unable to Update Board Size \n\n" +
                    e.getMessage() + "\n\nEnsure Value is an Integer");
            error.showAndWait();
        }
    }

    /**
     * Randomizes the alive squares on the board
     */
    private void randomizeBoard() {
        game.getGrid().randomizeGrid();
        setTickCounter0();
    }

    /**
     * Starts the simulation and hides all the buttons that could potentially cause problems if pressed when the
     * sim is running
     */
    private void startSim() {
        start.setVisible(false);
        generate.setVisible(false);
        randomize.setVisible(false);
        numInput.setVisible(false);
        clear.setVisible(false);
        tickSlider.setVisible(false);
        save.setVisible(false);
        load.setVisible(false);
        revert.setVisible(true);
        stop.setVisible(true);
        game.getGrid().storeGridAsArrayList();
        board.setOnBlockClick(this::blockClickedDuringSim);
        game.play();
    }

    /**
     * Pauses the simulation and reveals the previously hidden buttons
     */
    private void pauseSim() {
        game.pause();
        start.setVisible(true);
        generate.setVisible(true);
        randomize.setVisible(true);
        numInput.setVisible(true);
        clear.setVisible(true);
        load.setVisible(true);
        save.setVisible(true);
        tickSlider.setVisible(true);
        revert.setVisible(false);
        stop.setVisible(false);
        board.setOnBlockClick(this::blockClicked);
    }

    /**
     * Reverts the board to what it was before the sim started
     */
    private void revertBoard() {
        pauseSim();
        game.getGrid().revertGrid();
        setTickCounter0();
    }

    /**
     * Clears the board of all cells that are alive
     */
    private void clearBoard() {
        game.getGrid().clearGrid();
        setTickCounter0();
    }

    /**
     * Sets the tick counter back to 0
     */
    private void setTickCounter0() {
        Platform.runLater(() -> game.getTickCount().set(0));
    }

    /**
     * saves the current board config to a .gol file
     */
    private void saveConfig() {
        String name = getFileName();
        System.out.println(name);
        var gridList = game.getGrid().getGridAsArrayList();
    }

    /**
     * Prompts the user to enter a file name and checks whether it is valid
     *
     * @return a valid file name or null if they opted to cancel the task
     */
    private String getFileName() {
        var valid = false;
        String result = "";
        while (!valid) {
            valid = true;
            TextInputDialog input = new TextInputDialog();
            input.setTitle("Enter File Name");
            input.setHeaderText("Please Enter File Name");
            input.setHeaderText("Please enter a name containing only numbers and letters");
            input.setContentText("");
            Optional<String> userInput = input.showAndWait();
            if (userInput.isEmpty()) {
                return null;
            }
            result = userInput.get();
            for (char c : result.toCharArray()) {
                if (!Character.isLetterOrDigit(c)) {
                    valid = false;
                }
            }
            if(!valid) {
                Alert error = new Alert(Alert.AlertType.ERROR, "Error: Illegal Character Error\n\nPlease use numbers " +
                        "and letters only");
                error.showAndWait();
                continue;
            }
            if (result.length() == 0) {
                valid = false;
                Alert error = new Alert(Alert.AlertType.ERROR, "Error: Please Enter a File Name");
                error.showAndWait();
                continue;
            }
            if (valid) {
                var file = new File(savePath + "/" + result + ".gol");
                if (file.exists()) {
                    valid = false;
                    Alert error = new Alert(Alert.AlertType.ERROR, "Error: File Already Exists");
                    error.showAndWait();
                }
            }
        }
        return result;
    }

    /**
     * Loads a board configuration from a .gol file
     */
    private void loadConfig() {

    }
}