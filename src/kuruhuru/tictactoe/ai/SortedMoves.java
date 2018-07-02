package kuruhuru.tictactoe.ai;

import kuruhuru.tictactoe.bignum.Bignum;

/**
 * Moves sorted according strength
 */
public class SortedMoves {
    Bignum wining;          // wining move
    Bignum fork;            // wining fork
    Bignum potentialForks;  // potential forks
    Bignum checks;          // Checks - threat of winning
    Bignum goodMoves;      // The moves, which in the future can build a winning series

    SortedMoves() {
        wining = null;
        fork = null;
        potentialForks = null;
        checks = null;
        goodMoves = null;
    }
}
