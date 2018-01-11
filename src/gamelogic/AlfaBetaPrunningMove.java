package gamelogic;

import util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class AlfaBetaPrunningMove extends MinMaxMove {


    public AlfaBetaPrunningMove(int maxDepth) {
        super(maxDepth);
    }


    @Override
    protected void getNextMoveInternal(GameState nimGameState) {

        alphabeta(nimGameState, this.maxDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);

    }


    void alphabeta(GameState node, int depthLeft, float α, float β, boolean maximizingPlayer) {

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

        int numElementsLeftToRemove = numPossibleStates;

        for( int i=0; i < numPossibleStates; i++ ) {
            GameState child = m_queue.removeLast();
            numElementsLeftToRemove --;

            alphabeta(child, depthLeft - 1, α, β, !maximizingPlayer);

            if(maximizingPlayer) {
                if (m_resultHeuristicValue > bestValue) {
                    bestValue = m_resultHeuristicValue;
                    bestState = child;
                }
                α = Math.max(α, bestValue);
                if (β <= α)
                    break; // β cut-off
            } else {
                if(m_resultHeuristicValue < bestValue) {
                    bestValue = m_resultHeuristicValue;
                    bestState = child;
                }
                β = Math.min(β, bestValue);
                if( β <= α )
                    break; // α cut-off
            }

        }

        // remove the rest of elements from queue - because we may have exited from loop using break
        dequeueMultiple(m_queue, numElementsLeftToRemove);


    //    return new Pair<>(bestValue, bestState);
        m_resultHeuristicValue = bestValue;
        m_resultNode = bestState;
    }


}
