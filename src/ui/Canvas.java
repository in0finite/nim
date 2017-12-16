package ui;

import gamelogic.Nim;
import gamelogic.Pillar;

import java.awt.*;

public class Canvas extends java.awt.Canvas {

    MainWindow mainWindow;


    public Canvas(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }


    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Nim nim = this.mainWindow.getNim();
        if(null == nim)
            return;

        // draw coins for every pillar

        int coinWidth = 50;
        int coinHeight = 20;
        int hgap = 5;
        int vgap = 10;
        //int canvasWidth = this.getWidth();
        int canvasHeight = this.getHeight();

        for(int x=0; x < nim.getGameState().pillars.size(); x++) {

            Pillar pillar = nim.getGameState().pillars.get(x);

            for (int y = 0; y < pillar.getNumCoins(); y++) {
                int yPos = y * coinHeight + y * vgap;
                yPos = canvasHeight - yPos - coinHeight * 2 ;
                g.fillRect(x * coinWidth + x * hgap, yPos, coinWidth, coinHeight);
            }

        }

    }

}
