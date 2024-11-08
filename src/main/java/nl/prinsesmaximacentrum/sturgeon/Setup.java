package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static java.lang.Math.ceil;

/**
 * Class to create elements for the setup page.
 */
public class Setup {

    private JLabel titleLabel, inputLabel, outputLabel, barcodeLabel, runNameLabel, classLabel, iterLabel, modelLabel,
            advancedLabel, emptyBarcodeLabel, emptyRunLabel;
    private JButton inputButton, outputButton, modelButton;
    private JTextField inputField, outputField, runNameField, barcodeField, modelField;
    private JComboBox<Integer> iterBox;
    private JComboBox<Boolean> classBox;
    private final double labelMultiplier = 0.01, titleMultiplier = 0.03, subtitleMultiplier = 0.015;
    private ColorConfig colorConfig;
    private Config config;
    private Logger logger;

    public Setup(ColorConfig colorConfig, Config config, Logger logger) {
        this.colorConfig = colorConfig;
        this.config = config;
        this.logger = logger;
        this.setLabels();
        this.setButtons();
        this.setTextFields();
        this.setComboBoxes();
    }

    public void addElements(Container container) {
        container.add(titleLabel);

        container.add(inputLabel);
        container.add(inputField);
        container.add(inputButton);

        container.add(advancedLabel);

        container.add(outputLabel);
        container.add(outputField);
        container.add(outputButton);

        container.add(classLabel);
        container.add(classBox);

        container.add(runNameLabel);
        container.add(runNameField);
        container.add(emptyRunLabel);

        container.add(iterLabel);
        container.add(iterBox);

        container.add(barcodeLabel);
        container.add(barcodeField);
        container.add(emptyBarcodeLabel);

        container.add(modelLabel);
        container.add(modelField);
        container.add(modelButton);

    }

    private void setLabels() {
        this.titleLabel = new JLabel("Setup", JTextField.CENTER);
        this.inputLabel = new JLabel("Pod5 input folder", JTextField.RIGHT);
        this.outputLabel = new JLabel("Result location", JTextField.RIGHT);
        this.barcodeLabel = new JLabel("Used barcode", JTextField.RIGHT);
        this.runNameLabel = new JLabel("Output folder", JTextField.RIGHT);
        this.classLabel = new JLabel("Use unclassified barcodes?", JTextField.RIGHT);
        this.iterLabel = new JLabel("<HTML>Number of iterations<p>before new CNV</HTML>", JTextField.RIGHT);
        this.modelLabel = new JLabel("Prediction Model", JTextField.RIGHT);
        this.advancedLabel = new JLabel("------Advanced options------", JTextField.CENTER);
        this.emptyBarcodeLabel = new JLabel();
        this.emptyRunLabel = new JLabel();

        for (JLabel label : new JLabel[]{titleLabel, inputLabel, outputLabel, barcodeLabel, runNameLabel, classLabel,
                iterLabel, modelLabel, advancedLabel, emptyBarcodeLabel, emptyRunLabel}) {
            label.setForeground(Color.LIGHT_GRAY);
        }
    }

    private void setButtons() {
        // Using HTML to create a newline and adds a tag to know which field need to have the outcome of the button
        this.inputButton = new JButton("<html>Choose<p class=input>Folder</html>");
        this.outputButton = new JButton("<html>Choose<p class=output>Folder</html>");
        this.modelButton = new JButton("<html>Choose<p class=config>File</html>");

        for (JButton button : new JButton[]{inputButton, outputButton, modelButton}) {
            button.setBackground(colorConfig.getFileButton());
            button.setOpaque(true);

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseClicked(MouseEvent e){
                    String buttonText = button.getText();
                    TargetFinder finder = new TargetFinder(Setup.this.getTextField(buttonText),
                            (!buttonText.contains("class=config")),
                            Setup.this.config.getDefaultDir());
                    finder.start();
                }
            });
        }
    }

    private JTextField getTextField(String key){
        if (key.contains("class=input")) {
            return inputField;
        } else if (key.contains("class=output")) {
            return outputField;
        } else {
            return modelField;
        }
    }

    private void setTextFields() {
        this.inputField = new JTextField();
        this.outputField = new JTextField();
        this.runNameField = new JTextField();
        this.barcodeField = new JTextField();
        this.modelField = new JTextField(config.getModel());

        for (JTextField textField : new JTextField[]{inputField, outputField, modelField, runNameField}){
            textField.setHorizontalAlignment(SwingConstants.LEFT);
        }
        for (JTextField textField : new JTextField[]{barcodeField}){
            textField.setHorizontalAlignment(SwingConstants.CENTER);
        }
    }

    private void setComboBoxes() {
        this.classBox = new JComboBox<>(new Boolean[]{true, false});
        this.classBox.setSelectedIndex(0);
        this.classBox.setBackground(Color.white);

        this.iterBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10});
        this.iterBox.setSelectedIndex(4);
        this.iterBox.setBackground(Color.white);

    }

    public void setSizes(Rectangle size) {
        this.setLabelSizes(size);
        this.setFieldSizes(size);
        this.setButtonSizes(size);
        this.setComboBoxSizes(size);
    }

    private void setLabelSizes(Rectangle size){
        double fontSize = (double) size.height + size.width;
        for (JLabel label : new JLabel[]{inputLabel, outputLabel, barcodeLabel, runNameLabel, modelLabel,
                                         classLabel, iterLabel}) {
            label.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.16),
                    (int) ceil(size.height * 0.2)
            ));
            label.setFont(new Font("Arial", Font.BOLD,
                    (int) ceil(fontSize * labelMultiplier)
            ));
        }
        for (JLabel label : new JLabel[]{classLabel, iterLabel}) {
            label.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.25),
                    (int) ceil(size.height * 0.2)
            ));
        }
        for (JLabel label : new JLabel[]{emptyBarcodeLabel, emptyRunLabel}) {
            label.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.1),
                    (int) ceil(size.height * 0.15)
            ));
        }

        titleLabel.setPreferredSize(new Dimension(
                size.width,
                (int) ceil(size.height * 0.10)
        ));
        titleLabel.setFont(new Font("Arial", Font.BOLD,
                (int) ceil(fontSize * titleMultiplier)
        ));

        advancedLabel.setPreferredSize(new Dimension(
                (int) ceil(size.width * 0.40),
                (int) ceil(size.height * 0.10)
        ));
        advancedLabel.setFont(new Font("Arial", Font.BOLD,
                (int) ceil(fontSize * subtitleMultiplier)
        ));


    }

    private void setFieldSizes(Rectangle size) {
        double fontSize = (double) size.height + size.width;
        for (JTextField field : new JTextField[]{inputField, outputField, runNameField, barcodeField, modelField}) {
            field.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.15),
                    (int) ceil(size.height * 0.1)
            ));
            field.setFont(new Font("Arial", Font.PLAIN,
                    (int) ceil(fontSize * labelMultiplier)
            ));
        }
    }

    private void setButtonSizes(Rectangle size) {
        double fontSize = (double) size.height + size.width;
        for (JButton button : new JButton[]{inputButton, outputButton, modelButton}) {
            button.setFocusable(false);
            button.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.1),
                    (int) ceil(size.height * 0.09)
            ));
            button.setFont(new Font("Arial", Font.BOLD,
                    (int) ceil(fontSize * labelMultiplier * 0.8)
            ));
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
    }

    private void setComboBoxSizes(Rectangle size) {
        double fontSize = (double) size.height + size.width;
        for (JComboBox comboBox : new JComboBox[]{iterBox, classBox}) {
            comboBox.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.15),
                    (int) ceil(size.height * 0.2)
            ));
            comboBox.setFont(new Font("Arial", Font.BOLD,
                    (int) ceil(fontSize * labelMultiplier)
            ));
        }
    }

    public boolean validateSetup() {
        logger.addToLog("Validating setup with the following values:" +
                "\ninput: " + this.inputField.getText() +
                "\noutput: " + this.outputField.getText() + "/" + this.runNameField.getText() +
                "\nbarcode: " + this.barcodeField.getText() +
                "\nUnclassified barcodes: " + this.classBox.getSelectedItem().toString() +
                "\nNumber iterations: " + this.iterBox.getSelectedItem().toString() +
                "\nModel: " + this.modelField.getText());
        for (String checkStr : new String[]{runNameField.getText(), barcodeField.getText(), inputField.getText(),
                                            outputField.getText(), modelField.getText()}) {
            if (Objects.equals(checkStr, "")) {
                return false;
            }
        }
        return !Files.exists(Paths.get(outputField.getText() + "/" + this.runNameField.getText()));
    }

    public JLabel getTitleLabel() {
        return titleLabel;
    }

    public JLabel getInputLabel() {
        return inputLabel;
    }

    public JLabel getOutputLabel() {
        return outputLabel;
    }

    public JLabel getBarcodeLabel() {
        return barcodeLabel;
    }

    public JLabel getRunNameLabel() {
        return runNameLabel;
    }

    public JLabel getClassLabel() {
        return classLabel;
    }

    public JLabel getIterLabel() {
        return iterLabel;
    }

    public JLabel getModelLabel() {
        return modelLabel;
    }

    public JLabel getAdvancedLabel() {
        return advancedLabel;
    }

    public JLabel getEmptyBarcodeLabel() {
        return emptyBarcodeLabel;
    }

    public JLabel getEmptyRunLabel() {
        return emptyRunLabel;
    }

    public JButton getInputButton() {
        return inputButton;
    }

    public JButton getOutputButton() {
        return outputButton;
    }

    public JButton getModelButton() {
        return modelButton;
    }

    public JTextField getInputField() {
        return inputField;
    }

    public JTextField getOutputField() {
        return outputField;
    }

    public JTextField getRunNameField() {
        return runNameField;
    }

    public JTextField getBarcodeField() {
        return barcodeField;
    }

    public JTextField getModelField() {
        return modelField;
    }

    public JComboBox<Integer> getIterBox() {
        return iterBox;
    }

    public JComboBox<Boolean> getClassBox() {
        return classBox;
    }

    public double getLabelMultiplier() {
        return labelMultiplier;
    }

    public double getTitleMultiplier() {
        return titleMultiplier;
    }

    public double getSubtitleMultiplier() {
        return subtitleMultiplier;
    }
}
