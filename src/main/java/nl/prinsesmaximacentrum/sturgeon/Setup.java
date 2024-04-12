package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

import static java.lang.Math.ceil;

public class Setup {

    private JLabel titleLabel, inputLabel, outputLabel, barcodeLabel, bmLabel, classLabel, iterLabel, configLabel,
            advancedLabel, emptyBarcodeLabel, emptyBmLabel;
    private JButton inputButton, outputButton, configButton;
    private JTextField inputField, outputField, bmField, barcodeField, configField;
    private JComboBox<Integer> iterBox;
    private JComboBox<Boolean> classBox;
    private Biomaterial biomaterial = new Biomaterial();
    private final double labelMultiplier = 0.01, titleMultiplier = 0.03, subtitleMultiplier = 0.015;
    private ColorConfig colorConfig;

    public Setup(ColorConfig colorConfig) {
        this.colorConfig = colorConfig;
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

        container.add(barcodeLabel);
        container.add(barcodeField);
        container.add(emptyBarcodeLabel);

        container.add(iterLabel);
        container.add(iterBox);

        container.add(bmLabel);
        container.add(bmField);
        container.add(emptyBmLabel);

        container.add(configLabel);
        container.add(configField);
        container.add(configButton);
    }

    private void setLabels() {
        this.titleLabel = new JLabel("Setup", JTextField.CENTER);
        this.inputLabel = new JLabel("Pod5 input folder", JTextField.RIGHT);
        this.outputLabel = new JLabel("Result output folder", JTextField.RIGHT);
        this.barcodeLabel = new JLabel("Used barcode", JTextField.RIGHT);
        this.bmLabel = new JLabel("Biomaterial ID", JTextField.RIGHT);
        this.classLabel = new JLabel("Use unclassified barcodes?", JTextField.RIGHT);
        this.iterLabel = new JLabel("Number of iterations before new CNV", JTextField.RIGHT);
        this.configLabel = new JLabel("Config file", JTextField.RIGHT);
        this.advancedLabel = new JLabel("------Advanced options------", JTextField.CENTER);
        this.emptyBarcodeLabel = new JLabel();
        this.emptyBmLabel = new JLabel();

        for (JLabel label : new JLabel[]{titleLabel, inputLabel, outputLabel, barcodeLabel, bmLabel, classLabel,
                iterLabel, configLabel, advancedLabel}) {
            label.setForeground(Color.LIGHT_GRAY);
//            label.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
        }
    }

    private void setButtons() {
        // Using HTML to create a newline and adds a tag to know which field need to have the outcome of the button
        this.inputButton = new JButton("<html>Choose<p class=input>Folder</html>");
        this.outputButton = new JButton("<html>Choose<p class=output>Folder</html>");
        this.configButton = new JButton("<html>Choose<p class=config>File</html>");

        for (JButton button : new JButton[]{inputButton, outputButton, configButton}) {
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
                            (!buttonText.contains("class=config")));
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
            return configField;
        }
    }

    private void setTextFields() {
        this.inputField = new JTextField();
        this.outputField = new JTextField();
        this.bmField = new JTextField();
        this.barcodeField = new JTextField();
        this.configField = new JTextField();

        for (JTextField textField : new JTextField[]{inputField, outputField, configField}){
            textField.setHorizontalAlignment(SwingConstants.LEFT);
        }
        for (JTextField textField : new JTextField[]{bmField, barcodeField}){
            textField.setHorizontalAlignment(SwingConstants.CENTER);
        }

        this.bmField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateBm(bmField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateBm(bmField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateBm(bmField.getText());
            }

            private void updateBm(String text) {
                Setup.this.biomaterial.setBm(text);
            }
        });
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
        for (JLabel label : new JLabel[]{inputLabel, outputLabel, barcodeLabel, bmLabel, configLabel,
                                         classLabel, iterLabel}) {
            label.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.15),
                    (int) ceil(size.height * 0.2)
            ));
            label.setFont(new Font("Arial", Font.BOLD,
                    (int) ceil(fontSize * labelMultiplier)
            ));
        }
        for (JLabel label : new JLabel[]{classLabel, iterLabel}) {
            label.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.34),
                    (int) ceil(size.height * 0.2)
            ));
        }
        for (JLabel label : new JLabel[]{emptyBarcodeLabel, emptyBmLabel}) {
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
                (int) ceil(size.width * 0.45),
                (int) ceil(size.height * 0.10)
        ));
        advancedLabel.setFont(new Font("Arial", Font.BOLD,
                (int) ceil(fontSize * subtitleMultiplier)
        ));


    }

    private void setFieldSizes(Rectangle size) {
        double fontSize = (double) size.height + size.width;
        for (JTextField field : new JTextField[]{inputField, outputField, bmField, barcodeField, configField}) {
            field.setPreferredSize(new Dimension(
                    (int) ceil(size.width * 0.2),
                    (int) ceil(size.height * 0.1)
            ));
            field.setFont(new Font("Arial", Font.PLAIN,
                    (int) ceil(fontSize * labelMultiplier)
            ));
        }
    }

    private void setButtonSizes(Rectangle size) {
        double fontSize = (double) size.height + size.width;
        for (JButton button : new JButton[]{inputButton, outputButton, configButton}) {
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

    public String getBiomaterialID() {
        return biomaterial.getBm();
    }

    public boolean validateSetup() {
        for (String checkStr : new String[]{getBiomaterialID(), barcodeField.getText(), inputField.getText(),
                                            outputField.getText(), configField.getText()}) {
            if (Objects.equals(checkStr, "")) {
                return false;
            }
        }
        return true;
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

    public JLabel getBmLabel() {
        return bmLabel;
    }

    public JLabel getClassLabel() {
        return classLabel;
    }

    public JLabel getIterLabel() {
        return iterLabel;
    }

    public JLabel getConfigLabel() {
        return configLabel;
    }

    public JLabel getAdvancedLabel() {
        return advancedLabel;
    }

    public JLabel getEmptyBarcodeLabel() {
        return emptyBarcodeLabel;
    }

    public JLabel getEmptyBmLabel() {
        return emptyBmLabel;
    }

    public JButton getInputButton() {
        return inputButton;
    }

    public JButton getOutputButton() {
        return outputButton;
    }

    public JButton getConfigButton() {
        return configButton;
    }

    public JTextField getInputField() {
        return inputField;
    }

    public JTextField getOutputField() {
        return outputField;
    }

    public JTextField getBmField() {
        return bmField;
    }

    public JTextField getBarcodeField() {
        return barcodeField;
    }

    public JTextField getConfigField() {
        return configField;
    }

    public JComboBox<Integer> getIterBox() {
        return iterBox;
    }

    public JComboBox<Boolean> getClassBox() {
        return classBox;
    }

    public Biomaterial getBiomaterial() {
        return biomaterial;
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
