package gamelogic;

import java.util.ArrayList;

public class Pillar {

    //ArrayList<Coin> m_coins = new ArrayList<>();
    int m_numCoins = 0;



    public Pillar(int numCoins) {
        m_numCoins = numCoins;
    }

    public Pillar(Pillar pillar) {
        this(pillar.m_numCoins);
    }

    public int getNumCoins() {
        return m_numCoins;
    }

    public boolean  removeCoins(int count) {

//        if(m_coins.size() < count)
//            return false;
//
//        for (int i = 0; i < count; i++) {
//            m_coins.remove(0);
//        }

        if(m_numCoins < count)
            return false;

        m_numCoins -= count;

        return true;
    }


}
