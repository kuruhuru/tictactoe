import kuruhuru.tictactoe.ai.Game;
import kuruhuru.tictactoe.bignum.Bignum;

import java.util.ArrayList;

public class Test {

    public static void main(String[] args) {
        Game game = new Game(15, 15, 5);
        Bignum one = Bignum.newOne((byte)4);
        one.bitwiseShift(8 * 15 + 8);
        System.out.println(one.toBinaryString());
//        game.printBoard();
//        game.printWins();
        Bignum moves = new Bignum(7,0,0,7);
        System.out.println(moves.toBinaryString());
        ArrayList<Bignum> l = Bignum.getBits(moves);
        System.out.println("Printing moves list");
        for (Bignum move: l) {
            System.out.println(move.toBinaryString());
        }
    }
}
