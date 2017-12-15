package gamelogic;

public interface MoveStrategy {

    Move getNextMove(Nim.GameState nimGameState);

}
