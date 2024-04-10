package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.io.File;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class TargetFinder extends Thread {

    private JFileChooser fileChooser = new JFileChooser();
    private boolean targetDir;
    private File target;
    private JTextField textField;

    public TargetFinder(JTextField textField, Boolean targetDir) {
        this.textField = textField;
        this.targetDir = targetDir;
    }

    public void run() {
        fileChooser.setFileSelectionMode((targetDir) ? JFileChooser.DIRECTORIES_ONLY : JFileChooser.FILES_ONLY);
        int reply = fileChooser.showOpenDialog(fileChooser);
        if (reply == APPROVE_OPTION) {
            target = fileChooser.getSelectedFile();
            textField.setText(target.getAbsolutePath());
            textField.setCaretPosition(textField.getText().length());

        }
    }

}
