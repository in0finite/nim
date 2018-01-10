package gamelogic;

import util.Pair;

import java.util.ArrayList;

public class MinMaxMove implements MoveStrategy {

    protected int maxDepth = 0;
    private int numCalls = 0;


    public class NodeData {
        ArrayList<Pillar> pillars = new ArrayList<>();
    }



    public MinMaxMove(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    @Override
    public Move getNextMove(GameState nimGameState) {


        this.numCalls = 0;

        Move move = null;

        Pair<Float, GameState> pair = minimax(nimGameState, this.maxDepth, true);
        if(pair.secondValue != nimGameState) {
            // found best state
            // figure out the move
            move = GameState.getMoveBetweenTwoStates(nimGameState, pair.secondValue);
            System.out.println("heuristic value = " + pair.firstValue + ", num calls = " + this.numCalls);
        } else {
            System.out.println("Failed to find a move. Have I lost the game?");
        }

        return move;
    }


    public float heuristicValue(GameState node, int depthLeft, boolean hasChildren, boolean maximizingPlayer) {

        if(node.getNumPillars() < 1)
            return 0f;

        if(!hasChildren) {  // we have no move to play
            if(maximizingPlayer)
                return -Float.MAX_VALUE;
            else
                return Float.MAX_VALUE;
        }

        // compute xor sum of number of coins per pillar

        int xorSum = node.getNumCoinsAtPillar(0);
        for(int i=1; i < node.getNumPillars(); i++) {
            xorSum ^= node.getNumCoinsAtPillar(i);
        }

        // TODO: is this correct ?

        //return xorSum == 0 ? 1f : 0f ;

        if(maximizingPlayer)
            return xorSum;

        return -xorSum;
    }


    /// Performs minimax search. Returns pair of best node and it's heuristic value.
    public Pair<Float,GameState> minimax(GameState node, int depthLeft, boolean maximizingPlayer) {

        this.numCalls++;

        ArrayList<GameState> allPossibleNewStates = node.getAllPossibleNewStates();

        if( depthLeft == 0 || allPossibleNewStates.size() == 0) {
            // no more depth available, or this node has no children
            return new Pair<>( this.heuristicValue(node, depthLeft, allPossibleNewStates.size() > 0, maximizingPlayer), node );
        }


        float bestValue ;
        GameState bestState = node;

        if( maximizingPlayer ) {
            // looking for maximum value among children
            bestValue = Float.NEGATIVE_INFINITY;
            for( GameState child : allPossibleNewStates ) {
                Pair<Float,GameState> pair = minimax(child, depthLeft - 1, false);
                if(pair.firstValue > bestValue) {
                    bestValue = pair.firstValue;
                    bestState = child;
                }
            }
        }
        else {
            // looking for minimum value among children
            bestValue = Float.POSITIVE_INFINITY;
            for( GameState child : allPossibleNewStates ) {
                Pair<Float, GameState> pair = minimax(child, depthLeft - 1, true);
                if(pair.firstValue < bestValue) {
                    bestValue = pair.firstValue;
                    bestState = child;
                }
            }
        }

        return new Pair<>(bestValue, bestState);
    }



}
