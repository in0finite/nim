package ui;

import gamelogic.Nim;
import gamelogic.Pillar;
import gamelogic.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;

public class MainWindow extends JDialog {
    private JPanel contentPane;
    private JButton newGameButton;
    private JLabel statusLabel;
    private JButton aboutButton;
    private JPanel gamePanel;

    private gamelogic.Nim nim;
    private Canvas canvas;



    public MainWindow() {
        setContentPane(contentPane);
        setModal(true);
        //getRootPane().setDefaultButton(buttonOK);

//        // call onCancel() when cross is clicked
//        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                onCancel();
//            }
//        });

//        // call onCancel() on ESCAPE
//        contentPane.registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                onCancel();
//            }
//        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StartNewGame();
            }
        });

        aboutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OpenAboutDialog();
            }
        });

        // create canvas
        this.canvas = new Canvas(this);
        this.gamePanel.add(this.canvas, BorderLayout.CENTER);

        // start timer
        Timer timer = new Timer(2000, (evnt) -> OnTimerAction());
        timer.setRepeats(true);
        timer.start();


    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        MainWindow dialog = new MainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public Nim getNim() {
        return nim;
    }


    void HandleException(Exception ex) {

        System.out.println(ex);

        // display message box

        JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);

    }

    void OnTimerAction() {

        if(this.nim != null) {
            // the game is on
            // if AI is on the move, obtain a move from it, and play

        //    this.nim.getCurrentPlayer();
        }

        System.out.println("timer tick " + new Date() + " " + Thread.currentThread());

    }

    void StartNewGame() {

        // open new game dialog
        // if user pressed OK, read params
        // start new game

        try {

            NewGameDialog dialog = new NewGameDialog();
            dialog.pack();
            dialog.setModal(true);
            dialog.setVisible(true);

            if (dialog.getResult() == NewGameDialog.Result.OK) {
                // read params
                NewGameDialog.Params params = dialog.getParams();

                // create pillars
                ArrayList<Pillar> pillars = new ArrayList<>();
                for (int i = 0; i < params.numPillars; i++) {
                    Pillar pillar = new Pillar(params.numCoinsPerPillar[i]);
                    pillars.add(pillar);
                }

                // create players
                Player player1 = null, player2 = null;
                if (params.playersType == NewGameDialog.PlayersType.HumanVsAI.ordinal()) {
                    // 1 human and 1 AI
                    player1 = new Player(Player.getCurrentUserName(), false);
                    player2 = new Player("AI", true);
                } else if (params.playersType == NewGameDialog.PlayersType.HumanVsHuman.ordinal()) {
                    // 2 human players
                    player1 = new Player("Player 1", false);
                    player2 = new Player("Player 2", false);
                } else if (params.playersType == NewGameDialog.PlayersType.AIVsAI.ordinal()) {
                    // 2 AI players
                    player1 = new Player("AI 1", true);
                    player2 = new Player("AI 2", true);
                }

                // start the game
                this.nim = new Nim(pillars, player1, player2);

                this.OnNewGameStarted();

            }

        } catch (Exception ex) {
            this.HandleException(ex);
        }

    }

    void OnNewGameStarted() {

        this.UpdateStatusLabel();
        this.UpdateCanvas();

    }

    void UpdateStatusLabel() {

        String str = "";

        if(this.nim != null) {

            // use html so we can display new line
            str += "<html>";
            str += this.nim.getPlayer1().getName() + "&nbsp;&nbsp;&nbsp;VS&nbsp;&nbsp;&nbsp;" + this.nim.getPlayer2().getName();
            str += "<br><br>" + "Current turn: " + this.nim.getCurrentPlayer().getName();
            str += "</html>";

        } else {
            str = "Game is not started";
        }

        this.statusLabel.setText(str);

    }

    void UpdateCanvas() {

        this.canvas.repaint();

    }

    void OpenAboutDialog() {

        String str = "Nim\n\nminmax tree game (or something like that, I don't know :D)\n\nbuilt by in0finite => github.com/in0finite" +
                "\n\nLicense: MIT";

        JOptionPane.showMessageDialog(this, str,
                "About", JOptionPane.INFORMATION_MESSAGE);

    }

}
