package gamelogic;

import util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

public class MinMaxMove implements MoveStrategy {

    protected int maxDepth = 0;
    private int numCalls = 0;
    private ArrayDeque<GameState> m_queue = new ArrayDeque<>(4096);



    public class NodeData {
        ArrayList<Pillar> pillars = new ArrayList<>();
    }



    public MinMaxMove(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    @Override
    public Move getNextMove(GameState nimGameState) {


        this.numCalls = 0;
        m_queue.clear();

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

        //ArrayList<GameState> allPossibleNewStates = node.getAllPossibleNewStates();
        int queueSizeBefore = m_queue.size();
        node.getAllPossibleNewStates(m_queue);
        int numPossibleStates = m_queue.size() - queueSizeBefore ;

        if( depthLeft == 0 || numPossibleStates == 0) {
            // first remove all added elements from queue
            dequeueMultiple(m_queue, numPossibleStates);
            // no more depth available, or this node has no children
            return new Pair<>( this.heuristicValue(node, depthLeft, numPossibleStates > 0, maximizingPlayer), node );
        }


        float bestValue ;
        GameState bestState = node;

        if( maximizingPlayer ) {
            // looking for maximum value among children
            bestValue = Float.NEGATIVE_INFINITY;
            for( int i=0; i < numPossibleStates ; i++ ) {
                GameState child = m_queue.removeLast();
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
            for( int i=0; i < numPossibleStates ; i++ ) {
                GameState child = m_queue.removeLast();
                Pair<Float, GameState> pair = minimax(child, depthLeft - 1, true);
                if(pair.firstValue < bestValue) {
                    bestValue = pair.firstValue;
                    bestState = child;
                }
            }
        }

        return new Pair<>(bestValue, bestState);
    }


    public static void dequeueMultiple( ArrayDeque<GameState> queue, int numTimesToDequeue ) {
        for (int i = 0; i < numTimesToDequeue; i++) {
            queue.removeLast();
        }
    }


}
