package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

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
    private int activeScreen = 0;
    private Logger logger;
    private JScrollPane terminalScroll;

    public SturgeonGUI(ColorConfig colorConfig, Config config, String logPath) {
        super();
        this.colorConfig = colorConfig;
        this.config = config;
        this.logger = new Logger(logPath);
        this.logger.addToLog("Sturgeon v" + this.config.getVersion() + " started");
        this.setTitle("Sturgeon v" + this.config.getVersion());
        this.setClosure();
        this.setWindow();
        this.buildGUI();
        this.setSizes();
        this.pack();
    }

    private void setClosure() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SturgeonGUI.this.setVisible(false);
                SturgeonGUI.this.dispose();
            }
        });
    }

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

    private void setMenuItems() {
        this.menuItems = new Menu(colorConfig);
        JButton setupButton = this.menuItems.getSetupButton();
        this.menuPanel.add(setupButton);
        this.disableMenuButton(false);

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

    private void setStopClick(JButton stopButton) {
        stopButton.setEnabled(true);
        stopButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.RUNNING) {
                    try {
                        SturgeonGUI.this.logger.addToLog("Clicked STOP button");
                        ProcessBuilder pb = new ProcessBuilder("/bin/bash", "-c",
                                "/bin/bash -c 'touch " + config.getWrapperFlagDir() + "/wrapper_stop.txt'");
                        pb.start();
                        SturgeonGUI.this.running.stop();
                        SturgeonGUI.this.running.showProcess();
                        stopButton.setEnabled(false);
                        SturgeonGUI.this.logger.addToLog("Completed loading STOP");
                    } catch (IOException err) {
                        SturgeonGUI.this.logger.addToLog(err.getMessage());
                    }
                }
            }
        });
    }

    private void disableMenuButton(Boolean disable) {
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

    private void setRunningClick(JButton runningButton) {
        runningButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SturgeonGUI.this.addLoadingMessage(false, "START");
                Setup setupOptions = SturgeonGUI.this.setupOptions;
                JTextComponent log = SturgeonGUI.this.terminalArea;
                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP &
                        setupOptions.validateSetup()) {
//                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP) {
                    SturgeonGUI.this.displayPanel.removeAll();
                    SturgeonGUI.this.setActiveScreen(SturgeonGUI.this.RUNNING);
                    SturgeonGUI.this.disableMenuButton(true);
                    try {
                        SturgeonGUI.this.running = new Running(setupOptions.getInputField().getText(),
                                setupOptions.getOutputField().getText(),
                                setupOptions.getBarcodeField().getText(),
                                setupOptions.getBiomaterial().getBm(),
                                setupOptions.getModelField().getText(),
                                (boolean) setupOptions.getClassBox().getSelectedItem(),
                                (int) setupOptions.getIterBox().getSelectedItem(),
                                log, SturgeonGUI.this.displayPanel, SturgeonGUI.this.config, menuItems, colorConfig,
                                SturgeonGUI.this.logger, SturgeonGUI.this.terminalScroll);
                        running.run();
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
                                "Make sure you filled in everything, the output folder is empty and " +
                                "you have given a correct biomaterial ID.");
                    } else {
                        SturgeonGUI.this.setSizes();
                        SturgeonGUI.this.running.showProcess();
                    }
                }
                SturgeonGUI.this.addLoadingMessage(true, "START");
            }
        });
    }

    private void addLoadingMessage(boolean finished, String step) {
        SwingUtilities.invokeLater(() -> {
            if (!finished) {
                SturgeonGUI.this.logger.addToLog("Loading " + step);
            } else {
                SturgeonGUI.this.logger.addToLog("Finished loading " + step);
            }
        });
    }

    private void showSetup() {
        this.setActiveScreen(SETUP);
        this.displayPanel.removeAll();
        setupOptions.addElements(displayPanel);
        this.revalidate();
        this.repaint();
    }

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

    private Color getActiveColor() {
        if (this.activeScreen == this.SETUP) {
            return colorConfig.getSetup();
        } else {
            return colorConfig.getRunning();
        }
    }

    private void setActiveScreen(int screen) {
        this.activeScreen = screen;
    }


}
