package etf.nim.la150391d.gamelogic;

/**
 * Computes the move using alpha-beta prunning.
 */
public class AlfaBetaPrunningMove extends MinMaxMove {


    public AlfaBetaPrunningMove(int maxDepth) {
        super(maxDepth);
    }


    /**
     * Overridden to use alpha-beta search.
     */
    @Override
    protected void getNextMoveInternal(GameState nimGameState) {

        alphabeta(nimGameState, this.maxDepth, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY, true);

    }


    /**
     * Performs alpha-beta search. Results (best node and it's heuristic value) are stored in class' variables.
     * @param node starting node
     * @param depthLeft how much depth is left ?
     * @param α current α
     * @param β current β
     * @param maximizingPlayer is this max or min level ?
     */
    void alphabeta(GameState node, int depthLeft, float α, float β, boolean maximizingPlayer) {

        if(minimaxStart(node, depthLeft, maximizingPlayer))
            return;

        int numPossibleStates = m_numPossibleStates;


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
