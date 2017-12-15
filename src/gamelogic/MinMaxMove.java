package gamelogic;

import java.util.ArrayList;

public class MinMaxMove implements MoveStrategy {

    private int maxDepth = 0;


    public class NodeData {
        ArrayList<Pillar> pillars = new ArrayList<>();
    }



    public MinMaxMove(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    @Override
    public Move getNextMove(Nim.GameState nimGameState) {


        // perform minimax

        float maxValue = Float.NEGATIVE_INFINITY;
        int indexOfMaxValue = -1;
        int i=0;
        ArrayList<Nim.GameState> allPossibleNewStates = getAllPossibleNewStates(nimGameState);
        for(Nim.GameState state : allPossibleNewStates) {
            float value = minimax(state, this.maxDepth, true);
            if(value > maxValue) {
                maxValue = value;
                indexOfMaxValue = i;
            }
            i++;
        }

        Move move = null;

        if(indexOfMaxValue != -1) {
            // found best move
            // figure out the move from new state

            Nim.GameState gameState = allPossibleNewStates.get(indexOfMaxValue);
            for (int j = 0; j < gameState.pillars.size(); j++) {
                if(gameState.pillars.get(j).getNumCoins() != nimGameState.pillars.get(j).getNumCoins()) {
                    // coins have been removed from this pillar
                    move = new Move( j, nimGameState.pillars.get(j).getNumCoins() - gameState.pillars.get(j).getNumCoins() );

                    break;
                }
            }

        } else {
            // failed to find a move
            // have we lost the game ?

        }

        if(null == move)
            System.out.println("Can't find a move");

        return move;
    }


    public float heuristicValue(Nim.GameState node) {

        // compute xor sum of number of coins per pillar
        int xorSum = 0;
        for(Pillar pillar : node.pillars) {
            xorSum ^= pillar.getNumCoins();
        }

        // TODO: is this correct ?
        return xorSum == 0 ? 1f : 0f ;
    }


    // TODO: function should also return the best node
    float minimax(Nim.GameState node, int depthLeft, boolean maximizingPlayer) {

        ArrayList<Nim.GameState> allPossibleNewStates = getAllPossibleNewStates(node);

        if( depthLeft == 0 || allPossibleNewStates.size() == 0) {
            // no more depth available, or this node has no children
            return this.heuristicValue(node);
        }

        if( maximizingPlayer ) {
            // looking for maximum value among children
            float bestValue = Float.NEGATIVE_INFINITY;
            for( Nim.GameState child : allPossibleNewStates ) {
                float v = minimax(child, depthLeft - 1, false);
                bestValue = Math.max(bestValue, v);
            }

            return bestValue;
        }
        else {
            // looking for minimum value among children
            float bestValue = Float.POSITIVE_INFINITY;
            for( Nim.GameState child : allPossibleNewStates ) {
                float v = minimax(child, depthLeft - 1, true);
                bestValue = Math.min(bestValue, v);
            }
            return bestValue;
        }

    }

    public ArrayList<Nim.GameState> getAllPossibleNewStates(Nim.GameState currentState) {

        ArrayList<Nim.GameState> newStates = new ArrayList<>();

        int maxNumCoinsToRemove = currentState.numCoinsRemovedLastTurn * 2;
        if(0 == maxNumCoinsToRemove)
            maxNumCoinsToRemove = Integer.MAX_VALUE;

        for (int i = 0; i < currentState.pillars.size(); i++) {

            // remove coins from this pillar and create new states
            // j => num coins removed
            for (int j = 1; j < maxNumCoinsToRemove && j < currentState.pillars.get(i).getNumCoins(); j++) {

                Nim.GameState gameState = new Nim.GameState();
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

}
