package gamelogic;

import java.util.ArrayList;

public class GameState {

    public ArrayList<Pillar> pillars = new ArrayList<>();
    public int numCoinsRemovedLastTurn = 0;



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

        Pillar pillar = this.pillars.get(pillarIndexToRemoveFrom);

        int newNumCoins = pillar.getNumCoins() - numCoinsToRemove;

        if(newNumCoins < 0)
            return false;


        // check if any other pillar has this number of coins (except if number of coins is 0)
        if(newNumCoins != 0) {
            for (int i = 0; i < this.pillars.size(); i++) {

                if (i == pillarIndexToRemoveFrom)
                    continue;

                if (this.pillars.get(i).getNumCoins() == newNumCoins) {
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


        for (int i = 0; i < currentState.pillars.size(); i++) {

            // remove coins from this pillar and create new states
            // j => num coins removed
            for (int j = 1; j <= maxNumCoinsToRemove && j <= currentState.pillars.get(i).getNumCoins(); j++) {

                // check if this move is possible
                if(!this.isMovePossible( i, j ))
                    continue;

                GameState gameState = new GameState();
                gameState.numCoinsRemovedLastTurn = j;
                // copy pillars
                for(Pillar pillar : currentState.pillars)
                    gameState.pillars.add(new Pillar(pillar));
                // remove coins from this pillar
                gameState.pillars.get(i).removeCoins(j);

                newStates.add(gameState);
            }
        }


        return newStates;
    }


    public static Move getMoveBetweenTwoStates(GameState firstState, GameState secondState) {

        for (int j = 0; j < secondState.pillars.size(); j++) {
            if(secondState.pillars.get(j).getNumCoins() != firstState.pillars.get(j).getNumCoins()) {
                // coins have been removed from this pillar
                return new Move( j, firstState.pillars.get(j).getNumCoins() - secondState.pillars.get(j).getNumCoins() );
            }
        }

        System.out.println("Can't find a move between two states");

        return null;
    }

}
