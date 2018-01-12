package etf.nim.la150391d.ui;

import etf.nim.la150391d.gamelogic.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * The main class in the program. It creates main window, and has all the responsibilities for starting new game,
 * interacting with the human players and AI, playing moves, etc.
 */
public class MainWindow extends JDialog {
    private JPanel contentPane;
    private JButton newGameButton;
    private JLabel statusLabel;
    private JButton aboutButton;
    private JPanel gamePanel;
    private JCheckBox pauseAICheckBox;

    private Nim nim;
    private Canvas canvas;
    private boolean isAIPaused = false;
    private Timer timer = null;


    /**
     * Ctor. It setups the GUI.
     */
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

    /**
     * Called after ctor. Creates the canvas, starts the timer, and updates UI.
     */
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


    public static void main(String[] args) {
        MainWindow dialog = new MainWindow();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public Nim getNim() {
        return nim;
    }


    /**
     * Displays message box.
     */
    void ShowMessage(String title, String text) {

        JOptionPane.showMessageDialog(this, text, title, JOptionPane.INFORMATION_MESSAGE);

    }

    /**
     * Prints exception to stdout, and displays a message box about it.
     */
    void HandleException(Exception ex) {

        ex.printStackTrace();

        // display message box

        this.ShowMessage("Error: " + ex.getClass().getName(), ex.getMessage());

    }

    /**
     * Called periodically by timer. If AI player is on the move, it obtains a move from AI, and plays it.
     */
    void OnTimerAction() {

        try {

            if (this.nim != null && !this.nim.isGameOver()) {
                // the game is on
                // if AI is on the move, obtain a move from it, and play

                Player player = this.nim.getCurrentPlayer();
                if (player.isAI()) {

                    if (!this.isAIPaused) {

                        Move move = player.getMoveStrategy().getNextMove(this.nim.getGameState());
                        //    System.out.println("obtained move from AI: " + move);
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

            //    System.out.println("timer tick " + new Date());

        } catch (Exception ex) {
            this.HandleException(ex);
        }

    }

    /**
     * Called when mouse has clicked on a coin. If the current player is human player, it tries to play a move.
     * @param pillarIndex pillar index where mouse has clicked
     * @param coinIndex index of coin on specified pillar
     */
    void onClickOnCoin(int pillarIndex, int coinIndex) {

        // human player trying to make a move

        // check if it is his turn
        // if yes, try to make a move

        if (null == this.nim)
            return;

        if (this.nim.getCurrentPlayer().isAI())
            return;

        // how many coins he wants to remove ?
        int numCoinsToRemove = this.nim.getGameState().getNumCoinsAtPillar(pillarIndex) - coinIndex;

        if (!this.nim.getGameState().isMovePossible(pillarIndex, numCoinsToRemove)) {
            // display message
            this.ShowMessage("", "Invalid move: " + new Move(pillarIndex, numCoinsToRemove));
            return;
        }

        this.nim.playMove(new Move(pillarIndex, numCoinsToRemove));

        this.UpdateUI();

        if (this.nim.isGameOver()) {
            this.OnGameOver();
        }

    }

    /**
     * Called when game is over. Displays message box to inform player.
     */
    void OnGameOver() {

        if (null == this.nim)
            return;
        if (!this.nim.isGameOver())
            return;

        // if only 1 player is human, display something like: you won / you lost
        // otherwise just display winner name

        if ((this.nim.getPlayer1().isAI() && !this.nim.getPlayer2().isAI())
                || (this.nim.getPlayer2().isAI() && !this.nim.getPlayer1().isAI())
                ) {
            // only 1 player is human
            this.ShowMessage("GAME OVER", this.nim.getWinningPlayer().isAI() ? "you lost" : "you won");
        } else {
            this.ShowMessage("GAME OVER", "winner is " + this.nim.getWinningPlayer().getName());
        }

    }

    /**
     * Opens the settings/new game dialog.
     */
    SettingsDialog OpenSettingsDialog() {

        SettingsDialog dialog = new SettingsDialog();
        dialog.pack();
        dialog.setLocationRelativeTo(this); // center the dialog
        dialog.setModal(true);
        dialog.setVisible(true);

        return dialog;
    }

    /**
     * Creates MoveStrategy based on enumeration from UI.
     * @param aiStrategy enumeration from UI
     */
    public static MoveStrategy getMoveStrategyFromEnum(SettingsDialog.AIStrategy aiStrategy, int maxTreeDepth) {

        if (aiStrategy == SettingsDialog.AIStrategy.Minmax)
            return new MinMaxMove(maxTreeDepth);
        else if (aiStrategy == SettingsDialog.AIStrategy.AlphaBetaPrunning)
            return new AlfaBetaPrunningMove(maxTreeDepth);
        else if (aiStrategy == SettingsDialog.AIStrategy.Competitive)
            return new CompetitiveMove(maxTreeDepth);

        throw new IllegalArgumentException("no such AI strategy available");
    }

    /**
     * Starts new game. Opens a new game dialog, and if OK was pressed, parses all parameters, and creates a new game.
     */
    void StartNewGame() {

        // open settings dialog
        // if user pressed OK, read params
        // start new game

        try {

            SettingsDialog dialog = this.OpenSettingsDialog();
            if (dialog.getResult() == SettingsDialog.Result.OK) {
                // read params
                SettingsDialog.Params params = dialog.getParams();

                // check if params are valid
                if (params.numPillars > 10)
                    throw new IllegalArgumentException("number of pillars can not be higher than 10");
                int[] inputIntegers = new int[]{params.numPillars, params.timerInterval, params.maxTreeDepth1,
                        params.maxTreeDepth2};
                for (int inputInteger : inputIntegers) {
                    if (inputInteger <= 0)
                        throw new IllegalArgumentException("invalid input");
                }

                // create pillars
                ArrayList<Integer> pillars = new ArrayList<>();
                for (int i = 0; i < params.numPillars; i++) {
                    //Pillar pillar = new Pillar(params.numCoinsPerPillar[i]);
                    pillars.add(params.numCoinsPerPillar[i]);
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
                if (player1.isAI()) {
                    player1.setMoveStrategy(getMoveStrategyFromEnum(params.aiStrategy1, params.maxTreeDepth1));
                }
                if (player2.isAI()) {
                    player2.setMoveStrategy(getMoveStrategyFromEnum(params.aiStrategy2, params.maxTreeDepth2));
                }

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

    /**
     * Called when new game is started.
     */
    void OnNewGameStarted() {

        System.out.println("New game started");

        this.UpdateUI();

    }

    /**
     * Called when checkbox for pausing AI is changed.
     */
    void OnPauseAICheckBoxAction() {

        this.isAIPaused = this.pauseAICheckBox.isSelected();

    }

    /**
     * Updates status label and canvas.
     */
    void UpdateUI() {

        this.UpdateStatusLabel();
        this.UpdateCanvas();

    }

    /**
     * Updates text for status label.
     */
    void UpdateStatusLabel() {

        String str = "";

        if (this.nim != null) {

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

    /**
     * Repaints the canvas.
     */
    void UpdateCanvas() {

        this.canvas.repaint();

    }

    /**
     * Opens about dialog which shows information about this program.
     */
    void OpenAboutDialog() {

        String str = "Nim\n\nminmax tree game\n\nbuilt by in0finite => github.com/in0finite" +
                "\n\nLicense: MIT";

        JOptionPane.showMessageDialog(this, str,
                "About", JOptionPane.INFORMATION_MESSAGE);

    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        contentPane.setPreferredSize(new Dimension(800, 480));
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout(0, 0));
        contentPane.add(gamePanel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        statusLabel = new JLabel();
        Font statusLabelFont = this.$$$getFont$$$(null, Font.BOLD, -1, statusLabel.getFont());
        if (statusLabelFont != null) statusLabel.setFont(statusLabelFont);
        statusLabel.setForeground(new Color(-15328536));
        statusLabel.setHorizontalAlignment(2);
        statusLabel.setHorizontalTextPosition(2);
        statusLabel.setText("<html>Please start a <br> new game</html>");
        gamePanel.add(statusLabel, BorderLayout.NORTH);
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setFloatable(false);
        contentPane.add(toolBar1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(-1, 20), null, 0, false));
        newGameButton = new JButton();
        newGameButton.setText("New game");
        toolBar1.add(newGameButton);
        pauseAICheckBox = new JCheckBox();
        pauseAICheckBox.setText("pause AI");
        toolBar1.add(pauseAICheckBox);
        aboutButton = new JButton();
        aboutButton.setText("About");
        toolBar1.add(aboutButton);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
