package gamelogic;

import java.util.ArrayList;

public class GameState {

    public ArrayList<Pillar> pillars = new ArrayList<>();
    public int numCoinsRemovedLastTurn = 0;



//    public GameState(GameState other) {
//
//    }


    public int getNumPillars() {
        return this.pillars.size();
    }

    public int getNumCoinsAtPillar( int pillarIndex ) {
        return this.pillars.get(pillarIndex).getNumCoins();
    }

    void setNumCoinsAtPillar( int pillarIndex, int numCoins ) {
        this.pillars.get(pillarIndex).m_numCoins = numCoins;
    }

    void removeCoinsAtPillar( int pillarIndex, int numCoins ) {
        this.pillars.get(pillarIndex).m_numCoins -= numCoins;
    }


    public void copyPillarsData( GameState destination ) {
        destination.pillars.clear();
        destination.pillars.ensureCapacity(this.getNumPillars());
        for(int i=0; i < this.getNumPillars(); i++) {
            destination.pillars.add(new Pillar(this.getNumCoinsAtPillar(i)));
        //    destination.setNumCoinsAtPillar(i, this.getNumCoinsAtPillar(i));
        }
    }


    public  int getNumPillarsWithCoins() {

        int count = 0;
        for(int i=0; i < this.getNumPillars(); i++) {
            if(this.getNumCoinsAtPillar(i) > 0)
                count++;
        }

        return count;
    }


    public  boolean isMovePossible(Move move) {

        return this.isMovePossible(move.pillarIndex, move.numCoinsTaken);

    }

    public  boolean isMovePossible(int pillarIndexToRemoveFrom, int numCoinsToRemove) {


        if( numCoinsToRemove < 1 )
            return false;

        if(this.numCoinsRemovedLastTurn != 0 && numCoinsToRemove > this.numCoinsRemovedLastTurn * 2) {
            // it's one of the rules
            return false;
        }

        int newNumCoins = this.getNumCoinsAtPillar(pillarIndexToRemoveFrom) - numCoinsToRemove;

        if(newNumCoins < 0)
            return false;


        // check if any other pillar has this number of coins (except if number of coins is 0)
        if(newNumCoins != 0) {
            for (int i = 0; i < this.getNumPillars(); i++) {

                if (i == pillarIndexToRemoveFrom)
                    continue;

                if (this.getNumCoinsAtPillar(i) == newNumCoins) {
                    // this pillar will have the same number of coins
                    return false;
                }
            }
        }


        return true;
    }

    public ArrayList<GameState> getAllPossibleNewStates() {

        GameState currentState = this;

        ArrayList<GameState> newStates = new ArrayList<>();

        int maxNumCoinsToRemove = currentState.numCoinsRemovedLastTurn * 2;
        if(0 == maxNumCoinsToRemove)
            maxNumCoinsToRemove = Integer.MAX_VALUE;


        for (int i = 0; i < currentState.getNumPillars(); i++) {

            // remove coins from this pillar and create new states
            // j => num coins removed
            for (int j = 1; j <= maxNumCoinsToRemove && j <= currentState.getNumCoinsAtPillar(i) ; j++) {

                // check if this move is possible
                if(!currentState.isMovePossible( i, j ))
                    continue;

                GameState gameState = new GameState();
                gameState.numCoinsRemovedLastTurn = j;
                // copy pillars
                currentState.copyPillarsData(gameState);
                // remove coins from this pillar
                gameState.removeCoinsAtPillar(i, j);

                newStates.add(gameState);
            }
        }


        return newStates;
    }


    public static Move getMoveBetweenTwoStates(GameState firstState, GameState secondState) {

        for (int j = 0; j < secondState.getNumPillars(); j++) {
            if(secondState.getNumCoinsAtPillar(j) != firstState.getNumCoinsAtPillar(j)) {
                // coins have been removed from this pillar
                return new Move( j, firstState.getNumCoinsAtPillar(j) - secondState.getNumCoinsAtPillar(j) );
            }
        }

        System.out.println("Can't find a move between two states");

        return null;
    }

}
