import kuruhuru.tictactoe.ai.Game;
import kuruhuru.tictactoe.bignum.Bignum;

public class Test {

    public static void main(String[] args) {
        Game game = new Game(5, 5, 3);
        game.printWins();
    }
}
