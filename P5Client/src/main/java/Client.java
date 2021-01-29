import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.util.ArrayList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/*
 * Authors: Tarun Maddineni, Aditya Vandanapu, Regie Sales, Dmytro Kuzmyk
 * netIDs: tmaddi2, avanda7, rsales3, dkuzmy3
 * Description: This project contains the client code for the game Tic-Tac-Toe. The client connects to the server through the use of a Socket
 *              and chooses a difficulty to play Tic-Tac-Toe. They then play against the server with however many games they want. They can
 *              see the top 3 scores on the server.
 */

/*
 *  This class contains the client's logic for creating a new connection and passing info between the server and the client.
 */
public class Client extends Thread{
    Socket clientSocket;
    ObjectOutputStream out;
    ObjectInputStream in;
    GameInfo game;
    Integer port;
    String ip;
    Integer id;
    Stage stage;
    Scene exitWin;
    Scene exitLose;
    Scene gameScene;
    Scene exitTie;
    ListView top3scores;
    ArrayList<Button> grid;
    ArrayList<ImageView> graphics;
    boolean hasID = false;
    int points=0;
    TextField score;

    // Constructor that takes ip, port, and GUI elements to create the client
    Client(Integer p, String i, ListView l, ArrayList<ImageView> gr, Stage s, Scene s3 , Scene s4, Scene s5, Scene et, TextField pts){
        port = p;
        ip = i;
        top3scores = l;
        //grid = g;
        graphics = gr;
        stage = s;
        gameScene = s3;
        exitWin = s4;
        exitLose = s5;
        exitTie = et;
        id = null;
        game = new GameInfo();
        score = pts;
    }


    // Sets the difficulty and sends the move
    public void setDiff(Integer i){
        this.game.messageID = 1;
        this.game.difficulty = i;
        try{
            this.out.writeObject(this.game);
            this.out.reset();
        }
        catch(Exception e){
            System.out.println("Could not set difficulty");
        }
    }


    // Sends the chosen move of the player to the server
    public void sendMove(Integer i){
        this.game.board[i] = "O";
        this.game.messageID = 2;
        try{
            this.out.writeObject(this.game);
            this.out.reset();
        }
        catch(Exception e){
            System.out.println("Could not send move");
        }
    }

    // Run the client by attempting to connect to the server and reads/sends input from/to server
    public void run(){
        try{
            clientSocket = new Socket(ip, port);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            clientSocket.setTcpNoDelay(true);
        }
        catch(Exception e){
            System.out.println("Streams not open");
            Platform.runLater(()->{ 
                                    Alert alert = new Alert(Alert.AlertType.ERROR, "Streams not open. No connection to server.", ButtonType.OK);
                                    alert.showAndWait();
                                    PauseTransition p2 = new PauseTransition(Duration.millis(3000));
                                    Platform.exit();});

        }

        GameInfo tempGame = null;
        // Accepts game information and responds accordingly
        while(true){

            try{
                tempGame = (GameInfo)this.in.readObject();
            }
            catch(Exception e){
                System.out.println("Could not receive information from server");
                break;
            }

            if(tempGame.messageID == 1){
                Platform.runLater(()->{top3scores.getItems().clear();});
                Platform.runLater(()->{
                    top3scores.getItems().add("                            You are player "+id);
                    top3scores.getItems().add("-----------------------------------------------------------");
                    top3scores.getItems().add(" ");

                });
                for(int i = 0; i < tempGame.top3ID.size(); i++){
                    int pid = tempGame.top3ID.get(i);
                    int score = tempGame.top3points.get(i);
                    Platform.runLater(()->{top3scores.getItems().add("Player " + pid + ":    Wins: " + score);});
                    if(!hasID){this.id = tempGame.idClient; hasID=true;}
                    //System.out.println("id: "+this.id);
                }

            }

            // Reflect the move made by the CPU
            else if(tempGame.messageID == 2){
                this.game.board = tempGame.board;
                Image x = new Image("file:src/main/resources/X.png");
                //Image o = new Image("file:src/main/resources/O.png");
                //Image b = new Image("file:src/main/resources/empty.png");

                for(int i = 0; i < 9; i++){
                    ImageView graphic = this.graphics.get(i);
                    //Button g = this.grid.get(i);
                    if(tempGame.board[i].equals("X")){
                        Platform.runLater(()->{
                            graphic.setImage(x);
                            graphic.setDisable(true);
                        });
                    }
                    /*else if(tempGame.board[i].equals("O")){
                        Platform.runLater(()->{graphic.setImage(o);});
                    }
                    // Reenable blank squares
                    else{
                        Platform.runLater(()->{graphic.setImage(b);});
                        //Platform.runLater(()->{g.setDisable(false);});
                    }*/
                }

            }
            else{
                this.game.board = tempGame.board;
                Image x = new Image("file:src/main/resources/X.png");
                //Image o = new Image("file:src/main/resources/O.png");
                //Image b = new Image("file:src/main/resources/empty.png");

                for(int i = 0; i < 9; i++){
                    ImageView graphic = this.graphics.get(i);
                    //Button g = this.grid.get(i);
                    if(tempGame.board[i].equals("X")){
                        Platform.runLater(()->{
                            graphic.setImage(x);
                            graphic.setDisable(true);
                        });
                    }
                    /*else if(tempGame.board[i].equals("O")){
                        Platform.runLater(()->{graphic.setImage(o);});
                    }
                    // Reenable blank squares
                    else{
                        Platform.runLater(()->{graphic.setImage(b);});
                    }*/
                }

                String temp[] = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
                this.game.board = temp;

                // Pause for a moment before going to the play again scene
                PauseTransition p = new PauseTransition(Duration.millis(3000));
                if (tempGame.whoWon == 1) {
                    p.setOnFinished(e->{stage.setScene(exitWin); points++; score.setText(""+points);});
                    p.playFromStart();
                    //System.out.println("this is whowon == 1");

                }
                else if(tempGame.whoWon == 0){
                    p.setOnFinished(e->{stage.setScene(exitLose);});
                    p.playFromStart();
                    //System.out.println("this is whowon == 0");
                }
                else{
                p.setOnFinished(e -> {stage.setScene(exitTie);});
                p.playFromStart();
                //System.out.println("this is whowon == else");
                }
            }
        }
    }

}
