package uk.ac.soton.comp1206.scene;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;
//TODO add a way to revert to the start of a sim, add a tick counter, add a way to
// set the time between ticks, make sure that they can only make changes to the board when the sim has been stopped
/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(ChallengeScene.class);
    protected Game game;
    private BorderPane mainPane;
    private GameBoard board;
    private TextField numInput;
    private Button start;
    private Button stop;

    /**
     * Create a new Single Player challenge scene
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

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("challenge-background");
        root.getChildren().add(challengePane);

        mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        newBoard(10);

        var buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setAlignment(Pos.CENTER);

        start = new Button("Start");
        start.getStyleClass().add("menuItem");
        stop = new Button("Stop");
        stop.getStyleClass().add("menuItem");
        stop.setVisible(false);
        var randomize = new Button("Randomize");
        randomize.getStyleClass().add("menuItem");
        var generate = new Button("Generate");
        generate.getStyleClass().add("menuItem");
        numInput = new TextField();
        numInput.setPromptText("Enter Size of Grid");
        buttonBox.getChildren().add(start);
        buttonBox.getChildren().add(stop);
        buttonBox.getChildren().add(randomize);
        buttonBox.getChildren().add(generate);
        buttonBox.getChildren().add(numInput);

        mainPane.setBottom(buttonBox);

        generate.setOnAction(e -> Platform.runLater(this::updateBoardSize));
        numInput.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                updateBoardSize();
            }
        });
        randomize.setOnAction(e -> Platform.runLater(this::randomizeBoard));
        start.setOnAction(e -> Platform.runLater(this::startSim));
        stop.setOnAction(e -> Platform.runLater(this::pauseSim));
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
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
    //TODO might need to get rid of this
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

    private void newBoard(int size) {
        logger.info("Setting up new board");
        game = new Game(size, size);
        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/1.7f,gameWindow.getWidth()/1.7f);
        mainPane.setCenter(board);
        board.setOnBlockClick(this::blockClicked);
    }

    private void updateBoardSize() {
        logger.info("Updating Board Size...");
        var input = numInput.getText();
        try {
            var size = Integer.parseInt(input);
            if (size > 50) {
                Alert warning = new Alert(Alert.AlertType.WARNING, "Large grids will greatly reduce performance");
                warning.showAndWait();
            }
            newBoard(size);
            logger.info("Board updated to size: " + size);
        } catch (Exception e) {
            logger.info("Failed to update board size due to invalid input: " + input);
            Alert error = new Alert(Alert.AlertType.ERROR, "Unable to Update Board Size \n\n" +
                    e.getMessage() + "\n\nEnsure Value is an Integer");
            error.showAndWait();
        }
    }

    private void randomizeBoard() {
        game.getGrid().randomizeGrid();
    }

    private void startSim() {
        start.setVisible(false);
        stop.setVisible(true);
        game.play();
    }

    private void pauseSim() {
        game.pause();
        start.setVisible(true);
        stop.setVisible(false);
    }

}
