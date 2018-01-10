package ui;

import gamelogic.Nim;
import gamelogic.Pillar;
import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Canvas extends java.awt.Canvas {

    MainWindow mainWindow;
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();

    int coinWidth = 50;
    int coinHeight = 20;
    int hgap = 5;
    int vgap = 10;



    public Canvas(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                onMouseClicked(mouseEvent);
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

    }


    void onMouseClicked(MouseEvent mouseEvent) {

        // find coin by position

        Nim nim = this.mainWindow.getNim();
        if(null == nim)
            return;

        for(int x=0; x < nim.getGameState().getNumPillars(); x++) {

            for (int y = 0; y < nim.getGameState().getNumCoinsAtPillar(x); y++) {

                Rectangle rect = new Rectangle();
                this.getCoinRectangle(x, y, rect);

                if( rect.contains(mouseEvent.getPoint()) ) {
                    // this coin is clicked
                    this.mainWindow.onClickOnCoin(x, y);
                    return;
                }
            }

        }

    }


    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // draw coins for every pillar

        this.coinRectangles.clear();
        this.getCoinsPositions(this.coinRectangles);

        for (Rectangle rect:
                this.coinRectangles
             ) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        }


    }

    public  void   getCoinsPositions( ArrayList<Rectangle> positions ) {

        Nim nim = this.mainWindow.getNim();
        if(null == nim)
            return;

        for(int x=0; x < nim.getGameState().getNumPillars(); x++) {

            for (int y = 0; y < nim.getGameState().getNumCoinsAtPillar(x); y++) {

                Rectangle rect = new Rectangle();
                this.getCoinRectangle(x, y, rect);

                positions.add(rect);
            }

        }

    }

    public  void getCoinRectangle(int pillarIndex, int coinIndex, Rectangle rect) {

        int canvasHeight = this.getHeight();


        int xPos = pillarIndex * coinWidth + pillarIndex * hgap ;

        int yPos = coinIndex * coinHeight + coinIndex * vgap;
        yPos = canvasHeight - yPos - coinHeight * 2 ;   // reverse along y axis


        rect.x = xPos;
        rect.y = yPos;
        rect.width = this.coinWidth;
        rect.height = this.coinHeight;

    }

}
