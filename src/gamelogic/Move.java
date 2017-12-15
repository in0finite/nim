package gamelogic;

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


}
