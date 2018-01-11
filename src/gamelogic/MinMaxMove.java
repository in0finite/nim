package gamelogic;

import util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

public class MinMaxMove implements MoveStrategy {

    protected int maxDepth = 0;
    protected int numCalls = 0;
    protected ArrayDeque<GameState> m_queue = new ArrayDeque<>(4096);
    protected float m_resultHeuristicValue = 0f;
    protected GameState m_resultNode = null;



    public class NodeData {
        ArrayList<Pillar> pillars = new ArrayList<>();
    }



    public MinMaxMove(int maxDepth) {
        this.maxDepth = maxDepth;
    }


    @Override
    public Move getNextMove(GameState nimGameState) {

        long timeAtStart = System.currentTimeMillis();

        this.numCalls = 0;
        m_queue.clear();
        m_resultHeuristicValue = 0f;
        m_resultNode = null;


        this.getNextMoveInternal(nimGameState);

        long timeElapsedMs = System.currentTimeMillis() - timeAtStart;

        Move move = null;
        if(m_resultNode != nimGameState) {
            // found best state
            // figure out the move
            move = GameState.getMoveBetweenTwoStates(nimGameState, m_resultNode);
            System.out.println("heuristic value = " + m_resultHeuristicValue + ", num calls = " + this.numCalls + ", time = " + timeElapsedMs + " ms");
        } else {
            System.out.println("Failed to find a move. Have I lost the game?");
        }

        return move;
    }

    protected void getNextMoveInternal(GameState nimGameState) {

        minimax(nimGameState, this.maxDepth, true);

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
    void minimax(GameState node, int depthLeft, boolean maximizingPlayer) {

        this.numCalls++;

        //ArrayList<GameState> allPossibleNewStates = node.getAllPossibleNewStates();
        int queueSizeBefore = m_queue.size();
        node.getAllPossibleNewStates(m_queue);
        int numPossibleStates = m_queue.size() - queueSizeBefore ;

        if( depthLeft == 0 || numPossibleStates == 0) {
            // first remove all added elements from queue
            dequeueMultiple(m_queue, numPossibleStates);
            // no more depth available, or this node has no children
        //    return new Pair<>( this.heuristicValue(node, depthLeft, numPossibleStates > 0, maximizingPlayer), node );
            m_resultHeuristicValue = this.heuristicValue(node, depthLeft, numPossibleStates > 0, maximizingPlayer);
            m_resultNode = node;
            return ;
        }


        float bestValue = maximizingPlayer ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        GameState bestState = node;

        // look for best value among children
        for( int i=0; i < numPossibleStates ; i++ ) {
            GameState child = m_queue.removeLast();
            minimax(child, depthLeft - 1, !maximizingPlayer);
            if( maximizingPlayer ) {
                if (m_resultHeuristicValue > bestValue) {
                    bestValue = m_resultHeuristicValue;
                    bestState = child;
                }
            } else {
                if(m_resultHeuristicValue < bestValue) {
                    bestValue = m_resultHeuristicValue;
                    bestState = child;
                }
            }
        }

    //    return new Pair<>(bestValue, bestState);
        m_resultHeuristicValue = bestValue;
        m_resultNode = bestState;
    }


    public static void dequeueMultiple( ArrayDeque<GameState> queue, int numTimesToDequeue ) {
        for (int i = 0; i < numTimesToDequeue; i++) {
            queue.removeLast();
        }
    }


}
