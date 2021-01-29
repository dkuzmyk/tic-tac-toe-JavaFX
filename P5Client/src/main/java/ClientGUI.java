import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;


public class ClientGUI extends Application {

	Pane loginLayer;
	Pane lobbyLayer;
	Pane gameLayer;
	Pane exitLayer;
	Pane exitLoseLayer;
	Pane exitTieLayer;

	ListView topThree;

	String ip;
	int port;
	int points=0;

	// images for scenes
	Image loginBG;
	ImageView loginPic;
	Image lobbyBG;
	ImageView lobbyPic;
	Image gameBG;
	ImageView gamePic;
	Image exitBG;
	ImageView exitPic;
	Image exitLoseBG;
	ImageView exitLostPic;
	Image exitTieBG;
	ImageView exitTiePic;

	// images for x / o
	Image cross;
	ImageView crossPic;
	Image circle;
	ImageView circlePic;

	Image empty;

	// move position images
	ImageView topLeft;
	ImageView topMid;
	ImageView topRight;
	ImageView midLeft;
	ImageView midMid;
	ImageView midRight;
	ImageView botLeft;
	ImageView botMid;
	ImageView botRight;
	// buttons
	Button exitButton;
	Button exitButton2;
	Button exitButton3;
	Button connectButton;
	Button yesButton;
	Button noButton;
	Button yesButton2;
	Button yesButton3;
	Button noButton2;
	Button noButton3;
	Button easy;
	Button medium;
	Button hard;

	// client
	Client c;

	// checking validity vars
	int portCheck;
	String ipString;

	// textfields
	TextField ipField;
	TextField portField;
	TextField score;

	// hashmap
	HashMap<String, Scene> sceneMap;
	ArrayList<ImageView> images;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Tic Tac Toe");

		Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid port or IP Input", ButtonType.YES);

		loginLayer = new Pane();	// setting up the first scene
		loginBG = new Image("file:src/main/resources/loginBG.png");
		loginPic = new ImageView();
		loginPic.setImage(loginBG);
		loginLayer.getChildren().add(loginPic);	// add background

		portField = new TextField("5555");
		portField.setPrefSize(150,50);
		portField.setLayoutX(250);
		portField.setLayoutY(335);
		loginLayer.getChildren().add(portField);

		ipField = new TextField("127.0.0.1");
		ipField.setPrefSize(150,50);
		ipField.setLayoutX(250);
		ipField.setLayoutY(205);
		loginLayer.getChildren().add(ipField);

		exitButton = new Button();
		exitButton.setPadding(Insets.EMPTY);
		exitButton.setGraphic(new ImageView("file:src/main/resources/exit.png"));
		exitButton.setLayoutX(270);
		exitButton.setLayoutY(470);
		loginLayer.getChildren().add(exitButton);
		// exit button action
		exitButton.setOnAction(exit->{
			System.exit(0);
			Platform.exit();
		});

		// difficulty management
		easy = new Button();
		medium = new Button();
		hard = new Button();
		easy.setOnAction(x->{
			// start easy mode game
			System.out.println("Selected easy mode");
			c.setDiff(0);
			primaryStage.setScene(sceneMap.get("goGame"));
		});

		medium.setOnAction(y->{
			// start normal mode game
			System.out.println("Selected normal mode");
			c.setDiff(1);
			primaryStage.setScene(sceneMap.get("goGame"));
		});

		hard.setOnAction(z->{
			// start hard mode game
			System.out.println("Selected hard mode");
			c.setDiff(2);
			primaryStage.setScene(sceneMap.get("goGame"));
		});

		// images with functions to send to client c
		topLeft = new ImageView();
		topMid = new ImageView();
		topRight = new ImageView();
		midLeft = new ImageView();
		midMid = new ImageView();
		midRight = new ImageView();
		botLeft = new ImageView();
		botMid = new ImageView();
		botRight = new ImageView();

		images = new ArrayList<ImageView>();
		images.add(topLeft);
		images.add(topMid);
		images.add(topRight);
		images.add(midLeft);
		images.add(midMid);
		images.add(midRight);
		images.add(botLeft);
		images.add(botMid);
		images.add(botRight);

		connectButton = new Button();
		connectButton.setPadding(Insets.EMPTY);
		connectButton.setGraphic(new ImageView("file:src/main/resources/connect.png"));
		connectButton.setLayoutX(80);
		connectButton.setLayoutY(470);
		loginLayer.getChildren().add(connectButton);
		// connect button action
		connectButton.setOnAction(connect->{

			try {
				portCheck = Integer.parseInt(portField.getText());    // check port validity
				ipString = ipField.getText();							// get ip string
			}
			catch(Exception e){
				alert.showAndWait();
			}

			if((portCheck > 0) && (portCheck < 65535) && ipCheck(ipString)) {
				ip = ipString;
				port = portCheck;
				// set port for the connection ..
				System.out.println("Received ip: "+ip);
				System.out.println("Received port: "+port);

				c = new Client(port, ip, topThree, images, primaryStage, sceneMap.get("goGame"),
						sceneMap.get("exitWin"), sceneMap.get("exitLose"), sceneMap.get("exitTie"), score);
				c.start();
				primaryStage.setScene(sceneMap.get("goLobby"));
			}
			else alert.showAndWait();
		});

		// exit button action
		yesButton2 = new Button();
		yesButton2.setOnAction(exit->{
			// say to server to load lobby scene
			System.out.println("Loading lobby..");
			clearMoves();
			primaryStage.setScene(sceneMap.get("goLobby"));
		});

		yesButton3 = new Button();
		yesButton3.setOnAction(exit->{
			// say to server to load lobby scene
			System.out.println("Loading lobby..");
			clearMoves();
			primaryStage.setScene(sceneMap.get("goLobby"));
		});

		// exit button action
		yesButton = new Button();
		yesButton.setOnAction(exit->{
			// say to server to load lobby scene
			System.out.println("Loading lobby..");
			clearMoves();
			primaryStage.setScene(sceneMap.get("goLobby"));
		});

		// debug button to check game grid
		Button gameDebug = new Button("Game ui");
		gameDebug.setPadding(Insets.EMPTY);
		gameDebug.setLayoutY(580);
		gameDebug.setLayoutX(0);
		loginLayer.getChildren().add(gameDebug);
		gameDebug.setOnAction(asd->{primaryStage.setScene(sceneMap.get("goGame"));});

		// debug button to check exit screen
		Button gameDebug2 = new Button("exit ui");
		gameDebug2.setPadding(Insets.EMPTY);
		gameDebug2.setLayoutY(580);
		gameDebug2.setLayoutX(50);
		loginLayer.getChildren().add(gameDebug2);
		gameDebug2.setOnAction(asd2->{primaryStage.setScene(sceneMap.get("exitWin"));});

		Button gameDebug3 = new Button("exit2 ui");
		gameDebug3.setPadding(Insets.EMPTY);
		gameDebug3.setLayoutY(580);
		gameDebug3.setLayoutX(100);
		loginLayer.getChildren().add(gameDebug3);
		gameDebug3.setOnAction(asd3->{primaryStage.setScene(sceneMap.get("exitLose"));});

		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("goLobby",  lobbyClientGui());
		sceneMap.put("goGame",  gameClientGui());
		sceneMap.put("exitWin",  exitWinClientGui());
		sceneMap.put("exitLose",  exitLoseClientGui());
        sceneMap.put("exitTie",  exitTieGui());

		Scene scene = new Scene(loginLayer,500,600);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public Scene lobbyClientGui(){
		lobbyLayer = new Pane();
		lobbyBG = new Image("file:src/main/resources/lobbyBG.png");
		lobbyPic = new ImageView();
		lobbyPic.setImage(lobbyBG);
		lobbyLayer.getChildren().add(lobbyPic);

		// listView
		topThree = new ListView();
		topThree.setPrefSize(300,300);
		topThree.setLayoutX(445);
		topThree.setLayoutY(290);
		lobbyLayer.getChildren().add(topThree);

		// exit button
		exitButton2 = new Button();
		exitButton2.setPadding(Insets.EMPTY);
		exitButton2.setGraphic(new ImageView("file:src/main/resources/exit.png"));
		exitButton2.setLayoutX(630);
		exitButton2.setLayoutY(120);
		lobbyLayer.getChildren().add(exitButton2);
		// exit button action
		exitButton2.setOnAction(exit->{
			System.exit(0);
			Platform.exit();
		});

		// difficulty buttons

		easy.setPadding(Insets.EMPTY);
		easy.setGraphic(new ImageView("file:src/main/resources/easy.png"));
		easy.setLayoutX(69);
		easy.setLayoutY(300);


		medium.setPadding(Insets.EMPTY);
		medium.setGraphic(new ImageView("file:src/main/resources/medium.png"));
		medium.setLayoutX(69);
		medium.setLayoutY(460);


		hard.setPadding(Insets.EMPTY);
		hard.setGraphic(new ImageView("file:src/main/resources/hard.png"));
		hard.setLayoutX(69);
		hard.setLayoutY(620);

		lobbyLayer.getChildren().add(easy);
		lobbyLayer.getChildren().add(medium);
		lobbyLayer.getChildren().add(hard);

		// score
		score = new TextField();
		score.setPrefSize(150,50);
		score.setLayoutX(520);
		score.setLayoutY(660);
		int myPoints = 0; // sub with points from gameinfo =========
		score.setText(""+0);
		score.setDisable(true);
		lobbyLayer.getChildren().add(score);

		return new Scene(lobbyLayer, 800, 900);
	}

	public Scene gameClientGui() {
		gameLayer = new Pane();
		gameBG = new Image("file:src/main/resources/gameBG.png");
		gamePic = new ImageView();
		gamePic.setImage(gameBG);
		gameLayer.getChildren().add(gamePic);

		// image management
		empty = new Image("file:src/main/resources/empty.png");
		Image cross = new Image("file:src/main/resources/X.png");	// for server moves
		Image circle = new Image("file:src/main/resources/O.png");

		topLeft.setImage(empty);
		topLeft.setLayoutX(25);
		topLeft.setLayoutY(120);
		topLeft.setPickOnBounds(true);
		topLeft.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				topLeft.setDisable(true);
				topLeft.setImage(circle);
				c.sendMove(0);
				// add circle to the score grid
			}
		});

		topMid.setImage(empty);
		topMid.setLayoutX(290);
		topMid.setLayoutY(120);
		topMid.setPickOnBounds(true);
		topMid.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				topMid.setImage(circle);
				topMid.setDisable(true);
				c.sendMove(1);
			}
		});

		topRight.setImage(empty);
		topRight.setLayoutX(555);
		topRight.setLayoutY(120);
		topRight.setPickOnBounds(true);
		topRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				topRight.setDisable(true);
				topRight.setImage(circle);
				c.sendMove(2);
			}
		});

		midLeft.setImage(empty);
		midLeft.setLayoutX(25);
		midLeft.setLayoutY(390);
		midLeft.setPickOnBounds(true);
		midLeft.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				midLeft.setDisable(true);
				midLeft.setImage(circle);
				c.sendMove(3);
				// add circle to the score grid
			}
		});

		midMid.setImage(empty);
		midMid.setLayoutX(290);
		midMid.setLayoutY(390);
		midMid.setPickOnBounds(true);
		midMid.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				midMid.setDisable(true);
				midMid.setImage(circle);
				c.sendMove(4);

				// add circle to the score grid
			}
		});

		midRight.setImage(empty);
		midRight.setLayoutX(555);
		midRight.setLayoutY(390);
		midRight.setPickOnBounds(true);
		midRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				midRight.setDisable(true);
				midRight.setImage(circle);
				c.sendMove(5);
				// add circle to the score grid
			}
		});

		botLeft.setImage(empty);
		botLeft.setLayoutX(25);
		botLeft.setLayoutY(660);
		botLeft.setPickOnBounds(true);
		botLeft.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				botLeft.setDisable(true);
				botLeft.setImage(circle);
				c.sendMove(6);
				// add circle to the score grid
			}
		});

		botMid.setImage(empty);
		botMid.setLayoutX(290);
		botMid.setLayoutY(660);
		botMid.setPickOnBounds(true);
		botMid.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				botMid.setDisable(true);
				botMid.setImage(circle);
				c.sendMove(7);
				// add circle to the score grid
			}
		});

		botRight.setImage(empty);
		botRight.setLayoutX(555);
		botRight.setLayoutY(660);
		botRight.setPickOnBounds(true);
		botRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// if spot occupied
				// say u can't use this place
				// else
				botRight.setDisable(true);
				botRight.setImage(circle);
				c.sendMove(8);
				// add circle to the score grid
			}
		});


		//gameLayer.getChildren().addAll(midLeft);
		gameLayer.getChildren().addAll(topLeft, topMid, topRight, midLeft, midMid, midRight, botLeft, botMid, botRight);
		return new Scene(gameLayer, 800, 900);
	}

	public Scene exitWinClientGui(){
		exitLayer = new Pane();
		exitBG = new Image("file:src/main/resources/endBG.png");
		exitPic = new ImageView();
		exitPic.setImage(exitBG);
		exitLayer.getChildren().add(exitPic);

		yesButton.setPadding(Insets.EMPTY);
		yesButton.setGraphic(new ImageView("file:src/main/resources/yes.png"));
		yesButton.setLayoutX(220);
		yesButton.setLayoutY(500);
		exitLayer.getChildren().add(yesButton);

		noButton = new Button();
		noButton.setPadding(Insets.EMPTY);
		noButton.setGraphic(new ImageView("file:src/main/resources/no.png"));
		noButton.setLayoutX(420);
		noButton.setLayoutY(500);
		exitLayer.getChildren().add(noButton);
		// exit button action
		noButton.setOnAction(exit->{
			System.out.println("Exiting..");
			System.exit(0);
			Platform.exit();
		});

		return new Scene(exitLayer, 800, 900);
	}

	public Scene exitLoseClientGui(){
		exitLoseLayer = new Pane();
		exitLoseBG = new Image("file:src/main/resources/endLoseBG.png");
		exitLostPic = new ImageView();
		exitLostPic.setImage(exitLoseBG);
		exitLoseLayer.getChildren().add(exitLostPic);

		yesButton2.setPadding(Insets.EMPTY);
		yesButton2.setGraphic(new ImageView("file:src/main/resources/yes.png"));
		yesButton2.setLayoutX(220);
		yesButton2.setLayoutY(500);
		exitLoseLayer.getChildren().add(yesButton2);

		noButton2 = new Button();
		noButton2.setPadding(Insets.EMPTY);
		noButton2.setGraphic(new ImageView("file:src/main/resources/no.png"));
		noButton2.setLayoutX(420);
		noButton2.setLayoutY(500);
		exitLoseLayer.getChildren().add(noButton2);
		// exit button action
		noButton2.setOnAction(exit->{
			System.out.println("Exiting..");
			System.exit(0);
			Platform.exit();
		});

		return new Scene(exitLoseLayer, 800, 900);
	}

	public Scene exitTieGui(){
		exitTieLayer = new Pane();
		exitTieBG = new Image("file:src/main/resources/endTieBG.png");
		exitTiePic = new ImageView();
		exitTiePic.setImage(exitTieBG);
		exitTieLayer.getChildren().add(exitTiePic);

		yesButton3.setPadding(Insets.EMPTY);
		yesButton3.setGraphic(new ImageView("file:src/main/resources/yes.png"));
		yesButton3.setLayoutX(220);
		yesButton3.setLayoutY(500);
		exitTieLayer.getChildren().add(yesButton3);

		noButton3 = new Button();
		noButton3.setPadding(Insets.EMPTY);
		noButton3.setGraphic(new ImageView("file:src/main/resources/no.png"));
		noButton3.setLayoutX(420);
		noButton3.setLayoutY(500);
		exitTieLayer.getChildren().add(noButton3);
		// exit button action
		noButton3.setOnAction(exit->{
			System.out.println("Exiting..");
			System.exit(0);
			Platform.exit();
		});

		return new Scene(exitTieLayer, 800, 900);
	}

	public static boolean ipCheck (String ip) {
		try {
			if ( ip == null || ip.isEmpty() ) {
				return false;
			}
			String[] parts = ip.split( "\\." );
			if ( parts.length != 4 ) {
				return false;
			}
			for ( String s : parts ) {
				int i = Integer.parseInt( s );
				if ( (i < 0) || (i > 255) ) {
					return false;
				}
			}
			if ( ip.endsWith(".") ) {
				return false;
			}
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void clearMoves(){
		topLeft.setImage(empty);
		topMid.setImage(empty);
		topRight.setImage(empty);
		midLeft.setImage(empty);
		midMid.setImage(empty);
		midRight.setImage(empty);
		botLeft.setImage(empty);
		botMid.setImage(empty);
		botRight.setImage(empty);
		topLeft.setDisable(false);
		topMid.setDisable(false);
		topRight.setDisable(false);
		midLeft.setDisable(false);
		midMid.setDisable(false);
		midRight.setDisable(false);
		botLeft.setDisable(false);
		botMid.setDisable(false);
		botRight.setDisable(false);
	}


}
