package kuruhuru.tictactoe.bignum;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The class implements a set of (mostly bitwise) operations on 256 bits (4 * 64, 4 * long).
 * This is necessary to implement the logic of artificial intelligence in the game of tic-tac-toe,
 * where the board cells are represented in the form of a sequence of bits.
 * @author Petr Matyukov
 * @version %I%, %G%
 */
public class Bignum {
    public static enum Compare {
        LESS, GREATER, EQUAL
    }

    protected long[] num;


    /**
     * Creates Bignum with size
     * @param size is number of Long
     */
    public Bignum(byte size) {
        if (size <= 0)
            size = 4;
        num = new long[size];
    }

    public Bignum() {
        num = new long[4];
    }

    public Bignum(long... numbers) {
        num = numbers;
    }

    public Bignum(Bignum bignum) {
        num = new long[bignum.num.length];
        System.arraycopy(bignum.num, 0, num, 0, bignum.num.length);
    }

    public static Bignum newOne() {
        Bignum res = new Bignum();
        res.num[0] = 1L;
        return res;
    }

    public static Bignum newOne(byte size) {
        Bignum res = new Bignum(size);
        res.num[0] = 1L;
        return res;
    }


    public Bignum set(Bignum bignum) {
        num = new long[bignum.num.length];
        System.arraycopy(bignum.num, 0, num, 0, bignum.num.length);
        return this;
    }

    public Bignum set(long... numbers) {
        num = numbers;
        return this;
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
     * @return Compare
     */
    public Compare compare(Bignum bignum) {
        for (int i = num.length - 1; i >= 0; i--) {
            if (num[i] > bignum.num[i]) return Compare.GREATER;
            if (num[i] < bignum.num[i]) return Compare.LESS;
        }
        return Compare.LESS;
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
        for (int i=0; i<num.length; i++)
            if (--num[i] != -1) break;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bignum bignum = (Bignum) o;
        return Arrays.equals(num, bignum.num);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(num);
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
            if (gaps >= num.length) { // becomes zero
                for (int i = 0; i < num.length; i++) {
                    num[i] = 0;
                }
            } else {
                shift %= 64;
                for (int i = num.length-1; i >= 0; i--) {
                    if (i < gaps) {
                        num[i] = 0;
                    } else {
                        num[i] = num[i - gaps];
                    }
                }
                if (shift != 0) {
                    for (int i = num.length - 1; i >= gaps; i--) {
                        if (i == gaps) {
                            num[i] <<= shift;
                        } else {
                            num[i] <<= shift;
                            num[i] |= num[i - 1] >> (64 - shift);
                        }
                    }
                }
            }
        } else if (shift < 0) {
            shift = -shift;
            int gaps = shift / 64;
            if (gaps >= num.length) {// becomes zero
                for (int i = 0; i < num.length; i++) {
                    num[i] = 0;
                }
            } else {
                shift %= 64;
                for (int i = 0; i < num.length; i++) {
                    if (i > num.length - 1 - gaps) {
                        num[i] = 0;
                    } else {
                        num[i] = num[i + gaps];
                    }
                }
                if (shift != 0) {
                    for (int i = 0; i < num.length - gaps; i++) {
                        if (i == num.length - 1 - gaps) {
                            num[i] >>>= shift;
                        } else {
                            num[i] >>>= shift;
                            num[i] |= num[i + 1] << (64 - shift);
                        }
                    }
                }
            }

        }
        return this;
    }

    /**
     *
     * @return a list of Bignum objects where only one bit is set
     */
    public static ArrayList<Bignum> getBits(Bignum big) {
        ArrayList<Bignum> res = new ArrayList<>(big.num.length);
        Bignum bits = new Bignum(big);
        while (!bits.isZero()) {
            Bignum move = new Bignum(bits);
            bits.bitwiseAND(new Bignum(bits).minusOne());
            move.bitwiseXOR(bits);
            res.add(move);
        }

        return res;
    }
}
