package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

/**
 * Class for building the Sturgeon GUI
 */
public class SturgeonGUI extends JFrame {

    private Container window;
    private ColorConfig colorConfig;
    private Config config;
    private JPanel menuPanel, displayPanel, workPanel;
    private JTextArea terminalArea;
    private Menu menuItems;
    private Setup setupOptions;
    private Running running;
    private final int SETUP = 0, RUNNING = 1;
    private int activeScreen = 0, confirmStop = 1;
    private Logger logger;
    private JScrollPane terminalScroll;

    /**
     * SturgeonGUI init
     * @param colorConfig: config class of the colors used in the GUI
     * @param config: config class of paths and strings used throughout the application
     * @param logPath: path to the log file
     */
    public SturgeonGUI(ColorConfig colorConfig, Config config, String logPath) {
        super();
        this.colorConfig = colorConfig;
        this.config = config;
        this.logger = new Logger(logPath);
        this.logger.addToLog("Sturgeon GUI v" + this.config.getVersion() + " started");
        this.setTitle("Sturgeon GUI v" + this.config.getVersion());
        this.setClosure();
        this.setWindow();
        this.buildGUI();
        this.setSizes();
        this.pack();
    }

    /**
     * Method to adjust the behaviour when the user request to close the app.
     */
    private void setClosure() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.RUNNING & !SturgeonGUI.this.terminalArea.getText().contains("It is safe now to close this program!")) {
                    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                    JOptionPane.showMessageDialog(null, "Please wait till it is safe to close this program!");
                } else {
                    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    SturgeonGUI.this.setVisible(false);
                    SturgeonGUI.this.dispose();
                }
            }
        });
    }

    /**
     * Method to configure the main window
     */
    private void setWindow() {
        this.window = getContentPane();
        this.window.setLayout(new BorderLayout());
        this.window.setSize(new Dimension(800, 600));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.window.setPreferredSize(new Dimension(
                (int) floor(screenSize.width * 1),
                (int) floor(screenSize.height * 0.90)));
        this.window.setBackground(this.colorConfig.getMenu());
        this.window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SturgeonGUI.this.setSizes();
            }
        });
    }

    /**
     * Method to set the layout of the main frame
     */
    private void buildGUI() {
        this.logger.addToLog("Building GUI");
        this.menuPanel = new JPanel(new GridLayout(6,1));
        this.menuPanel.setBackground(this.colorConfig.getMenu());
        this.window.add(menuPanel, BorderLayout.WEST);
        this.setMenuItems();

        this.workPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.workPanel.setBackground(this.colorConfig.getTerminal());
        this.window.add(workPanel, BorderLayout.CENTER);
        this.setWorkSubPanels();
        this.logger.addToLog("Building GUI complete");

    }

    /**
     * Method so add buttons to the menu panel
     */
    private void setMenuItems() {
        this.menuItems = new Menu(colorConfig);
        JButton setupButton = this.menuItems.getSetupButton();
        this.menuPanel.add(setupButton);
        this.disableSetupButton(false);

        JButton runningButton = menuItems.getRunningButton();
        JButton stopButton = menuItems.getStopButton();
        this.menuPanel.add(runningButton);
        this.menuPanel.add(menuItems.getPredictionButton());
        this.menuPanel.add(menuItems.getConfidenceButton());
        this.menuPanel.add(menuItems.getCnvPlotButton());
        this.menuPanel.add(stopButton);
        this.setRunningClick(runningButton);
        this.setStopClick(stopButton);
    }

    /**
     * Method to set the action when clicking the stop button in the menu.
     * User should not be able to close when sturgeon is still running.
     * @param stopButton: JButton to add the action to
     */
    private void setStopClick(JButton stopButton) {
        stopButton.setEnabled(true);
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.RUNNING) {
                    try {
                        SturgeonGUI.this.logger.addToLog("Clicked STOP button");
                        SturgeonGUI.this.confirmStop = JOptionPane.showConfirmDialog(null,
                                "Are you sure you want to stop?", "Stop?", JOptionPane.YES_NO_OPTION);
                        if (SturgeonGUI.this.confirmStop == 0) {
                            ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c",
                                    "/bin/bash -c 'touch " + config.getWrapperFlagDir() + "/wrapper_stop.txt'");
                            pb.start();
                            SturgeonGUI.this.running.stop();
                            SturgeonGUI.this.running.showProcess();
                            stopButton.setEnabled(false);
                        }
                        SturgeonGUI.this.logger.addToLog("Completed loading STOP");
                    } catch (IOException err) {
                        SturgeonGUI.this.logger.addToLog(err.getMessage());
                    }
                }
            }
        });
    }

    /**
     * Method to disable/enable the setup button when the run has started
     * @param disable: if true, disable setup button. if false, enable setup button
     */
    private void disableSetupButton(Boolean disable) {
        JButton setupButton = this.menuItems.getSetupButton();
        if (disable) {
            setupButton.setEnabled(false);
            setupButton.removeMouseListener(setupButton.getMouseListeners()[setupButton.getMouseListeners().length-1]);
        } else {
            setupButton.setEnabled(true);
            setupButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    SturgeonGUI.this.addLoadingMessage(false, "SETUP");
                    SturgeonGUI.this.showSetup();
                    SturgeonGUI.this.setSizes();
                    SturgeonGUI.this.addLoadingMessage(true, "SETUP");
                }
            });

        }
    }

    /**
     * Add the action behind the start/running button.
     * When pressed the setup is validated and will try to call sturgeon using the Running object.
     * @param runningButton: JButton to add the action to
     */
    private void setRunningClick(JButton runningButton) {
        runningButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SturgeonGUI.this.addLoadingMessage(false, "START");
                Setup setupOptions = SturgeonGUI.this.setupOptions;
                JTextComponent log = SturgeonGUI.this.terminalArea;
                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP &
                        setupOptions.validateSetup()) {
                    String outputFolder = setupOptions.getOutputField().getText() + "/" + setupOptions.getRunNameField().getText();
                    SturgeonGUI.this.logger.addToLog("Creating folder: " + outputFolder);
                    File dir = new File(outputFolder);
                    if (dir.mkdirs()) {
                        // Set the permissions to 775
                        dir.setReadable(true, false);  // Readable by everyone
                        dir.setWritable(true, false);  // Writable by group and owner
                        dir.setExecutable(true, false);  // Executable by everyone
                    } else {
                        SturgeonGUI.this.logger.addToLog("Directory creation failed.");
                    }
                    SturgeonGUI.this.displayPanel.removeAll();
                    SturgeonGUI.this.setActiveScreen(SturgeonGUI.this.RUNNING);
                    SturgeonGUI.this.disableSetupButton(true);
                    try {
                        SturgeonGUI.this.running = new Running(setupOptions.getInputField().getText(),
                                outputFolder,
                                setupOptions.getBarcodeField().getText(),
                                setupOptions.getModelField().getText(),
                                (boolean) setupOptions.getClassBox().getSelectedItem(),
                                (int) setupOptions.getIterBox().getSelectedItem(),
                                log, SturgeonGUI.this.displayPanel, SturgeonGUI.this.config, menuItems, colorConfig,
                                SturgeonGUI.this.logger, SturgeonGUI.this.terminalScroll);
                        running.run();
                        SturgeonGUI.this.menuItems.getRunningButton().setText("Progress");
                        SturgeonGUI.this.setSizes();
                        SturgeonGUI.this.running.showProcess();
                    } catch (NullPointerException err) {
                        logger.addToLog(Arrays.toString(err.getStackTrace()) + "\n" + err.getMessage());
                        System.err.println("Error: " + err.getMessage());
                    }
                } else {
                    if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP) {
                        SturgeonGUI.this.logger.addToLog("Validation of the setup has failed");
                        log.setText(log.getText() + "\n> Validation of the setup has failed!\n" +
                                "Make sure you filled in everything and the output folder doesn't exist yet.");
                    } else {
                        SturgeonGUI.this.setSizes();
                        SturgeonGUI.this.running.showProcess();
                    }
                }
                SturgeonGUI.this.addLoadingMessage(true, "START");
            }
        });
    }

    /**
     * Adds additional debug lines to the log
     * @param finished: if true, log the step has succeeded else mention it is still loading.
     * @param step: Step in progress this log message is about.
     */
    private void addLoadingMessage(boolean finished, String step) {
        SwingUtilities.invokeLater(() -> {
            if (!finished) {
                SturgeonGUI.this.logger.addToLog("Loading " + step);
            } else {
                SturgeonGUI.this.logger.addToLog("Finished loading " + step);
            }
        });
    }

    /**
     * Method to show the setup page to the user
     */
    private void showSetup() {
        this.setActiveScreen(SETUP);
        this.displayPanel.removeAll();
        setupOptions.addElements(displayPanel);
        this.revalidate();
        this.repaint();
    }

    /**
     * Method to configure the workPanel with a panel to display the plots/setting/etc and to display
     * the terminal/log panel.
     */
    private void setWorkSubPanels() {
        this.displayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        this.setupOptions = new Setup(colorConfig, config, logger);
        this.showSetup();
        this.workPanel.add(displayPanel);

        this.terminalArea = new JTextArea();
        this.terminalArea.setBackground(this.colorConfig.getTerminal());
        this.terminalArea.setForeground(Color.GREEN);
        DefaultCaret caret = (DefaultCaret)this.terminalArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.terminalArea.setText("> Welcome to Sturgeon GUI v" + config.getVersion() +
                "\n> Contact " + config.getDevMail() + " if you have any questions/feedback.\n> " +
                "Please fill in the setup page to get started.");
        this.terminalScroll = new JScrollPane(this.terminalArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.terminalScroll.setBorder(BorderFactory.createEmptyBorder());
        this.workPanel.add(this.terminalScroll);

    }


    /**
     * Method the set the sizes of the elements in the gui based on the current screen size.
     */
    private void setSizes() {
        Rectangle size = this.window.getBounds();
        this.menuPanel.setPreferredSize(new Dimension(
                (int) floor(size.width * 0.15),
                size.height
        ));
        this.menuItems.setSizes();

        this.workPanel.setPreferredSize(new Dimension(
                (int) ceil(size.width * 0.85),
                size.height
        ));

        Rectangle workSize = this.workPanel.getBounds();
        this.displayPanel.setPreferredSize(new Dimension(
                workSize.width,
                (int) ceil(workSize.height * 0.7)
        ));
        this.displayPanel.setBackground(this.getActiveColor());

        Dimension terminalDim = new Dimension(
                workSize.width,
                (int) floor(workSize.height * 0.3)
        );
        this.terminalScroll.setPreferredSize(terminalDim);
        this.terminalArea.setFont(new Font("Arial", Font.PLAIN,
                (int) ceil((size.width + size.height) * 0.007)));

        if (activeScreen == SETUP) {
            this.setupOptions.setSizes(displayPanel.getBounds());
        } else {
            this.running.setSizes();
        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Method to choose which color to use based on the current activate state of the app
     * @return Color object
     */
    private Color getActiveColor() {
        if (this.activeScreen == this.SETUP) {
            return colorConfig.getSetup();
        } else {
            return colorConfig.getRunning();
        }
    }

    /**
     * Method to change the activeScreen flag to tell which stage the app is at
     * @param screen: int of the stage to set
     */
    private void setActiveScreen(int screen) {
        this.activeScreen = screen;
    }


}
