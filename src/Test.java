import kuruhuru.tictactoe.bignum.Bignum;

public class Test {

    public static void main(String[] args) {
        Bignum b = new Bignum(0, 0, 0, -1);
        System.out.println(b.minusOne().toBinaryString());
        System.out.println(b.bitwiseShift(65).toBinaryString());
        System.out.println(b.bitwiseShift(-6).toBinaryString());
    }
}
