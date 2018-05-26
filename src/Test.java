import kuruhuru.tictactoe.bignum.Bignum;

public class Test {

    public static void main(String[] args) {
        Bignum b = new Bignum(0, 0, 0, -1);
        System.out.println(b.minusOne().toBinaryString());
    }
}
