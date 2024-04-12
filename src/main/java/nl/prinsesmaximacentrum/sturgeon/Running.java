package nl.prinsesmaximacentrum.sturgeon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.ceil;

public class Running {

    private String inputFolder, outputFolder, barcode, biomaterial, configFile;
    private boolean useUnclass;
    private int numberInterations;
    private JPanel displayPanel;
    private JTextComponent logComponent;
    private JTextField titleField;
    private Pattern iterPattern = Pattern.compile("iteration_[0-9]+");
    private String predictPlotPath = "", confidencePlotPath = "", confidenceTablePath = "", cnvPlotPath = "";
    private final String predictTile = "Prediction of Iteration ", confidenceTitle = "Confidence of Iteration ",
                         cnvTitle = "CNV Plot Iteration ";
    private Config config;
    private Menu menu;

    public Running(String inputFolder, String outputFolder, String barcode,
                   String biomaterial, String configFile, boolean useUnclass,
                   int numberInterations, JTextComponent logComponent,
                   JPanel displayPanel, Config config, Menu menu) {
        this.config = config;
        this.menu = menu;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.barcode = barcode;
        this.biomaterial = biomaterial;
        this.configFile = configFile;
        this.useUnclass = useUnclass;
        this.numberInterations = numberInterations;
        this.logComponent = logComponent;
        this.displayPanel = displayPanel;
        this.setTextFields();
    }

    private void setTextFields() {
        this.titleField = new JTextField("Waiting for the first iteration to finish...");
        this.titleField.setBorder(BorderFactory.createEmptyBorder());
        this.titleField.setForeground(Color.white);

    }

    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("/bin/bash", "-c", "echo finished iteration_1;");
            Process proc = pb.start();

            Thread outputReader = new Thread(() -> {
                try (InputStream inputStream = proc.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String responseLine;
                    while ((responseLine = reader.readLine()) != null) {
                        this.log(responseLine);
                        Matcher matcher = iterPattern.matcher(responseLine);
                        if (matcher.find()) {
                            checkNewResults(matcher.group(0));
                        }
                    }
                    this.log("Finished!");
                } catch (IOException e) {
                    this.log("ERROR: " + e.getMessage());
                }
            });
            outputReader.start();

        } catch (IOException e) {
            this.log("ERROR: " + e.getMessage());
        }

    }

    private void checkNewResults(String iteration) {
        String iterationFolder = outputFolder + "/" + iteration + "/";
        List<List<String>> targets = Arrays.asList(
                Arrays.asList(this.confidencePlotPath, iterationFolder + config.getConfidencePlot(), "confidencePlot"),
                Arrays.asList(this.cnvPlotPath, iterationFolder + config.getCnvPlot(), "cnvPlot"),
                Arrays.asList(this.predictPlotPath, iterationFolder + config.getPredictPlot(), "predictPlot"),
                Arrays.asList(this.confidenceTablePath, iterationFolder + config.getConfidenceTable(), "confidenceTable")
        );
        for (List<String> targetStrings : targets) {
            String targetObject = targetStrings.get(0);
            if (!targetObject.contains(iteration)) {
                String filePath = targetStrings.get(1).replaceAll(iterPattern.pattern(), iteration);
                File targetFile = new File(filePath);
                if (targetFile.exists() && !targetFile.isDirectory()) {
                    setResultPath(targetStrings.get(2), filePath, iteration);
                }
            }
        }
    }

    private void setResultPath(String resultObject, String resultPath, String iteration) {
        String[] iterationSplit = iteration.split("_");
        iteration = iterationSplit[iterationSplit.length - 1];

        if (Objects.equals(resultObject, "confidencePlot") | Objects.equals(resultObject, "confidenceTable")) {
            if (Objects.equals(resultObject, "confidencePlot")) {
                this.confidencePlotPath = resultPath;
            } else if (Objects.equals(resultObject, "confidenceTable")) {
                this.confidenceTablePath = resultPath;
            }
            this.setActionButtons(this.menu.getConfidenceButton(),
                    confidenceTitle + iteration,
                    new String[]{this.confidencePlotPath});
        } else if (Objects.equals(resultObject, "predictPlot")) {
            this.predictPlotPath = resultPath;
            this.setActionButtons(this.menu.getPredictionButton(),
                    predictTile + iteration,
                    new String[]{this.predictPlotPath});
        } else {
            this.cnvPlotPath = resultPath;
            this.setActionButtons(this.menu.getCnvPlotButton(),
                    cnvTitle + iteration,
                    new String[]{this.cnvPlotPath});
        }
    }

    private void setActionButtons(JButton button, String title, String[] plotPaths) {
        button.setEnabled(true);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Running.this.displayPanel.setBackground(button.getBackground());
                Running.this.titleField.setBackground(button.getBackground());
                Running.this.showPlots(title, plotPaths);
                Running.this.setSizes();
                Running.this.displayPanel.repaint();
                Running.this.displayPanel.revalidate();
            }
        });
    }

    private void showPlots(String title, String[] plotPaths) {
        displayPanel.removeAll();
        titleField.setText(title);
        displayPanel.add(titleField);
        try {
            for (String plotPath : plotPaths) {
                System.out.println(plotPath);
                BufferedImage plotImage = ImageIO.read(new File(plotPath));
                JLabel plotLabel = new JLabel(new ImageIcon(plotImage));
                displayPanel.add(plotLabel);
            }
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    public void setSizes() {
        Rectangle size = displayPanel.getBounds();
        Component[] components = displayPanel.getComponents();
        boolean isPrediction = false;
        int counter = 0;
        for (Component component : components) {
            if (component instanceof JLabel) {
                counter++;
            } else if (component instanceof JTextField) {
                component.setPreferredSize(new Dimension(
                        size.width,
                        (int) ceil(size.height * 0.1)
                ));
                component.setFont(new Font("Arial", Font.BOLD,
                        (int) ceil((size.height + size.width) * 0.03)));
                isPrediction = ((JTextField) component).getText().contains("Prediction");
            }
        }
        int width, height;
        if (counter != 0) {
            for (Component component : components) {
                if (component instanceof JLabel) {
                    if (isPrediction) {
                        width = (int) ceil(size.width * 0.95);
                        height = (int) ceil(size.height * 0.85);
                    } else {
                        if (counter == 1) {
                            counter = 2;
                        }
                        width = (int) ceil(size.width * ((double) 1 / counter));
                        height = width;
                    }

                    component.setPreferredSize(new Dimension(width, height));
                    this.setIconSize((JLabel) component, width, height);
                }
            }
        }
    }

    private void setIconSize(JLabel label, int width, int height) {
        ImageIcon imageIcon = (ImageIcon) label.getIcon();
        Image icon = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(icon);
        label.setIcon(imageIcon);
    }

    private void log(String msg) {
        this.logComponent.setText(this.logComponent.getText() + "\n> " + msg);
    }
}
