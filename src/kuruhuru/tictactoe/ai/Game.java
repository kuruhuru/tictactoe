package kuruhuru.tictactoe.ai;

import kuruhuru.tictactoe.bignum.Bignum;

import java.util.ArrayList;

/**
 * The class implements a game of tic-tac-toe,
 * where the board cells are represented in the form of a sequence of bits.
 * The game board can be of different sizes. In particular, it can be rectangular.
 *
 * @author Petr Matyukov
 * @version %I%, %G%
 */

public class Game {

    public enum Field {
        X, O, EMPTY
    }

    public enum Result {
        X, O, DRAW, UNFINISHED
    }

    public static class GameResult {
        Result result = Result.UNFINISHED;
        Bignum win = null;
    }

    public enum Player {
        X, O;
        public Player next() {
            return (this == X)? O : X;
        }
    }

    private int width;  // width of game board
    private int height; // height of game board
    private int line;   // length of wining sequence
    private final byte bigSize;   // capacity of Bignum representing a board

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

        // width should be   0 < width <= 15
        width %= 16;
        if (width < 0) width = - width;
        else if (width == 0) width = 3;

        // height should be   0 < height <= 15
        height %= 16;
        if (height < 0) height = - height;
        else if (height == 0) height = 3;

        // creating empty board
        this.width = width;
        this.height = height;
        this.line = line;

        this.bigSize = (byte)((width*height)/64 + 1);
        this.X = new Bignum(bigSize);
        this.O = new Bignum(bigSize);
        this.filled = new Bignum(bigSize);

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
        Bignum one = Bignum.newOne(bigSize);
        for (int i=0; i < this.height * this.width; i++) {
            this.filled.bitwiseOR(one);
            one.bitwiseShift(1);
        }

        // Determine which series can win the game
        int winIndex = 0;

        // Horizontals
        Bignum horizontal = new Bignum(bigSize);
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne(bigSize);
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
        Bignum vertical = new Bignum(bigSize);
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne(bigSize);
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
        Bignum diag1 = new Bignum(bigSize);
        Bignum diag2 = new Bignum(bigSize);
        for (int i=0; i < this.line; i++) {
            one = Bignum.newOne(bigSize);
            one.bitwiseShift(i * this.width + i);
            diag1.bitwiseOR(one);
            one = Bignum.newOne(bigSize);
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
    public Field getField(int i, int j) {
        Bignum one = Bignum.newOne(bigSize);
        one.bitwiseShift(i * this.width + j);
        Bignum field = new Bignum(one);
        field.bitwiseAND(this.X);
        if (!field.isZero()) {
            return Field.X;
        }
        one.bitwiseAND(this.O);
        if (!one.isZero()) {
            return Field.O;
        }
        return Field.EMPTY;
    }

    /**
     * Prints board position
     */
    @SuppressWarnings("Duplicates")
    public void printBoard() {
        for (int i=0; i < this.height; i++) {
            for (int j=0; j < this.width; j++) {
                char field = '_';
                switch (this.getField(i, j)) {
                    case X:
                        field = 'X';
                        break;
                    case O:
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
    @SuppressWarnings("Duplicates")
    public void printBoard(Bignum fields) {
        System.out.println("fields = " + fields.toBinaryString());
        for (int i=0; i<this.height; i++) {
            for (int j=0; j < this.width; j++) {
                Bignum one = Bignum.newOne(bigSize);
                one.bitwiseShift(i * this.width + j);
                one.bitwiseAND(fields);
                if (!one.isZero()) {
                    System.out.print("|*");
                } else {
                    char field = '_';
                    switch (this.getField(i, j)) {
                        case X:
                            field = 'X';
                            break;
                        case O:
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

    /**
     * Returns game result
     */
    public GameResult result() {
        GameResult res = new GameResult();
        for( Bignum w : wins){
            Bignum win = new Bignum(w);
            win.bitwiseAND(this.X);
            if (win.equals(w)) {
                res.result = Result.X;
                res.win = win;
                return res;
            }
            win = new Bignum(w);
            win.bitwiseAND(this.O);
            if (win.equals(w)) {
                res.result = Result.O;
                res.win = win;
                return res;
            }
        }
        // If board is full then draw
        Bignum filled = new Bignum(this.X);
        filled.bitwiseOR(this.O);
        if (filled.equals(this.filled)) {
            res.result = Result.DRAW;
        }

        return res;
    }

    /**
     * Make move on the board if it possible.
     * If not - returns false
     */
    public boolean makeMove(Bignum move, Player player) {
        Bignum field = new Bignum(this.X);
        field.bitwiseOR(this.O);
        field.bitwiseAND(move);
        if (!field.isZero())//illegal move, field is not empty
            return false;

        if (player == Player.X) {
            this.X.bitwiseOR(move);
        } else {
            this.O.bitwiseOR(move);
        }
        return true;
    }

    /**
     * Undo move on the board if it possible.
     * If not - returns false
     */
    public void undoMove(Bignum move, Player player) {
        if (player == Player.X)
            this.X.bitwiseXOR(move);
        else
            this.O.bitwiseXOR(move);
    }

    /**
     *
     * @return all possible moves as a list
     */
    public ArrayList<Bignum> findPossibleMoves() {
        Bignum empty = new Bignum(this.X).bitwiseOR(this.O).bitwiseXOR(this.filled);
        return Bignum.getBits(empty);
    }

    /**
     * Sorts possible moves of the player, according to their strength
     * @return sorted moves
     */
    public SortedMoves sortPossibleMoves(Player player) {
        SortedMoves res = new SortedMoves();

        Bignum player_boardl;
        Bignum playerBoard;
        Bignum opponentBoard;


        if (player == Player.X) {
            playerBoard = new Bignum(this.X);
            opponentBoard = new Bignum(this.O);
        } else {
            playerBoard = new Bignum(this.O);
            opponentBoard = new Bignum(this.X);
        }

        for (Bignum w: this.wins) {
            Bignum win = new Bignum(w);
            Bignum potentialMoves = win.bitwiseAND(playerBoard);
            if(!potentialMoves.isZero()) {
                potentialMoves.bitwiseXOR(win); // The moves required to fill a wining row
                if (new Bignum(potentialMoves).bitwiseAND(opponentBoard).isZero()) {  // All fields are free
                    int count = 0;
                    Bignum pmove = new Bignum(potentialMoves);
                    while (!potentialMoves.isZero()) {
                        potentialMoves.bitwiseAND(new Bignum(potentialMoves).minusOne());
                        count += 1;
                    }
                    if (count == 1) {  // wining move found
                        res.wining = pmove;
                        return res;
                    } else if (count == 2) { // Check
                        Bignum potentialFork = new Bignum(pmove).bitwiseAND(res.checks);
                        if (!potentialFork.isZero()) {
                            int number = 0;
                            Bignum pfork = new Bignum(potentialFork);
                            while (!pfork.isZero()) {
                                pfork.bitwiseAND(new Bignum(pfork).minusOne());
                                number += 1;
                            }
                            if (number == 1) {  // Exactly fork
                                res.fork = potentialFork;
                            }
                            res.potentialForks.bitwiseOR(potentialFork);
                        } else { // Check
                            res.checks.bitwiseOR(pmove);
                        }
                    } else {
                        res.goodMoves.bitwiseOR(pmove);
                    }
                }
            }
        }
        return res;
    }
}
