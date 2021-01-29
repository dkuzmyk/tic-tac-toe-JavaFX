import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import java.util.ArrayList;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

// Class that will test the MinMax algorithm 
class TicTacToeTest {

        MinMax algorithm;
        static String[] b;

        // Creates a blank board
        @BeforeAll
        static void initialize(){
          String[] a = {"b", "b", "b", "b", "b", "b", "b", "b", "b"};
          b = a;
        }

        // Tests the MinMax class
	@Test
	void minMaxTest() {
	  algorithm = new MinMax(b);
          assertEquals("MinMax", algorithm.getClass().getName(), "wrong class");
	}

        // Tests the Node class
        @Test
        void nodeTest(){
          Node temp = new Node(b, 1);
          assertEquals("Node", temp.getClass().getName(), "wrong class");
        }

        // Tests finding moves for an empty board
        @Test
        void emptyBoardTest(){
          algorithm = new MinMax(b);
          assertEquals(9, algorithm.findMoves().size(), "all 9 moves not given"); 
        }

        // Test finding moves for a full board
        @Test
        void fullBoardTest(){
          String[] temp = {"O", "O", "O", "O", "O", "O", "O", "O", "O"};
          algorithm = new MinMax(temp);
          assertEquals(0, algorithm.findMoves().size(), "no moves should be found");
        }


        // Test if correct number of possible moves are available
        @Test
        void numMovesTest(){
          String[] temp = {"O", "b", "O", "b", "b", "O", "b", "O", "b"};
          algorithm = new MinMax(temp);
          assertEquals(5, algorithm.findMoves().size(), "5 moves should be found");
        }


        // Test minMax values of possible moves
        @Test
        void minMaxValTest(){
          algorithm = new MinMax(b);
          ArrayList<Node> mList = algorithm.findMoves();
          for(int i = 0; i < mList.size(); i++){
            Node temp = mList.get(i);
            assertEquals(0, temp.getMinMax(), "all moves should be normal moves");
          }
        }

        // Test minMax values of possible moves
        @Test
        void minMaxValTest2(){
          String[] board = {"O", "b", "O", "b", "b", "b", "O", "b", "b"};
          algorithm = new MinMax(board);
          ArrayList<Node> mList = algorithm.findMoves();
          for(int i = 0; i < mList.size(); i++){
            Node temp = mList.get(i);
            assertEquals(-10, temp.getMinMax(), "all moves should be losing moves");
          }
        }

        // Test minMax values of possible moves
        @Test
        void minMaxValTest3(){
          String[] board = {"X", "b", "X", "b", "b", "b", "X", "b", "X"};
          algorithm = new MinMax(board);
          ArrayList<Node> mList = algorithm.findMoves();
          for(int i = 0; i < mList.size(); i++){
            Node temp = mList.get(i);
            assertEquals(10, temp.getMinMax(), "all moves should be winning moves");
          }
        }

} 
