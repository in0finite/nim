package gamelogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * Nim game. Handles all stuff related to game: game state, players, turns, game over, etc.
 */
public class Nim {


    //ArrayList<Pillar>   m_pillars = new ArrayList<>();
    GameState   m_gameState = null;

    ArrayList<Move> m_moves = new ArrayList<>();

    int m_currentPlayer = 0;

    boolean m_isGameOver = false;
    Player  m_winningPlayer = null;

    Player  m_player1 = null;
    Player  m_player2 = null;


    /**
     * Starts a new game using the specified number of coins per pillar, and specified players.
     */
    public  Nim(Iterable<Integer> coinsPerPillar, Player player1, Player player2) throws Exception {

        if(!IsValidState(coinsPerPillar)) {
            // can't start the game
            throw new Exception("Invalid state of pillars");
        }

        for (Integer numCoins : coinsPerPillar) {
            if(numCoins <= 0)
                throw new Exception("Invalid state of pillars");
        }

        m_gameState = new GameState(coinsPerPillar);

        m_player1 = player1;
        m_player2 = player2;

    }


    public Player getPlayer1() {
        return m_player1;
    }

    public Player getPlayer2() {
        return m_player2;
    }

    public GameState getGameState() {
        return m_gameState;
    }

    public boolean isGameOver() {
        return m_isGameOver;
    }

    public Player getWinningPlayer() {
        return m_winningPlayer;
    }


    /**
     * Determines if the specified numbers of coins per pillar represent a valid game state.
     */
    public  static  boolean IsValidState(Iterable<Integer> coinsPerPillar ) {

        HashSet<Integer> foundNumbers = new HashSet<Integer>();

        for(Integer numCoins : coinsPerPillar) {

            if(foundNumbers.contains(numCoins))
                return false;

            foundNumbers.add(numCoins);
        }

        return true;
    }


    /**
     * Get the player who should play the next move.
     */
    public Player   getCurrentPlayer() {

        if(0 == m_currentPlayer)
            return m_player1;

        return m_player2;
    }


    /**
     * Play the specified move as the current player.
     * @return false if it is game over, or if the move is not valid, otherwise true
     */
    public boolean  playMove(Move move) {

        if(m_isGameOver)
            return false;

        if(!m_gameState.isMovePossible(move))
            return false;

        // remove coins from pillar
        m_gameState.removeCoinsAtPillar( move.getPillarIndex(), move.getNumCoinsTaken() );

        m_gameState.numCoinsRemovedLastTurn = move.numCoinsTaken;

        // remember this move
        m_moves.add(move);

        // check for game over
        if( m_gameState.getNumPossibleMoves() < 1 ) {
            m_isGameOver = true;
            m_winningPlayer = this.getCurrentPlayer();
        }

        // switch player
        Player oldPlayer = this.getCurrentPlayer();
        m_currentPlayer = (m_currentPlayer + 1) % 2 ;

        System.out.println("move has been played: " + move + ", next player is " + this.getCurrentPlayer());
        if(m_isGameOver) {
            System.out.println("GAME OVER - WINNER IS " + oldPlayer);
        }

        return true;
    }


}
