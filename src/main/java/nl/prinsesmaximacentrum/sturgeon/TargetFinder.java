package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static javax.swing.JFileChooser.APPROVE_OPTION;

/**
 * Class to find files and folders based on the user input
 */
public class TargetFinder extends Thread {

    private JFileChooser fileChooser;
    private boolean targetDir;
    private File target;
    private JTextField textField;

    public TargetFinder(JTextField textField, Boolean targetDir, String default_dir) {
        this.textField = textField;
        this.targetDir = targetDir;
        if (Files.exists(Paths.get(default_dir))) {
            fileChooser = new JFileChooser(default_dir);
        } else {
            System.out.println("default folder not found: " + default_dir);
            fileChooser = new JFileChooser();
        }
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
