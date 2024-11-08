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
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Class for calling the sturgeon back-end and showing pages to the users regarding progress and results.
 */
public class Running {

    private String inputFolder, outputFolder, barcode, modelFile;
    private boolean useUnclass;
    private int numberIterations, currentIteration = 0;
    private JPanel displayPanel, processPanel;
    private JTextComponent logComponent;
    private JScrollPane logScroll;
    private Logger logger;
    private JLabel titleLabel, resultLabel,
            startBox, endBox, confBox, cnvBox, predBox, guppyBox, waitBox,
            startLabel, endLabel, confLabel, cnvLabel, predLabel, guppyLabel, waitLabel;
    private JLabel[] flagOrder;
    private JButton outputButton;
    private boolean stop = false;
    private final String iterPrefix = "iteration_";
    private final Pattern iterPattern = Pattern.compile(iterPrefix + "[0-9]+");
    private String predictPlotPath = "", confidencePlotPath = "", confidenceTablePath = "", cnvPlotPath = "";
    private final String predictTile = "Prediction of ", confidenceTitle = "Confidence of ",
                         cnvTitle = "CNV Plot ";
    private Config config;
    private ColorConfig colorConfig;
    private Menu menu;

    /**
     * Init of the Running class
     * @param inputFolder: Path to the pod5 input folder
     * @param outputFolder: Path where sturgeon needs to write its output to
     * @param barcode: Barcode used in the library prep
     * @param modelFile: Path to the sturgeon model
     * @param useUnclass: If true, don't hard filter reads that do not match the barcode
     * @param numberIterations: How many sequencing iterations before making a CNV?
     * @param logComponent: Object to write the log to
     * @param displayPanel: What panel to display the current page on
     * @param config: Config object with all paths used throughout the application
     * @param menu: Menu object to change the buttons looks and behaviour when needed
     * @param colorConfig: Config containing the colors used
     * @param logger: Logger object to write messages to
     * @param logScroll: ScrollPane to set the position of the scrolls
     */
    public Running(String inputFolder, String outputFolder, String barcode,
                   String modelFile, boolean useUnclass,
                   int numberIterations, JTextComponent logComponent,
                   JPanel displayPanel, Config config, Menu menu, ColorConfig colorConfig,
                   Logger logger, JScrollPane logScroll) {
        this.config = config;
        this.colorConfig = colorConfig;
        this.menu = menu;
        this.inputFolder = inputFolder;
        this.outputFolder = outputFolder;
        this.barcode = barcode;
        this.modelFile = modelFile;
        this.useUnclass = useUnclass;
        this.numberIterations = numberIterations;
        this.logComponent = logComponent;
        this.logScroll = logScroll;
        this.logger = logger;
        this.displayPanel = displayPanel;
        this.setTitle("");
        this.setResultLabel("");
        this.setProcessElements();
    }

    private void setTitle(String title) {
        if (this.titleLabel == null) {
            this.titleLabel = new JLabel(title);
            this.titleLabel.setBorder(BorderFactory.createEmptyBorder());
            this.titleLabel.setForeground(Color.white);
        } else {
            if (!this.titleLabel.getText().contains("Finishing iteration")) {
                this.titleLabel.setText(title);
                this.titleLabel.repaint();
            }
        }
    }

    private void setResultLabel(String title) {
        if (this.resultLabel == null) {
            this.resultLabel = new JLabel(title);
            this.resultLabel.setForeground(Color.white);
        } else {
            SwingUtilities.invokeLater(() -> {
                this.resultLabel.setText(title);
            });
        }
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

        this.processPanel.add(titleLabel);
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
        String title = "Running iteration " + currentIteration;
        if (currentIteration == 0) {
            title = "Waiting for the first iteration";
        } else if (stop){
            title = "Finishing iteration " + currentIteration + " and stopping";
        }
        this.setTitle(title);
        displayPanel.add(processPanel);
        this.setSizes();
    }

    public void stop() {
        this.stop = true;
    }

    /**
     * Method to call the docker command and check if it is still running
     */
    public void run() {
        try {
            String command = "docker run --rm --gpus all " +
                    "-v " + inputFolder + ":/home/docker/input " +
                    "-v " + outputFolder + ":/home/docker/output " +
                    "-v " + config.getRefGenome() + ":/home/docker/refGenome/ " +
                    "-v " + modelFile + ":/opt/sturgeon/sturgeon/include/models/model.zip " +
                    "-v " + config.getWrapperFlagDir() + ":/home/docker/wrapper/ " +
                    "-v " + "/opt/docker/R_scripts/:/opt/sturgeon/R_scripts/ " +
                    config.getSturgeonImage() + " '" +
                    config.getWrapperScript() +
                    " --barcode " + barcode +
                    " --cnvFreq " + numberIterations +
                    ((!useUnclass) ? " --useClassifiedBarcode'" : "'");
            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c", command);
            this.log("Starting sturgeon with the following call:\n" +
                    String.join(" ", pb.command()));
            pb.redirectErrorStream(true);
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
                                this.checkNewResults();
                                this.resetScreen();
                            }
                        }
                    }
                    this.log("Finished last iteration " + currentIteration + ".\nIt is safe now to close this program!");
                } catch (IOException e) {
                    e.printStackTrace();
                    this.log("ERROR: " + e.getMessage());
                } catch (Exception e) {
                    this.log("ERROR: " + e.getMessage());
                }
            });
            outputReader.start();

        } catch (IOException e) {
            e.printStackTrace();
            this.log("ERROR: " + e.getMessage());
        }

    }

    /**
     * Method to tell the user which step sturgeon is at.
     * @param flagText: A sturgeon log line that contains a FLAG part
     */
    private void setFlag(String flagText) {
        boolean flagSet = false;
        for (JLabel box : this.flagOrder) {
            if ((flagText.contains("starting processing") && Objects.equals(box, startBox)) ||
                (flagText.contains("completed") && Objects.equals(box, endBox)) ||
                (flagText.contains("confidence") && Objects.equals(box, confBox)) ||
                (flagText.contains("CNV") && Objects.equals(box, cnvBox)) ||
                (flagText.contains("guppy") && Objects.equals(box, guppyBox)) ||
                (flagText.contains("waiting 30 seconds") && Objects.equals(box, waitBox)) ||
                (flagText.contains("sturgeon") && Objects.equals(box, predBox))) {
                box.setBackground(Color.blue);
                flagSet = true;
                if (flagText.contains("completed")) {
                    box.setBackground(Color.green);
                }
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

    private void checkNewResults() {
        String iterationFolder = outputFolder + "/" + iterPrefix + currentIteration + "/";
        List<List<String>> targets = Arrays.asList(
                Arrays.asList(this.confidencePlotPath, iterationFolder + config.getConfidencePlot(), "confidencePlot"),
                Arrays.asList(this.cnvPlotPath, iterationFolder + config.getCnvPlot(), "cnvPlot"),
                Arrays.asList(this.predictPlotPath, iterationFolder + config.getPredictPlot(), "predictPlot"),
                Arrays.asList(this.confidenceTablePath, iterationFolder + config.getConfidenceTable(), "confidenceTable")
        );
        for (List<String> targetStrings : targets) {
            String targetObject = targetStrings.get(0);
            if (!targetObject.contains(iterPrefix + currentIteration)) {
                String filePath = targetStrings.get(1).replaceAll(iterPattern.pattern(),
                        iterPrefix + currentIteration);
                File targetFile = new File(filePath);
                if (targetFile.exists() && !targetFile.isDirectory()) {
                    setResultPath(targetStrings.get(2), filePath, iterPrefix + currentIteration);
                }
            }
        }
    }

    private void setCurrentIteration(String iteration) {
        String[] iterationSplit = iteration.split("_");
        iteration = iterationSplit[iterationSplit.length - 1];
        if (this.currentIteration != Integer.parseInt(iteration)) {
            this.currentIteration = Integer.parseInt(iteration);
            cnvLabel.setForeground(Color.black);
            if (Objects.equals(cnvLabel, cnvLabel) && currentIteration % numberIterations != 0) {
                cnvLabel.setForeground(Color.lightGray);
            }
        }
        this.setTitle("Running Iteration " + currentIteration);
    }

    private void setResultPath(String resultObject, String resultPath, String iteration) {

        if (Objects.equals(resultObject, "confidencePlot") | Objects.equals(resultObject, "confidenceTable")) {
            if (Objects.equals(resultObject, "confidencePlot")) {
                this.confidencePlotPath = resultPath;
            } else if (Objects.equals(resultObject, "confidenceTable")) {
                this.confidenceTablePath = resultPath;
            }
            if (confidencePlotPath.contains("_"+iteration+".") && confidenceTablePath.contains("_"+iteration+".")) {
                this.setActionButtons(this.menu.getConfidenceButton(),
                        confidenceTitle + iteration.replace("_", " "),
                        new String[]{this.confidenceTablePath, this.confidencePlotPath});
            }
        } else if (Objects.equals(resultObject, "predictPlot")) {
            this.predictPlotPath = resultPath;
            this.setActionButtons(this.menu.getPredictionButton(),
                    predictTile + iteration.replace("_", " "),
                    new String[]{this.predictPlotPath});
        } else {
            this.cnvPlotPath = resultPath;
            this.setActionButtons(this.menu.getCnvPlotButton(),
                    cnvTitle + iteration.replace("_", " "),
                    new String[]{this.cnvPlotPath});
        }
    }

    /**
     * Method to adjust the action behind the results buttons
     * @param button: JButton to adjust
     * @param title: Title of the page when clicking on the button
     * @param plotPaths: Path to the plot/table to show on the page
     */
    private void setActionButtons(JButton button, String title, String[] plotPaths) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Running.this.displayPanel.setBackground(button.getBackground());
                Running.this.titleLabel.setBackground(button.getBackground());
                Running.this.showPlots(title, plotPaths);
                Running.this.setSizes();
                Running.this.displayPanel.repaint();
                Running.this.displayPanel.revalidate();
            }
        });
        button.setEnabled(true);
    }

    private void showPlots(String title, String[] plotPaths) {
        displayPanel.removeAll();
        resultLabel.setText(title);
        displayPanel.add(resultLabel);
        try {
            for (String plotPath : plotPaths) {
                if (plotPath.endsWith(".tsv")) {
                    TSVTable table = new TSVTable(plotPath);
                    displayPanel.add(table.getScrollPane());
                } else {
                    BufferedImage plotImage = ImageIO.read(Files.newInputStream(new File(plotPath).toPath()));
                    ImageIcon icon = new ImageIcon(plotImage);
                    JLabel plotLabel = new JLabel(icon);
                    displayPanel.add(plotLabel);
                }
            }
            displayPanel.revalidate();
            displayPanel.revalidate();
        } catch (IOException e) {
            e.printStackTrace();
            String msg = "ERROR: " + e.getMessage();
            this.log(msg);
            System.err.println(msg);
        }
    }

    private void setResultSizes() {
        Rectangle size = displayPanel.getBounds();
        Component[] components = displayPanel.getComponents();
        boolean isPrediction = false;
        int counter = 0;
        for (Component component : components) {
            if (component instanceof JLabel || component instanceof JScrollPane) {
                if (!Objects.equals(component, resultLabel)) {
                    counter++;
                } else {
                    resultLabel.setPreferredSize(new Dimension(
                            size.width,
                            (int) ceil(size.height * 0.1)
                    ));
                    resultLabel.setFont(new Font("Arial", Font.BOLD,
                            (int) ceil((size.height + size.width) * 0.02)));
                    isPrediction = (resultLabel).getText().contains("Prediction");
                }
            }
        }
        int width, height;
        if (counter != 0) {
            for (Component component : components) {
                if ((component instanceof JLabel || component instanceof JScrollPane) &&
                        !Objects.equals(component, resultLabel)) {
                    if (isPrediction) {
                        width = (int) ceil(size.width * 0.95);
                        height = (int) ceil(size.height * 0.85);
                    } else {
                        if (counter == 1) {
                            counter = 2;
                        }
                        width = (int) ceil(size.width * ((double) 1 / (counter * 1.1)) * 0.80);
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

    private void setProcessSizes() {
        Rectangle size = displayPanel.getBounds();
        Rectangle panelSize = new Rectangle((int) floor((double) size.width / 2), size.height);
        processPanel.setPreferredSize(new Dimension(
                panelSize.width,
                panelSize.height
        ));


        int boxSize = (int) floor((double) panelSize.width * 0.09);
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
        titleLabel.setPreferredSize(new Dimension(panelSize.width, (int) floor((double) panelSize.height * 0.1)));
        titleLabel.setFont(new Font("Arial", Font.BOLD, (int) ceil((double) boxSize * 0.5)));
        titleLabel.setBackground(menu.getRunningButton().getBackground());
        outputButton.setPreferredSize(new Dimension((int) ceil((double) labelSize * 0.5), boxSize));
        outputButton.setFont(new Font("Arial", Font.PLAIN, (int) ceil((double) boxSize * 0.4)));
        outputButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    }

    public void setSizes() {
        this.setResultSizes();
        this.setProcessSizes();
        this.resetScreen();
    }

    public void resetScreen() {
        this.processPanel.revalidate();
        this.processPanel.repaint();
        this.displayPanel.revalidate();
        this.displayPanel.repaint();
    }

    private void setIconSize(JLabel label, int width, int height) {
        ImageIcon imageIcon = (ImageIcon) label.getIcon();
        if (imageIcon != null) {
            Image icon = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            imageIcon = new ImageIcon(icon);
            label.setIcon(imageIcon);
        }
    }

    private void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            this.logger.addToLog(msg);

            int horizontalScrollPosition = this.logScroll.getHorizontalScrollBar().getValue();
            this.logComponent.setText(this.logComponent.getText() + "\n> " + msg);
            this.logComponent.setCaretPosition(this.logComponent.getDocument().getLength());
            this.logScroll.getHorizontalScrollBar().setValue(horizontalScrollPosition);
            this.logScroll.getViewport().setViewPosition(new Point(0, this.logScroll.getViewport().getViewPosition().y));
        });
    }
}
