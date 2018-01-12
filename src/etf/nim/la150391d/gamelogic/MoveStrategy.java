package etf.nim.la150391d.gamelogic;

/**
 * Interface which should be inherited by AIs, and is used to compute the next move.
 */
public interface MoveStrategy {

    Move getNextMove(GameState nimGameState);

}
