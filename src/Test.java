import kuruhuru.tictactoe.ai.Game;
import kuruhuru.tictactoe.bignum.Bignum;

public class Test {

    public static void main(String[] args) {
        Game game = new Game(15, 15, 5);
        Bignum one = Bignum.newOne((byte)4);
        one.bitwiseShift(8 * 15 + 8);
        System.out.println(one.toBinaryString());
//        game.printBoard();
        game.printWins();
    }
}
