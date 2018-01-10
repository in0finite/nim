package ui;

import gamelogic.*;

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
    private JCheckBox pauseAICheckBox;
    private JButton optionsButton;

    private gamelogic.Nim nim;
    private Canvas canvas;
    private boolean isAIPaused = false;
    private Timer   timer = null;



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

        pauseAICheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                OnPauseAICheckBoxAction();
            }
        });


        this.OnAfterCtor();

    }

    private void OnAfterCtor() {

        // create canvas
        this.canvas = new Canvas(this);
        this.gamePanel.add(this.canvas, BorderLayout.CENTER);

        // start timer
        this.timer = new Timer(2000, (evnt) -> OnTimerAction());
        this.timer.setRepeats(true);
        this.timer.start();


        this.UpdateUI();

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


    /// Displays message box.
    void ShowMessage( String title, String text ) {

        JOptionPane.showMessageDialog(this, text, title, JOptionPane.INFORMATION_MESSAGE);

    }

    void HandleException(Exception ex) {

        System.out.println(ex);

        // display message box

        this.ShowMessage("Error", ex.getMessage() );

    }

    void OnTimerAction() {

        try {

            if (this.nim != null && !this.nim.isGameOver()) {
                // the game is on
                // if AI is on the move, obtain a move from it, and play

                Player player = this.nim.getCurrentPlayer();
                if (player.isAI()) {

                    if(!this.isAIPaused) {

                        Move move = player.getMoveStrategy().getNextMove(this.nim.getGameState());
                        System.out.println("obtained move from AI: " + move);
                        if (move != null)
                            this.nim.playMove(move);

                        // update UI
                        this.UpdateUI();

                        if (this.nim.isGameOver()) {
                            this.OnGameOver();
                        }

                    }

                } else {
                    // human player
                    // he will manually play his move
                }

            }

            System.out.println("timer tick " + new Date());

        } catch (Exception ex) {
            this.HandleException(ex);
        }

    }

    void onClickOnCoin(int pillarIndex, int coinIndex) {

        // human player trying to make a move

        // check if it is his turn
        // if yes, try to make a move

        if(null == this.nim)
            return;

        if( this.nim.getCurrentPlayer().isAI() )
            return;

        // how many coins he wants to remove ?
        int numCoinsToRemove = this.nim.getGameState().pillars.get(pillarIndex).getNumCoins() - coinIndex ;

        if(!this.nim.getGameState().isMovePossible(pillarIndex, numCoinsToRemove)) {
            // display message
            this.ShowMessage("", "Invalid move");
            return;
        }

        this.nim.playMove(new Move(pillarIndex, numCoinsToRemove));

        this.UpdateUI();

        if(this.nim.isGameOver()) {
            this.OnGameOver();
        }

    }

    void OnGameOver() {

        if(null == this.nim)
            return;
        if(!this.nim.isGameOver())
            return;

        // if only 1 player is human, display something like: you won / you lost
        // otherwise just display winner name

        if( (this.nim.getPlayer1().isAI() && !this.nim.getPlayer2().isAI())
                || (this.nim.getPlayer2().isAI() && !this.nim.getPlayer1().isAI())
                ) {
            // only 1 player is human
            this.ShowMessage("GAME OVER", this.nim.getWinningPlayer().isAI() ? "you lost" : "you won" );
        } else {
            this.ShowMessage("GAME OVER", "winner is " + this.nim.getWinningPlayer().getName());
        }

    }

    SettingsDialog OpenSettingsDialog() {

        SettingsDialog dialog = new SettingsDialog();
        dialog.pack();
        dialog.setModal(true);
        dialog.setVisible(true);

        return dialog;
    }

    void StartNewGame() {

        // open settings dialog
        // if user pressed OK, read params
        // start new game

        try {

            SettingsDialog dialog = this.OpenSettingsDialog();
            if(dialog.getResult() == SettingsDialog.Result.OK) {
                // read params
                SettingsDialog.Params params = dialog.getParams();

                // check if params are valid
                if(params.numPillars > 10)
                    throw new IllegalArgumentException("number of pillars can not be higher than 10");
                int[] inputIntegers = new int[] { params.numPillars, params.timerInterval, params.maxTreeDepth1,
                    params.maxTreeDepth2 };
                for(int inputInteger : inputIntegers) {
                    if(inputInteger <= 0)
                        throw new IllegalArgumentException("invalid input");
                }

                // create pillars
                ArrayList<Pillar> pillars = new ArrayList<>();
                for (int i = 0; i < params.numPillars; i++) {
                    Pillar pillar = new Pillar(params.numCoinsPerPillar[i]);
                    pillars.add(pillar);
                }

                // create players
                Player player1 = null, player2 = null;
                if (params.playersType == SettingsDialog.PlayersType.HumanVsAI.ordinal()) {
                    // 1 human and 1 AI
                    player1 = new Player(Player.getCurrentUserName(), false);
                    player2 = new Player("AI", true);
                } else if (params.playersType == SettingsDialog.PlayersType.HumanVsHuman.ordinal()) {
                    // 2 human players
                    player1 = new Player("Player 1", false);
                    player2 = new Player("Player 2", false);
                } else if (params.playersType == SettingsDialog.PlayersType.AIVsAI.ordinal()) {
                    // 2 AI players
                    player1 = new Player("AI 1", true);
                    player2 = new Player("AI 2", true);
                }

                // assign move strategies for AI players
                // TODO: strategies must be obtained from UI
                if(player1.isAI())
                    player1.setMoveStrategy(new MinMaxMove(params.maxTreeDepth1));
                if(player2.isAI())
                    player2.setMoveStrategy(new MinMaxMove(params.maxTreeDepth2));

                // start the game
                this.nim = new Nim(pillars, player1, player2);

                // update timer interval
                this.timer.setDelay(params.timerInterval);

                this.OnNewGameStarted();

            }

        } catch (Exception ex) {
            this.HandleException(ex);
        }

    }

    void OnNewGameStarted() {

        System.out.println("New game started");

        this.UpdateUI();

    }

    void OnPauseAICheckBoxAction() {

        this.isAIPaused = this.pauseAICheckBox.isSelected() ;

    }

    void UpdateUI() {

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
