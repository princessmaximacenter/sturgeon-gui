package nl.prinsesmaximacentrum.sturgeon;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class Running {

    private String inputFolder, outputFolder, barcode, biomaterial, modelFile;
    private boolean useUnclass;
    private int numberIterations, currentIteration = 0;
    private JPanel displayPanel, processPanel;
    private JTextComponent logComponent;
    private Logger logger;
    private JTextField titleField;
    private JLabel startBox, endBox, confBox, cnvBox, predBox, guppyBox, waitBox,
            startLabel, endLabel, confLabel, cnvLabel, predLabel, guppyLabel, waitLabel;
    private JLabel[] flagOrder;
    private JButton outputButton;
    private boolean stop = false;
    private Pattern iterPattern = Pattern.compile("iteration_[0-9]+");
    private String predictPlotPath = "", confidencePlotPath = "", confidenceTablePath = "", cnvPlotPath = "";
    private final String predictTile = "Prediction of Iteration ", confidenceTitle = "Confidence of Iteration ",
                         cnvTitle = "CNV Plot Iteration ";
    private Config config;
    private ColorConfig colorConfig;
    private Menu menu;

    public Running(String inputFolder, String outputFolder, String barcode,
                   String biomaterial, String modelFile, boolean useUnclass,
                   int numberIterations, JTextComponent logComponent,
                   JPanel displayPanel, Config config, Menu menu, ColorConfig colorConfig,
                   Logger logger) {
        this.config = config;
        this.colorConfig = colorConfig;
        this.menu = menu;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.barcode = barcode;
        this.biomaterial = biomaterial;
        this.modelFile = modelFile;
        this.useUnclass = useUnclass;
        this.numberIterations = numberIterations;
        this.logComponent = logComponent;
        this.logger = logger;
        this.displayPanel = displayPanel;
        this.setTextFields();
        this.setProcessElements();
    }

    private void setTextFields() {
        this.titleField = new JTextField("Waiting for the first iteration to finish...");
        this.titleField.setBorder(BorderFactory.createEmptyBorder());
        this.titleField.setForeground(Color.white);

    }

    private void setProcessElements() {
        this.processPanel = new JPanel(new FlowLayout());
        processPanel.setBackground(menu.getRunningButton().getBackground());

        this.startBox = new JLabel();
        this.endBox = new JLabel();
        this.predBox = new JLabel();
        this.guppyBox = new JLabel();
        this.cnvBox = new JLabel();
        this.confBox = new JLabel();
        this.waitBox = new JLabel();

        this.flagOrder = new JLabel[]{startBox, waitBox, guppyBox, predBox, confBox, cnvBox, endBox};
        
        this.startLabel = new JLabel("Starting iteration");
        this.endLabel = new JLabel("Finishing iteration");
        this.predLabel = new JLabel("Making predictions");
        this.guppyLabel = new JLabel("Running Guppy");
        this.cnvLabel = new JLabel("Making CNV plot");
        this.confLabel = new JLabel("Making confidence plot");
        this.waitLabel = new JLabel("Waiting for new/complete file");

        this.outputButton = new JButton("Open result folder");
        this.outputButton.setBackground(colorConfig.getFileButton());
        this.outputButton.setOpaque(true);
        this.outputButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(new File(outputFolder));
                    } catch (IOException err) {
                        Running.this.log("ERROR: Failed to open " + outputFolder);
                    }
                }
            }
        });

        this.processPanel.add(titleField);
        this.processPanel.add(startBox);
        this.processPanel.add(startLabel);
        this.processPanel.add(waitBox);
        this.processPanel.add(waitLabel);
        this.processPanel.add(guppyBox);
        this.processPanel.add(guppyLabel);
        this.processPanel.add(predBox);
        this.processPanel.add(predLabel);
        this.processPanel.add(confBox);
        this.processPanel.add(confLabel);
        this.processPanel.add(cnvBox);
        this.processPanel.add(cnvLabel);
        this.processPanel.add(endBox);
        this.processPanel.add(endLabel);
        this.processPanel.add(outputButton);
    }

    public void showProcess() {
        displayPanel.removeAll();
        String title = "Progress Iteration " + currentIteration;
        if (currentIteration == 0) {
            title = "Waiting for the first iteration";
        } else if (stop){
            title = "Finishing Iteration " + currentIteration + " and Stopping";
        }
        titleField.setText(title);
        displayPanel.add(processPanel);
        displayPanel.revalidate();
        displayPanel.repaint();
    }

    public void run() {
        try {
            ProcessBuilder pb = new ProcessBuilder();
            String command = " run -it --rm --gpus all -v " + inputFolder + ":/home/docker/input " +
                    "-v " + outputFolder + ":/home/docker/output " +
                    "-v " + config.getRefGenome() + ":/home/docker/refGenome/ " +
                    "-v " + modelFile + ":/opt/sturgeon/sturgeon/include/models/model.zip " +
                    "-v " + config.getWrapperFlagDir() + ":/home/docker/wrapper/ " +
                    config.getExtraArgs() + " " +
                    config.getSturgeonImage() + " " + config.getWrapperScript() +
                    " --barcode " + barcode + " --useClassifiedBarcode " + !useUnclass +
                    " --cnvFreq " + numberIterations;
            this.log("Starting sturgeon with the following call:\n" +
                    "docker " + command);
            pb.command("docker", command);
            Process proc = pb.start();

            Thread outputReader = new Thread(() -> {
                try (InputStream inputStream = proc.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String responseLine;
                    while ((responseLine = reader.readLine()) != null) {
                        this.log(responseLine);
                        if (responseLine.contains("FLAG")) {
                            this.setFlag(responseLine);
                            Matcher matcher = iterPattern.matcher(responseLine);
                            if (matcher.find()) {
                                String iteration = matcher.group(0);
                                this.setCurrentIteration(iteration);
                                checkNewResults(iteration);
                            }
                        }
                    }
                    this.log("Finished!");
                } catch (IOException e) {
                    e.printStackTrace();
                    this.log("ERROR: " + e.getMessage());
                }
            });
            outputReader.start();

        } catch (IOException e) {
            e.printStackTrace();
            this.log("ERROR: " + e.getMessage());
        }

    }

    private void setFlag(String flagText) {
        boolean flagSet = false;
        for (JLabel box : this.flagOrder) {
            if ((flagText.contains("starting processing") && Objects.equals(box, startBox)) ||
                (flagText.contains("completed") && Objects.equals(box, endBox)) ||
                (flagText.contains("confidence") && Objects.equals(box, confBox)) ||
                (flagText.contains("CNV") && Objects.equals(box, cnvBox)) ||
                (flagText.contains("sturgeon") && Objects.equals(box, predBox))) {
                box.setBackground(Color.blue);
                flagSet = true;
            } else {
                if (flagSet) {
                    if (currentIteration % numberIterations != 0 && Objects.equals(box, cnvBox)){
                        box.setBackground(Color.lightGray);
                    } else {
                        box.setBackground(Color.white);
                    }
                } else {
                    box.setBackground(Color.green);
                }
            }
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

    private void setCurrentIteration(String iteration) {
        String[] iterationSplit = iteration.split("_");
        iteration = iterationSplit[iterationSplit.length - 1];
        this.currentIteration = Integer.parseInt(iteration);
    }

    private void setResultPath(String resultObject, String resultPath, String iteration) {

        if (Objects.equals(resultObject, "confidencePlot") | Objects.equals(resultObject, "confidenceTable")) {
            if (Objects.equals(resultObject, "confidencePlot")) {
                this.confidencePlotPath = resultPath;
            } else if (Objects.equals(resultObject, "confidenceTable")) {
                this.confidenceTablePath = resultPath;
            }
            this.setActionButtons(this.menu.getConfidenceButton(),
                    confidenceTitle + iteration,
                    new String[]{this.confidenceTablePath, this.confidencePlotPath});
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
                Running.this.setSizes(Running.this.displayPanel.getBounds());
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
                if (plotPath.endsWith(".tsv")) {
                    TSVTable table = new TSVTable(plotPath);
                    displayPanel.add(table.getScrollPane());
                } else {
                    System.out.println(plotPath);
                    BufferedImage plotImage = ImageIO.read(new File(plotPath));
                    JLabel plotLabel = new JLabel(new ImageIcon(plotImage));
                    displayPanel.add(plotLabel);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    private void setResultSizes(Rectangle size) {
        Component[] components = displayPanel.getComponents();
        boolean isPrediction = false;
        int counter = 0;
        for (Component component : components) {
            if (component instanceof JLabel || component instanceof JScrollPane) {
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
                if (component instanceof JLabel || component instanceof JScrollPane) {
                    if (isPrediction) {
                        width = (int) ceil(size.width * 0.95);
                        height = (int) ceil(size.height * 0.85);
                    } else {
                        if (counter == 1) {
                            counter = 2;
                        }
                        width = (int) ceil(size.width * ((double) 1 / (counter * 1.1)));
                        height = width;
                    }

                    component.setPreferredSize(new Dimension(width, height));

                    if (component instanceof JLabel) {
                        this.setIconSize((JLabel) component, width, height);
                    }
                }
            }
        }
    }

    private void setProcessSizes(Rectangle size) {
        Rectangle panelSize = new Rectangle((int) floor((double) size.width / 2), size.height);
        processPanel.setPreferredSize(new Dimension(
                panelSize.width,
                panelSize.height
        ));


        int boxSize = (int) floor((double) panelSize.width * 0.1);
        int borderSize = (int) ceil((double) boxSize / 20);
        for(JLabel box: new JLabel[]{startBox, endBox, confBox, cnvBox, predBox, guppyBox, waitBox}){
            box.setPreferredSize(new Dimension(boxSize, boxSize));
            box.setBorder(BorderFactory.createLineBorder(Color.black, borderSize));
            box.setOpaque(true);
        }
        int labelSize = (int) ceil((double) panelSize.width * 0.85);
        for(JLabel label: new JLabel[]{startLabel, endLabel, confLabel, cnvLabel, predLabel, guppyLabel, waitLabel}){
            label.setPreferredSize(new Dimension(labelSize, boxSize));
            label.setFont(new Font("Arial", Font.PLAIN, (int) ceil((double) boxSize * 0.5)));
            label.setBackground(menu.getRunningButton().getBackground());
            label.setForeground(Color.black);
            if (Objects.equals(label, cnvLabel) && currentIteration % numberIterations != 0) {
                label.setForeground(Color.lightGray);
            }
            label.setOpaque(true);
        }
        titleField.setPreferredSize(new Dimension(panelSize.width, (int) floor((double) panelSize.height * 0.1)));
        titleField.setFont(new Font("Arial", Font.BOLD, (int) ceil((double) boxSize * 0.7)));
        titleField.setBackground(menu.getRunningButton().getBackground());
        outputButton.setPreferredSize(new Dimension((int) ceil((double) labelSize * 0.5), boxSize));
        outputButton.setFont(new Font("Arial", Font.PLAIN, (int) ceil((double) boxSize * 0.4)));
        outputButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    public void setSizes(Rectangle size) {
        this.setResultSizes(size);
        this.setProcessSizes(size);
    }

    private void setIconSize(JLabel label, int width, int height) {
        ImageIcon imageIcon = (ImageIcon) label.getIcon();
        Image icon = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(icon);
        label.setIcon(imageIcon);
    }

    private void log(String msg) {
        String backlog = String.join("\n",
                Arrays.copyOfRange(this.logComponent.getText().split("\n"), 0, 100));
        this.logComponent.setText(backlog + "\n> " + msg);
        this.logger.addToLog(msg);
    }
}
