package gamelogic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Nim {


    ArrayList<Pillar>   m_pillars = new ArrayList<>();

    ArrayList<Move> m_moves = new ArrayList<>();

    int m_currentPlayer = 0;

    Player  m_player1 = null;
    Player  m_player2 = null;



    public  Nim(Iterable<Pillar> pillars, Player player1, Player player2) throws Exception {

        if(!IsValidState(pillars)) {
            // can't start the game
            throw new Exception("Invalid state of pillars");
        }

        for (Pillar pillar : pillars) {
            if(pillar.getNumCoins() == 0)
                throw new Exception("Invalid state of pillars");
        }

        m_pillars.addAll( (Collection<Pillar>) pillars );

        m_player1 = player1;
        m_player2 = player2;

    }


    public Player getPlayer1() {
        return m_player1;
    }

    public Player getPlayer2() {
        return m_player2;
    }


    public  static  boolean IsValidState(Iterable<Pillar> pillars ) {

        HashSet<Integer> foundNumbers = new HashSet<Integer>();

        for(Pillar p : pillars) {
            Integer numCoins = p.getNumCoins();

            if(foundNumbers.contains(numCoins))
                return false;

            foundNumbers.add(numCoins);
        }

        return true;
    }


    public Player   getCurrentPlayer() {

        if(0 == m_currentPlayer)
            return m_player1;

        return m_player2;
    }


    public boolean  playMove(Move move) {

        if(!this.isMovePossible(move))
            return false;

        // remove coins from pillar
        Pillar p = m_pillars.get( move.getPillarIndex() );
        if(!p.removeCoins(move.getNumCoinsTaken()))
            return false;

        // remember this move
        m_moves.add(move);

        // switch player
        m_currentPlayer = (m_currentPlayer + 1) % 2 ;

        return true;
    }

    /// Plays the next move using the current player.
//    public  boolean playNextMove() {
//
//        Player player = this.getCurrentPlayer();
//
//        // obtain next move from the player
//        Move move = player.getNextMove(this);
//
//        return this.playMove(move);
//
//    }

    public  boolean isMovePossible(Move move) {

        // TODO: not finished

        return false;
    }



}
