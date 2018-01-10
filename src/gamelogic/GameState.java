package gamelogic;

import java.util.ArrayList;

public class GameState {

    private int[] m_pillars = null;
    public int numCoinsRemovedLastTurn = 0;



    public GameState() {

        //this.m_pillars = new ArrayList<>(4);
        //this.m_pillars = new int[4];

    }

    public GameState( Iterable<Integer> coinsPerPillar ) {

        ArrayList<Integer> list = new ArrayList<>(4);
        for (Integer numCoins : coinsPerPillar) {
            list.add(numCoins);
        }

        this.m_pillars = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            this.m_pillars[i] = list.get(i);
        }

    }

    public GameState(GameState other) {

        //this.m_pillars = new ArrayList<>(other.getNumPillars());
        this.m_pillars = new int[other.getNumPillars()];
        other.copyPillarsData(this);
        this.numCoinsRemovedLastTurn = other.numCoinsRemovedLastTurn;

    }


    public int getNumPillars() {
        //return this.m_pillars.size();
        return this.m_pillars.length;
    }

    public int getNumCoinsAtPillar( int pillarIndex ) {
        //return this.m_pillars.get(pillarIndex).getNumCoins();
        return this.m_pillars[pillarIndex];
    }

    void setNumCoinsAtPillar( int pillarIndex, int numCoins ) {
        //this.m_pillars.get(pillarIndex).m_numCoins = numCoins;
        this.m_pillars[pillarIndex] = numCoins;
    }

    void removeCoinsAtPillar( int pillarIndex, int numCoins ) {
        //this.m_pillars.get(pillarIndex).m_numCoins -= numCoins;
        this.m_pillars[pillarIndex] -= numCoins;
    }


    public void copyPillarsData( GameState destination ) {
        //destination.m_pillars.clear();
        //destination.m_pillars.ensureCapacity(this.getNumPillars());
        for(int i=0; i < this.getNumPillars(); i++) {
            //destination.m_pillars.add(new Pillar(this.getNumCoinsAtPillar(i)));
            destination.setNumCoinsAtPillar(i, this.getNumCoinsAtPillar(i));
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

                GameState gameState = new GameState(currentState);
                gameState.numCoinsRemovedLastTurn = j;
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
