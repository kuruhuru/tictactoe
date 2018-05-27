package kuruhuru.tictactoe.ai;

import kuruhuru.tictactoe.bignum.Bignum;

/**
 * The class implements a game of tic-tac-toe,
 * where the board cells are represented in the form of a sequence of bits.
 * The game board can be of different sizes. In particular, it can be rectangular.
 *
 * @author Petr Matyukov
 * @version %I%, %G%
 */

public class Game {
    private int width; // width of game board
    private int height; // height of game board
    private int line;  // length of wining sequence

    private Bignum X; // Crosses on the board. Bit unit means a cross
    private Bignum O; // Zeros on the board. Bit single means zero
    private Bignum filled; // The filled board. A bit unit means a cross or a zero
    private Bignum lastMove; // The last move

    private Bignum[] wins; // All possible winnings. Bit units form a winning sequence.

    /**
     * The game dimension is input: width, height
     * And also the number of zeros or crosses in a row, necessary for victory
     * For example, (4, 4, 3) - the game on the board 4x4 and you need to build
     * a winning line from 3 crosses or zeros
     * @param width width of game board
     * @param height height of game board
     * @param line length of wining sequence
     */
    public Game(int width, int height, int line) {

        // width should be   0 < width <= 256
        width %= 256;
        if (width < 0) width = - width;
        else if (width == 0) width = 3;

        // height should be   0 < height <= 256
        height %= 256;
        if (height < 0) height = - height;
        else if (height == 0) height = 3;

        // creating empty board
        this.width = width;
        this.height = height;
        this.line = line;

        this.X = new Bignum();
        this.O = new Bignum();
        this.filled = new Bignum();

        // For convenience, the vertical should not be more than the horizontal
        if (height > width) {
            this.height = width;
            this.width = height;
        }

        if (this.line <= 0) {
            this.line = this.height;
        } else if (this.line > this.height) {
            this.line = this.height;
        }

        // Determine how many winning combinations can be
        int possibleWins = this.height * (this.width - this.line + 1) +
                this.width * (this.height - this.line + 1) +
                2 * (this.height - this.line + 1) * (this.width - this.line + 1);
        this.wins = new Bignum[possibleWins];

        // Indicator of the fullness of the board
        Bignum one = Bignum.newOne();
        for (int i=0; i < this.height * this.width; i++) {
            this.filled.bitwiseOR(one);
            one.bitwiseShift(1);
        }

        // Determine which series can win the game
        int winIndex = 0;

        // Horizontals
        Bignum horizontal = new Bignum();
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne();
            one.bitwiseShift(i);
            horizontal.bitwiseOR(one);
        }
        for (int shift=0; shift < (this.width - this.line + 1); shift++) {
            for (int i=0; i < this.height; i++) {
                Bignum win = new Bignum(horizontal);
                win.bitwiseShift(i * this.width + shift);
                this.wins[winIndex] = win;
                winIndex++;
            }
        }

        // Verticals
        Bignum vertical = new Bignum();
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne();
            one.bitwiseShift(i * this.width);
            vertical.bitwiseOR(one);
        }
        for (int shift=0; shift < (this.height - this.line + 1); shift++) {
            for (int i=0; i < this.width; i++) {
                Bignum win = new Bignum(vertical);
                win.bitwiseShift(i + this.width * shift);
                this.wins[winIndex] = win;
                winIndex++;
            }
        }

        // Diagonals
        Bignum diag1 = new Bignum();
        Bignum diag2 = new Bignum();
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne();
            one.bitwiseShift(i * this.width + i);
            diag1.bitwiseOR(one);
            one = Bignum.newOne();
            one.bitwiseShift(this.width - 1);
            one.bitwiseShift(i * this.width);
            one.bitwiseShift(-i);
            diag2.bitwiseOR(one);
            //diag2 |= (uint64_t )1 << game.width - 1 << i * game.width >> i;
        }

        for (int j=0; j < (this.width - this.line + 1); j++) {
            for (int i=0; i < (this.height - this.line + 1); i++) {
                Bignum win = new Bignum(diag1);
                win.bitwiseShift(j + i*this.width);
                this.wins[winIndex] = win;
                winIndex++;
                win = new Bignum(diag2);
                win.bitwiseShift(i * this.width);
                win.bitwiseShift(-j);
                this.wins[winIndex] = win;
                winIndex++;
            }
        }
    }


    /**
     * Returns the cell corresponding to the (i, j) position on the board,
     * where -1 is the cross, 1 means zero, 0 is empty
     */
    public int getField(int i, int j) {
        Bignum one = Bignum.newOne();
        one.bitwiseShift(i * this.width + j);
        Bignum field = new Bignum(one);
        field.bitwiseAND(this.X);
        if (!field.isZero()) {
            return -1;
        }
        one.bitwiseAND(this.O);
        if (!one.isZero()) {
            return 1;
        }
        return 0;
    }

    /**
     * Prints board position
     */
    public void printBoard() {
        for (int i=0; i < this.height; i++) {
            for (int j=0; j < this.width; j++) {
                char field = '_';
                switch (this.getField(i, j)) {
                    case -1:
                        field = 'X';
                        break;
                    case 1:
                        field = 'O';
                        break;
                }
                System.out.print("|" + field);
            }
            System.out.println("|");
        }
    }

    /**
     * Prints board position with '*' for some fields
     */
    public void printBoard(Bignum fields) {
        for (int i=0; i<this.height; i++) {
            for (int j=0; j < this.width; j++) {
                Bignum one = Bignum.newOne();
                one.bitwiseShift(i * this.width + j);
                one.bitwiseAND(fields);
                if (!one.isZero()) {
                    System.out.print("|*");
                } else {
                    char field = '_';
                    switch (this.getField(i, j)) {
                        case -1:
                            field = 'X';
                            break;
                        case 1:
                            field = 'O';
                            break;
                    }
                    System.out.print("|" + field);
                }
            }
            System.out.println("|");
        }
    }

    /**
     * Prints all wining lines
     */
    public void printWins() {
        for (int i=0; i<wins.length; i++) {
            printBoard(wins[i]);
            System.out.println();
        }
    }
}
