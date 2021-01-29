import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import javafx.application.Platform;
import javafx.scene.control.ListView;
import java.util.ArrayList;
import java.util.Random;

/*
 * Authors: Tarun Maddineni, Aditya Vandanapu, Dmytro Kuzmyk, Reggie Sales
 * netIDs: tmaddi2, avanda7, dkuzmy3, rsales3
 * Description: This project contains code for a server that hosts games of Tic-Tac-Toe. The server accepts connections from new
 *              clients on the local host. It receives input from clients and plays games of Tic-Tac-Toe against them based on a
 *              difficulty level they choose. It keeps tracks of things that happen on the server and the top 3 scores of all its
 *              clients. The server uses threads to handle the clients separately and updates its own GUI separately.
 */

/*
 *  This class contains the server's logic for creating a new connection and passing info between the server and the clients.
 */
public class Server{

    TheServer server;
    Integer port;
    ArrayList<ClientThread> clientList = new ArrayList<ClientThread>();
    ArrayList<Integer> clientPoints = new ArrayList<Integer>();
    ArrayList<Integer> topPos = new ArrayList<Integer>();
    ListView console;
    ListView top3;
    int count;
    static FindNextMove cpu;

    // Constructor that takes two ListViews and an Integer as inputs. Creates a new TheServer object and starts it.
    Server(ListView l1, ListView l2, Integer p){
        server = new TheServer();
        port = p;
        console = l1;
        top3 = l2;
        count = 1;
        topPos.add(-1);
        topPos.add(-1);
        topPos.add(-1);
        cpu = new FindNextMove();
        cpu.start();
        server.start();
    }

    // Inner class that actually contains the server logic to send and receive game info. Creates the separate client threads.
    public class TheServer extends Thread{

        public void run(){
            ServerSocket mysocket = null;
            // Try to launch server on the specified port
            try{
                mysocket = new ServerSocket(port);
                Platform.runLater(()->{console.getItems().add("Server running on port "+port);});
            }
            catch(Exception e){
                Platform.runLater(()->{console.getItems().add("The server socket did not launch!");});
            }
            // Continue to accept clients and add them to the array list
            while(true){
                try{
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    clientList.add(c);
                    clientPoints.add(0);
                    c.start();
                    Platform.runLater(()->{console.getItems().add("Client: " + c.id + " joined the server!");});
                    //c.out.writeObject(c.id);    // send id to the individual client
                    count++;

                }
                catch(Exception e){
                    Platform.runLater(()->{console.getItems().add("Client could not join");});
                }
            }
        }

    }

    // Inner class that will keep track of the client connection on its own thread
    public class ClientThread extends Thread{
        Socket connection;
        Integer id;
        GameInfo game;
        ObjectInputStream in;
        ObjectOutputStream out;

        // Constructor that creates a new ClientThread given a socket
        ClientThread(Socket s, Integer n){
            this.connection = s;
            this.id = n;
            game = new GameInfo();
        }

        synchronized void updateScores(){

            topPos.set(0, -1);
            topPos.set(1, -1);
            topPos.set(2, -1);

            // Update which players are on top
            for(int i = 0; i < clientList.size(); i++){
                // If 3 players or less were on the server

                if(topPos.get(0) == -1){
                    topPos.set(0, i + 1);
                }
                else if(topPos.get(1) == -1){
                    if(clientPoints.get(i) > clientPoints.get(topPos.get(0) - 1)){
                        topPos.set(1, topPos.get(0));
                        topPos.set(0, i + 1);
                    }
                    else{
                        topPos.set(1, i + 1);
                    }

                }
                else if(topPos.get(2) == -1){
                    if(clientPoints.get(i) > clientPoints.get(topPos.get(0) - 1)){
                        topPos.set(2, topPos.get(1));
                        topPos.set(1, topPos.get(0));
                        topPos.set(0, i + 1);
                    }
                    else if(clientPoints.get(i) > clientPoints.get(topPos.get(1) - 1)){
                        topPos.set(2, topPos.get(1));
                        topPos.set(1, i + 1);
                    }
                    else{
                        topPos.set(2, i + 1);
                    }
                }
                // The top 3 spots are not empty
                else{
                    if(clientPoints.get(i) > clientPoints.get(topPos.get(0) - 1)){
                        topPos.set(2, topPos.get(1));
                        topPos.set(1, topPos.get(0));
                        topPos.set(0, i + 1);
                    }
                    else if(clientPoints.get(i) > clientPoints.get(topPos.get(1) - 1)){
                        topPos.set(2, topPos.get(1));
                        topPos.set(1, i + 1);
                    }
                    else if(clientPoints.get(i) > clientPoints.get(topPos.get(2) - 1)){
                        topPos.set(2, i + 1);
                    }
                    else{
                        continue;
                    }
                }
            }

            Platform.runLater(()->{top3.getItems().clear();});
            for(int i = 0; i < 3; i++){
                int j = topPos.get(i);
                if(j == -1){
                    break;
                }
                int k = clientPoints.get(j - 1);
                Platform.runLater(()->{top3.getItems().add("Player " + j + ": "
                        + k);});
            }

            GameInfo temp = new GameInfo();
            temp.messageID = 1;
            for(int i = 0; i < 3; i++){
                if(topPos.get(i) == -1){
                    break;
                }
                temp.top3ID.add(topPos.get(i));
                temp.top3points.add(clientPoints.get(topPos.get(i) - 1));
            }
            for(int i = 0; i < clientList.size(); i++){
                ClientThread t = clientList.get(i);
                if(t == null){
                    continue;
                }
                try{
                    temp.idClient = clientList.size();
                    t.out.writeObject(temp);
                    t.out.reset();
                }
                catch(Exception e){
                    Platform.runLater(()->{console.getItems().add("Could not send top 3 scores");});
                }
            }
        }

        // Check if a player/cpu has won
        boolean checkWin(String s){
            String board[] = this.game.board;
            if((board[0].equals(s)) && (board[1].equals(s)) && (board[2].equals(s))){
                return true;
            }
            else if((board[3].equals(s)) && (board[4].equals(s)) && (board[5].equals(s))){
                return true;
            }
            else if((board[6].equals(s)) && (board[7].equals(s)) && (board[8].equals(s))){
                return true;
            }
            else if((board[0].equals(s)) && (board[3].equals(s)) && (board[6].equals(s))){
                return true;
            }
            else if((board[1].equals(s)) && (board[4].equals(s)) && (board[7].equals(s))){
                return true;
            }
            else if((board[2].equals(s)) && (board[5].equals(s)) && (board[8].equals(s))){
                return true;
            }
            else if((board[0].equals(s)) && (board[4].equals(s)) && (board[8].equals(s))){
                return true;
            }
            else if((board[2].equals(s)) && (board[4].equals(s)) && (board[6].equals(s))){
                return true;
            }
            else{
                return false;
            }
        }

        // Check if the game resulted in a tie
        boolean tied(){
            for(int i = 0; i < 9; i++){
                if(this.game.board[i].equals("b")){
                    return false;
                }
            }
            return true;
        }

        // Use algorithm to make the next move for the server
        synchronized void nextMove(){
            cpu.b = this.game.board;
            cpu.d = this.game.difficulty;
            cpu.inUse = true;
            // Until a move has been made, don't return
            while(cpu.inUse){
                System.out.print("");
            }
            this.game.board = cpu.b;
        }

        // Run the client thread
        public void run(){
            try{
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            // Update the top 3 scores on server
            updateScores();

            // Read in game information and respond accordingly
            while(true){
                GameInfo tempGame;
                try{
                    tempGame = (GameInfo)this.in.readObject();
                    // If the client has chosen a difficulty
                    if(tempGame.messageID == 1){
                        this.game.difficulty = tempGame.difficulty;
                        this.game.whoWon = -1;
                        String temp[] = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
                        this.game.board = temp;
                        this.game.messageID = 2;
                        // CPU makes first move and sends game to client
                        nextMove();
                        try{
                            this.out.writeObject(this.game);
                            this.out.reset();
                        }
                        catch(Exception e){
                            Platform.runLater(()->{console.getItems().add("Could not send move to client " + this.id);});
                        }
                    }
                    // If the client has made a move
                    else{
                        //System.out.println("Received move");
                        this.game.board = tempGame.board;
                        this.game.messageID = 2;
                        // Check if the player won
                        if(checkWin("O")){
                            this.game.whoWon = 1;
                            Platform.runLater(()->{console.getItems().add("Client " + this.id + " won their game against the server");});
                            synchronized(clientPoints){
                                clientPoints.set(this.id - 1, clientPoints.get(this.id - 1) + 1);
                            }
                            updateScores();
                            this.game.messageID = 3;
                            try{
                                this.out.writeObject(this.game);
                                this.out.reset();
                            }
                            catch(Exception e){
                                Platform.runLater(()->{console.getItems().add("Client " + this.id + " could not receive win");});
                            }
                            continue;
                        }
                        // Check if the game is tied
                        if(tied()){
                            this.game.whoWon = 2;
                            Platform.runLater(()->{console.getItems().add("Client " + this.id + " tied their game against the server");});
                            this.game.messageID = 3;
                            try{
                                this.out.writeObject(this.game);
                                this.out.reset();
                            }
                            catch(Exception e){
                                Platform.runLater(()->{console.getItems().add("Client " + this.id + " could not receive tie");});
                            }
                            continue;
                        }
                        // Make the next move
                        nextMove();
                        // Check if CPU won
                        if(checkWin("X")){
                            this.game.whoWon = 0;
                            Platform.runLater(()->{console.getItems().add("Client " + this.id + " lost their game against the server");});
                            this.game.messageID = 3;
                            try{
                                this.out.writeObject(this.game);
                                this.out.reset();
                            }
                            catch(Exception e){
                                Platform.runLater(()->{console.getItems().add("Client " + this.id + " could not receive loss");});
                            }
                            continue;
                        }
                        // Check if game is tied
                        if(tied()){
                            this.game.whoWon = 2;
                            Platform.runLater(()->{console.getItems().add("Client " + this.id + " tied their game against the server");});
                            this.game.messageID = 3;
                            try{
                                this.out.writeObject(this.game);
                                this.out.reset();
                            }
                            catch(Exception e){
                                Platform.runLater(()->{console.getItems().add("Client " + this.id + " could not receive tie");});
                            }
                            continue;
                        }
                        // Send the move

                        try{
                            this.out.writeObject(this.game);
                            this.out.reset();
                        }
                        catch(Exception e){
                            Platform.runLater(()->{console.getItems().add("Could not send move to client " + this.id);});
                        }
                    }
                }
                catch(Exception e){
                    Platform.runLater(()->{console.getItems().add("OOOOPPs...Something wrong with the socket from client: " + this.id + "....closing down!");});
                    break;
                }
            }
        }
    }

    // Class that will use the MinMax algorithm to make the next move for CPU
    public class FindNextMove extends Thread{
        int d;
        String[] b;
        boolean inUse;

        // Constructor for FindNextMove that sets values to default
        FindNextMove(){
            d = -1;
            b = new String[9];
            inUse = false;
        }

        // Run the algorithm on its own thread
        public void run(){
            // Wait for a client thread to "ask" for a move by setting the inUse variable to true
            while(true){
                System.out.print("");
                if(this.inUse){
                    // Easy difficulty: Always make a random move
                    if(d == 0){
                        // Keep trying until a valid move is made
                        while(true){
                            //System.out.println("Trying moves");
                            int move = (int)(Math.random() * 9);
                            if(this.b[move].equals("b")){
                                this.b[move] = "X";
                                break;
                            }
                        }
                    }
                    // Medium difficulty: 50% of the time random other 50% use MinMax
                    else if(d == 1){
                        int x = (int)(Math.random() * 2);
                        if(x == 1){
                            // Keep trying until a valid move is made
                            while(true){
                                int move = (int)(Math.random() * 9);
                                if(this.b[move].equals("b")){
                                    this.b[move] = "X";
                                    break;
                                }
                            }
                        }
                        else{
                            MinMax state = new MinMax(this.b);
                            ArrayList<Node> moveList = state.findMoves();
                            int bestState = 0;
                            // Choose one of the best possible moves
                            for(int i = 1; i < moveList.size(); i++){
                                Node temp = moveList.get(i);
                                if(temp.getMinMax() > moveList.get(bestState).getMinMax()){
                                    bestState = i;
                                }
                            }
                            this.b[moveList.get(bestState).getMovedTo()-1] = "X";
                        }
                    }
                    // Hard difficulty: Always use MinMax
                    else{
                        MinMax state = new MinMax(this.b);
                        ArrayList<Node> moveList = state.findMoves();
                        int bestState = 0;
                        // Choose one of the best possible moves
                        for(int i = 1; i < moveList.size(); i++){
                            Node temp = moveList.get(i);
                            if(temp.getMinMax() > moveList.get(bestState).getMinMax()){
                                bestState = i;
                            }
                        }
                        System.out.println("Placed X at" + (moveList.get(bestState).getMovedTo() - 1));
                        this.b[moveList.get(bestState).getMovedTo() - 1] = "X";
                    }
                    this.inUse = false;
                }
            }
        }
    }
}
