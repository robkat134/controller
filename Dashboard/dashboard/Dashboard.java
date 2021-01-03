package dashboard;

import bussimulator.Runner;
import infoborden.Infobord;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import mockDatabaseLogger.ArrivaLogger;

public class Dashboard extends Application {
	private GridPane gridPane;

    private void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}
    
    private void startBord(String halte, String richting) {
		final Infobord infobord = new Infobord(halte,richting);
		Platform.runLater(new Runnable() {
			public void run() {
				infobord.start(new Stage());
			}
		});
    }
	private void startAlles() {
		thread(new Runner(),false); 
	}

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {

		TextField busStopDefault = new TextField("A-Z");
		TextField busDirectionDefault = new TextField("1 of -1");

		// Create pane
		createPane();

		// Add buttons
		addButton("Start Bord", 1).setOnAction( e -> {
			startBord(busStopDefault.getText(), busDirectionDefault.getText());
		});
		addButton("Start", 2).setOnAction( e -> {
			startAlles();
		});
		addButton("Start Logger", 3).setOnAction( e -> {
			thread(new ArrivaLogger(), false);
		});

		// Add text input
		addTextInput("Halte:", 0, busStopDefault);
		addTextInput("Richting:", 1, busDirectionDefault);

		// Show pane
		showPane("BusSimulatie control-Center", primaryStage);
	}

	// Create a pane and set its properties
	private void createPane() {
		this.gridPane = new GridPane();
		this.gridPane.setAlignment(Pos.CENTER);
		this.gridPane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
		this.gridPane.setHgap(5.5);
		this.gridPane.setVgap(5.5);
	}

	// Add button to pane and return the button object
	private Button addButton(String buttonName, int horizontalPosition) {
		Button button = new Button(buttonName);
		this.gridPane.add(button, horizontalPosition, 5);
		GridPane.setHalignment(button, HPos.LEFT);
		return button;
	}

	// Add text input to pane
	private void addTextInput(String labelName, int yCoordinate, TextField defaultInput) {
		this.gridPane.add(new Label(labelName), 0, yCoordinate);
		this.gridPane.add(defaultInput, 1, yCoordinate);
	}

	// Show pane on screen
	private void showPane(String title, Stage primaryStage) {
		Scene scene = new Scene(this.gridPane);
		primaryStage.setTitle(title);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * The main method is only needed for the IDE with limited
	 * JavaFX support. Not needed for running from the command line.
	 */
	public static void main(String[] args) {
		launch(args);
	}
} 