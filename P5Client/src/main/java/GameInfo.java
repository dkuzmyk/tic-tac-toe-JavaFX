import java.io.Serializable;
import java.util.ArrayList;

/*
 * Class that will hold information of the state of the game and will be passed between the server and clients.
 */
public class GameInfo implements Serializable{

    String board[];
    ArrayList<Integer> top3ID;
    ArrayList<Integer> top3points;
    Integer difficulty;
    Integer messageID;
    Integer whoWon;
    Integer idClient;

    // New game info object
    GameInfo(){
        board = new String[9];
        top3ID = new ArrayList<Integer>(3);
        top3points = new ArrayList<Integer>(3);
    }
}
