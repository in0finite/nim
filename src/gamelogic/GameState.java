package gamelogic;

import java.util.ArrayList;

public class GameState {

    public ArrayList<Pillar> pillars = new ArrayList<>();
    public int numCoinsRemovedLastTurn = 0;



    public ArrayList<GameState> getAllPossibleNewStates() {

        GameState currentState = this;

        ArrayList<GameState> newStates = new ArrayList<>();

        int maxNumCoinsToRemove = currentState.numCoinsRemovedLastTurn * 2;
        if(0 == maxNumCoinsToRemove)
            maxNumCoinsToRemove = Integer.MAX_VALUE;

        for (int i = 0; i < currentState.pillars.size(); i++) {

            // remove coins from this pillar and create new states
            // j => num coins removed
            for (int j = 1; j < maxNumCoinsToRemove && j < currentState.pillars.get(i).getNumCoins(); j++) {

                GameState gameState = new GameState();
                gameState.numCoinsRemovedLastTurn = j;
                // copy pillars
                gameState.pillars.addAll(currentState.pillars);
                // remove coins from this pillar
                gameState.pillars.get(i).removeCoins(j);    // TODO: check if it is possible

                newStates.add(gameState);
            }
        }

        if(0 == newStates.size()) {
            // we did not add any new state
            // there is no possible move

            //bestStateIndex = -1 ;
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
