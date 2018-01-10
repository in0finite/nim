package gamelogic;

import util.Pair;

import java.util.ArrayList;

public class AlfaBetaPrunningMove extends MinMaxMove {


    public AlfaBetaPrunningMove(int maxDepth) {
        super(maxDepth);
    }


    @Override
    public Move getNextMove(GameState nimGameState) {

        Move move = null;

        Pair<Float,GameState> pair = alphabeta(nimGameState, this.maxDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);
        if(pair.secondValue != nimGameState) {
            // found best state
            // figure out the move
            move = GameState.getMoveBetweenTwoStates(nimGameState, pair.secondValue);
        } else {
            System.out.println("Failed to find a move. Have I lost the game?");
        }

        return move;
    }


    public Pair<Float,GameState> alphabeta(GameState node, int depthLeft, float α, float β, boolean maximizingPlayer) {

        ArrayList<GameState> allPossibleNewStates = node.getAllPossibleNewStates();

        if( depthLeft == 0 || allPossibleNewStates.size() == 0) {
            // no more depth available, or this node has no children
            return new Pair<>(heuristicValue(node, depthLeft, allPossibleNewStates.size() > 0, maximizingPlayer), node);
        }


        float bestValue ;
        GameState bestState = node;

        if(maximizingPlayer) {
            bestValue = Float.NEGATIVE_INFINITY;
            for( GameState child : allPossibleNewStates ) {
                Pair<Float, GameState> pair = alphabeta(child, depthLeft - 1, α, β, false);
                if(pair.firstValue > bestValue) {
                    bestValue = pair.firstValue;
                    bestState = child;
                }
                α = Math.max(α, bestValue);
                if( β <= α )
                    break; // β cut-off
            }
        } else {
            bestValue = Float.POSITIVE_INFINITY;
            for( GameState child : allPossibleNewStates ) {
                Pair<Float, GameState> pair = alphabeta(child, depthLeft - 1, α, β, true);
                if(pair.firstValue < bestValue) {
                    bestValue = pair.firstValue;
                    bestState = child;
                }
                β = Math.min(β, bestValue);
                if( β <= α )
                    break; // α cut-off
            }
        }

        return new Pair<>(bestValue, bestState);
    }


}
