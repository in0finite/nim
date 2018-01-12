package etf.nim.la150391d.gamelogic;

/**
 * Represents a single move in the game.
 */
public class Move {

    int pillarIndex = -1;
    int numCoinsTaken = 0;

    public Move(int pillarIndex, int numCoinsTaken) {
        this.pillarIndex = pillarIndex;
        this.numCoinsTaken = numCoinsTaken;
    }

    public int getPillarIndex() {
        return pillarIndex;
    }

    public int getNumCoinsTaken() {
        return numCoinsTaken;
    }

    @Override
    public String toString() {
        return "Move{" +
                "pillarIndex=" + pillarIndex +
                ", numCoinsTaken=" + numCoinsTaken +
                '}';
    }

}
