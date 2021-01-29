import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.util.HashMap;


public class ServerGUI extends Application {

	ListView serverLog;
	ListView clientLog;

	Pane loginLayer;
	Pane lobbyLayer;
	int port;

	// images for scenes
	Image loginBG;
	ImageView loginPic;
	Image lobbyBG;
	ImageView lobbyPic;

	// buttons
	Button exitButton;
	Button exitButton2;
	Button connectButton;
	Button onButton;

	// textfields
	TextField portField;

	// hashmap
	HashMap<String, Scene> sceneMap;
	Server server;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		primaryStage.setTitle("Tic Tac Toe Server");

		Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid port Input", ButtonType.YES);

		loginLayer = new Pane();	// setting up the first scene
		loginBG = new Image("file:src/main/resources/serverlogin.png");
		loginPic = new ImageView();
		loginPic.setImage(loginBG);
		loginLayer.getChildren().add(loginPic);	// add background

		portField = new TextField("5555");
		portField.setPrefSize(150,50);
		portField.setLayoutX(300);
		portField.setLayoutY(205);
		loginLayer.getChildren().add(portField);

		exitButton = new Button();
		exitButton.setPadding(Insets.EMPTY);
		exitButton.setGraphic(new ImageView("file:src/main/resources/exit.png"));
		exitButton.setLayoutX(300);
		exitButton.setLayoutY(320);
		loginLayer.getChildren().add(exitButton);
		// exit button action
		exitButton.setOnAction(exit->{
			System.exit(0);
			Platform.exit();
		});

		// listviews
		serverLog = new ListView();
		serverLog.setPrefSize(200, 380);
		serverLog.setLayoutY(220);
		serverLog.setLayoutX(60);

		clientLog = new ListView();
		clientLog.setPrefSize(200, 380);
		clientLog.setLayoutY(220);
		clientLog.setLayoutX(342);

		connectButton = new Button();
		connectButton.setPadding(Insets.EMPTY);
		connectButton.setGraphic(new ImageView("file:src/main/resources/connect.png"));
		connectButton.setLayoutX(120);
		connectButton.setLayoutY(320);
		loginLayer.getChildren().add(connectButton);
		// connect button action
		connectButton.setOnAction(connect->{

			try {
				port = Integer.parseInt(portField.getText());    // get port integer
			}
			catch(Exception e){
				alert.showAndWait();
			}

			if((port > 0) && (port < 65535)) {
				System.out.println("Received port: " + port);
				// set port for the connection ..
				primaryStage.setScene(sceneMap.get("goLobby"));
			}
			// else error port
			else{
				alert.showAndWait();
			}
		});



		sceneMap = new HashMap<String, Scene>();
		sceneMap.put("goLobby",  lobbyServerGui());

		Scene scene = new Scene(loginLayer,600,600);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.sizeToScene();
		primaryStage.show();
	}

	public Scene lobbyServerGui(){
		lobbyLayer = new Pane();
		lobbyBG = new Image("file:src/main/resources/serverlobby.png");
		lobbyPic = new ImageView();
		lobbyPic.setImage(lobbyBG);
		lobbyLayer.getChildren().add(lobbyPic);

		exitButton2 = new Button();
		exitButton2.setPadding(Insets.EMPTY);
		exitButton2.setGraphic(new ImageView("file:src/main/resources/exit.png"));
		exitButton2.setLayoutX(265);
		exitButton2.setLayoutY(120);
		exitButton2.setOnAction(exit2->{
			System.exit(0);
			Platform.exit();
		});

		onButton = new Button();
		onButton.setPadding(Insets.EMPTY);
		onButton.setGraphic(new ImageView("file:src/main/resources/turnonbutton.png"));
		onButton.setLayoutX(265);
		onButton.setLayoutY(120);
		lobbyLayer.getChildren().add(onButton);
		onButton.setOnAction(exit2->{
			lobbyLayer.getChildren().remove(onButton);
			lobbyLayer.getChildren().add(exitButton2);

			// this is where the server turns on the threads connection
			// to the previously set port

			server = new Server(serverLog, clientLog, port);

		});

		lobbyLayer.getChildren().add(serverLog);
		lobbyLayer.getChildren().add(clientLog);

		return new Scene(lobbyLayer, 600, 650);
	}

}
