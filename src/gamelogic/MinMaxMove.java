package gamelogic;

import util.Pair;

import java.util.ArrayList;

public class MinMaxMove implements MoveStrategy {

    protected int maxDepth = 0;


    public class NodeData {
        ArrayList<Pillar> pillars = new ArrayList<>();
    }



    public MinMaxMove(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    @Override
    public Move getNextMove(GameState nimGameState) {


        Move move = null;

        Pair<Float, GameState> pair = minimax(nimGameState, this.maxDepth, true);
        if(pair.secondValue != nimGameState) {
            // found best state
            // figure out the move
            move = GameState.getMoveBetweenTwoStates(nimGameState, pair.secondValue);
        } else {
            System.out.println("Failed to find a move. Have I lost the game?");
        }

        return move;
    }


    public float heuristicValue(GameState node) {

        // compute xor sum of number of coins per pillar
        int xorSum = 0;
        for(Pillar pillar : node.pillars) {
            xorSum ^= pillar.getNumCoins();
        }

        // TODO: is this correct ?
        //return xorSum == 0 ? 1f : 0f ;
        return xorSum;
    }


    /// Performs minimax search. Returns pair of best node and it's heuristic value.
    public Pair<Float,GameState> minimax(GameState node, int depthLeft, boolean maximizingPlayer) {

        ArrayList<GameState> allPossibleNewStates = node.getAllPossibleNewStates();

        if( depthLeft == 0 || allPossibleNewStates.size() == 0) {
            // no more depth available, or this node has no children
            return new Pair<>( this.heuristicValue(node), node );
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
