package gamelogic;

import util.Pair;

import java.util.HashMap;

/**
 * The best AI player. Uses combination of alpha-beta prunning and something more to achieve better results.
 */
public class CompetitiveMove extends AlfaBetaPrunningMove {


    /**
     * Thrown when victory condition is found. It signals that the search should be stopped.
     * Extends RuntimeException in order to be unchecked.
     */
    public class VictoryException extends RuntimeException {
        public GameState currentNode;
        public GameState lastNode;
    }


    private HashMap<GameState, Pair<Float, Integer>> m_visitedStates = new HashMap<GameState, Pair<Float, Integer>>(16 * 1024 * 1024 * 1024);
    private int m_numStatesEliminated = 0;



    public CompetitiveMove(int maxDepth) {
        //super(Integer.MAX_VALUE);   // not limited by depth
        super(maxDepth);
    }


    @Override
    protected void getNextMoveInternal(GameState nimGameState) {

        m_visitedStates.clear();
        m_numStatesEliminated = 0;

        try {
            super.getNextMoveInternal(nimGameState);
        } catch (VictoryException victoryEx) {
            m_resultNode = victoryEx.lastNode;
            m_resultHeuristicValue = Float.MAX_VALUE;   // has to be done, because the value could be min float
        }

        System.out.println("eliminated " + m_numStatesEliminated + " already visited states, hashmap size " + m_visitedStates.size());

    }

    @Override
    void alphabeta(GameState node, int depthLeft, float α, float β, boolean maximizingPlayer) {

        // check if this state has been searched before
        Pair<Float, Integer> entryInHashMap = null;
        if(maximizingPlayer) {
            entryInHashMap = m_visitedStates.get(node);
            if (entryInHashMap != null) {
                // we searched this node already
                // check at which depth we searched it
                if(depthLeft <= entryInHashMap.secondValue) {
                    // this depth is <= than the one we searched with -> no need to search it again
                    // assign it's results and return
                    m_resultHeuristicValue = entryInHashMap.firstValue;
                    //m_resultNode = foundResult.secondValue;
                    m_resultNode = node;
                    //System.out.println("found already visited state");
                    m_numStatesEliminated++;
                    return;
                }
            }
        }

        // let parent class do the work
//        try {
            super.alphabeta(node, depthLeft, α, β, maximizingPlayer);

//        } catch (VictoryException victoryEx) {
//            // reassign node
//            victoryEx.lastNode = victoryEx.currentNode;
//            victoryEx.currentNode = node;
//            throw victoryEx;
//        }

        // if victory is found, abort search
//        if( (maximizingPlayer && m_resultHeuristicValue == Float.MAX_VALUE) || (!maximizingPlayer && m_resultHeuristicValue == Float.MIN_VALUE) )
//        {
//            System.out.println("Found victory move, value = " + m_resultHeuristicValue + ", depth = " + (this.maxDepth - depthLeft));
//            //m_resultNode = node;    // we need to assign this, because maybe there isn't anyone to catch exception
//            VictoryException victoryException = new VictoryException();
//            victoryException.lastNode = m_resultNode;
//            victoryException.currentNode = node;
//            throw victoryException;
//        }

        // remember this state
        if(maximizingPlayer) {
            if(entryInHashMap != null) {
                // just update values inside Pair
                entryInHashMap.firstValue = m_resultHeuristicValue;
                entryInHashMap.secondValue = depthLeft;
            } else {
                // entry did not exist at the time we accessed
                // add new entry
                // no, we can't add new entry, because it may have already been added
                entryInHashMap = m_visitedStates.get(node);
                if(entryInHashMap != null) {
                    // update values
                    entryInHashMap.firstValue = m_resultHeuristicValue;
                    entryInHashMap.secondValue = depthLeft;
                } else {
                    // add new entry
                    entryInHashMap = new Pair<>(m_resultHeuristicValue, depthLeft);
                    m_visitedStates.put(node, entryInHashMap);
                }
                //entryInHashMap = m_visitedStates.putIfAbsent(node, new Pair<>(m_resultHeuristicValue, depthLeft));

            }
        }

    }

}
