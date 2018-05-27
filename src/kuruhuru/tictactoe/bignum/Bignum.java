package kuruhuru.tictactoe.bignum;

import java.util.Arrays;

/**
 * The class implements a set of (mostly bitwise) operations on 256 bits (4 * 64, 4 * long).
 * This is necessary to implement the logic of artificial intelligence in the game of tic-tac-toe,
 * where the board cells are represented in the form of a sequence of bits.
 * @author Petr Matyukov
 * @version %I%, %G%
 */
public class Bignum {
    private long[] num;


    public Bignum() {
        num = new long[4];
    }

    public Bignum(long n1, long n2, long n3, long n4) {
        num = new long[]{n1, n2, n3, n4};
    }

    public Bignum(Bignum bignum) {
        num = new long[4];
        System.arraycopy(bignum.num, 0, num, 0, 4);
    }

    public static Bignum newOne() {
        Bignum res = new Bignum();
        res.num[0] = 1L;
        return res;
    }

    public Bignum set(Bignum bignum) {
        num = new long[4];
        System.arraycopy(bignum.num, 0, num, 0, 4);
        return this;
    }

    public Bignum set(long n1, long n2, long n3, long n4) {
        num = new long[]{n1, n2, n3, n4};
        return this;
    }

    public Bignum copy() {
        return new Bignum(this);
    }


    @Override
    public String toString() {
        return "Bignum{" +
                "num=" + Arrays.toString(num) +
                '}';
    }

    /**
     * @return Bignum as a binary string including '0' prefixes
     */
    public String toBinaryString() {
        StringBuilder res = new StringBuilder(5 * 64);
        res.append("Bignum{\n");
        for (int i = 0; i < num.length; i++) {
            StringBuilder sb = new StringBuilder(64);
            String binstr = Long.toBinaryString(num[i]);
            int prefix = 64 - binstr.length();
            for (int j = 0; j < prefix; j++) sb.append('0');
            sb.append(binstr);

            res.append("\t");
            res.append(sb);
            res.append("\n");
        }
        res.append("}");
        return res.toString();
    }

    /**
     * @return true if Bignum is zero
     */
    public boolean isZero() {
        for (int i = 0; i < num.length; i++)
            if (num[i] != 0) return false;
        return true;
    }

    /**
     * @param bignum another Bignum to compare
     * @return -1 if this is less than bignum, 0 if both are equal, 1 if this is greater than bignum
     */
    public int compare(Bignum bignum) {
        for (int i = num.length - 1; i >= 0; i--) {
            if (num[i] > bignum.num[i]) return 1;
            if (num[i] < bignum.num[i]) return -1;
        }
        return 0;
    }

    /**
     * Bitwise AND operation
     *
     * @param bignum
     */
    public Bignum bitwiseAND(Bignum bignum) {
        for (int i = 0; i < num.length; i++)
            num[i] &= bignum.num[i];
        return this;
    }

    /**
     * Bitwise OR operation
     *
     * @param bignum
     */
    public Bignum bitwiseOR(Bignum bignum) {
        for (int i = 0; i < num.length; i++)
            num[i] |= bignum.num[i];
        return this;
    }

    /**
     * Bitwise XOR operation
     *
     * @param bignum
     */
    public Bignum bitwiseXOR(Bignum bignum) {
        for (int i = 0; i < num.length; i++)
            num[i] ^= bignum.num[i];
        return this;
    }

    /**
     * Bitwise INVERT operation
     */
    public Bignum bitwiseINVERT() {
        for (int i = 0; i < num.length; i++)
            num[i] = ~num[i];
        return this;
    }

    /**
     * bignum - 1
     */
    public Bignum minusOne() {
        if (--num[0] == -1)
            if (--num[1] == -1)
                if (--num[2] == -1)
                    num[3]--;
        return this;
    }

    /**
     * Bitwise shift
     *
     * @param shift positive means shift to left, negative means shift to right
     * @return shifted bignum
     */
    public Bignum bitwiseShift(int shift) {
        if (shift > 0) {
            int gaps = shift / 64;
            if (gaps >= 4) { // becomes zero
                for (int i = 0; i < 4; i++) {
                    num[i] = 0;
                }
            } else {
                shift %= 64;
                for (int i = 3; i >= 0; i--) {
                    if (i < gaps) {
                        num[i] = 0;
                    } else {
                        num[i] = num[i - gaps];
                    }
                }
                for (int i = 3; i >= gaps; i--) {
                    if (i == gaps) {
                        num[i] <<= shift;
                    } else {
                        num[i] <<= shift;
                        num[i] |= num[i - 1] >> (64 - shift);
                    }
                }
            }
        } else if (shift < 0) {
            shift = -shift;
            int gaps = shift / 64;
            if (gaps >= 4) {// becomes zero
                for (int i = 0; i < 4; i++) {
                    num[i] = 0;
                }
            } else {
                shift %= 64;
                for (int i = 0; i < 4; i++) {
                    if (i > 3 - gaps) {
                        num[i] = 0;
                    } else {
                        num[i] = num[i + gaps];
                    }
                }
                for (int i = 0; i < 4 - gaps; i++) {
                    if (i == 3 - gaps) {
                        num[i] >>>= shift;
                    } else {
                        num[i] >>>= shift;
                        num[i] |= num[i + 1] << (64 - shift);
                    }
                }
            }
        }
        return this;
    }
}
