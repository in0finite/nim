package ui;

import javax.swing.*;
import java.awt.event.*;
import java.util.Scanner;

public class NewGameDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxPlayerTypes;
    private JTextField textFieldPillars;
    private JTextField textFieldMaxTreeDepth;


    public enum Result {
        OK,
        Cancel
    }

    private Result result = Result.Cancel;



    public NewGameDialog() {
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
        NewGameDialog dialog = new NewGameDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }


    public Result getResult() {
        return result;
    }


    public enum PlayersType {
        HumanVsAI,
        HumanVsHuman,
        AIVsAI
    }

    public class Params {
        public int playersType = 0;
        public int numPillars = 0;
        public int[] numCoinsPerPillar = null;
        public int maxTreeDepth = 1;
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
        try(Scanner scanner = new Scanner(this.textFieldMaxTreeDepth.getText())) {
            params.maxTreeDepth = scanner.nextInt();
        }

        return params;
    }

}
