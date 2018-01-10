package ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;

public class SettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxPlayerTypes;
    private JTextField textFieldPillars;
    private JTextField textFieldMaxTreeDepth1;
    private JTextField textFieldMoveTime;
    private JTextField textFieldMaxTreeDepth2;
    private JComboBox comboBoxStrategy1;
    private JComboBox comboBoxStrategy2;


    public enum Result {
        OK,
        Cancel
    }

    private Result result = Result.Cancel;



    public SettingsDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        this.result = Result.OK;
        dispose();
    }

    private void onCancel() {
        this.result = Result.Cancel;
        dispose();
    }

    public static void main(String[] args) {
        SettingsDialog dialog = new SettingsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public Result getResult() {
        return result;
    }


    // TODO: add AI vs human
    public enum PlayersType {
        HumanVsAI,
        HumanVsHuman,
        AIVsAI
    }

    public enum AIStrategy {
        Minmax,
        AlphaBetaPrunning,
        Competitive
    }

    public class Params {
        public int playersType = 0;
        public int numPillars = 0;
        public int[] numCoinsPerPillar = null;
        public int maxTreeDepth1 = 1;
        public int maxTreeDepth2 = 1;
        public int timerInterval = 2000;
        public AIStrategy aiStrategy1 = AIStrategy.Minmax;
        public AIStrategy aiStrategy2 = AIStrategy.Minmax;
    }


    /// Read parameters from UI controls.
    public Params   getParams() {

        Params params = new Params();

        params.playersType = this.comboBoxPlayerTypes.getSelectedIndex();
        try(Scanner scanner = new Scanner(this.textFieldPillars.getText())) {
            params.numPillars = scanner.nextInt();
            params.numCoinsPerPillar = new int[params.numPillars];
            for (int i = 0; i < params.numPillars; i++) {
                params.numCoinsPerPillar[i] = scanner.nextInt();
            }
        }
        params.maxTreeDepth1 = readIntFromTextField(this.textFieldMaxTreeDepth1);
        params.maxTreeDepth2 = readIntFromTextField(this.textFieldMaxTreeDepth2);
        params.timerInterval = readIntFromTextField(this.textFieldMoveTime);
        params.aiStrategy1 = AIStrategy.values()[ this.comboBoxStrategy1.getSelectedIndex() ];
        params.aiStrategy2 = AIStrategy.values()[ this.comboBoxStrategy2.getSelectedIndex() ];

        return params;
    }

    public static int readIntFromTextField(JTextField textField) {

        try(Scanner scanner = new Scanner(textField.getText())) {
            return scanner.nextInt();
        }

    }

}
