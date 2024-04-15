package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

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

    public SturgeonGUI(ColorConfig colorConfig, Config config) {
        super("Sturgeon");
        this.colorConfig = colorConfig;
        this.config = config;
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
        this.window.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
        this.window.setBackground(this.colorConfig.getMenu());
        this.window.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                SturgeonGUI.this.setSizes();
            }
        });
    }

    private void buildGUI() {

        this.menuPanel = new JPanel(new GridLayout(6,1));
        this.menuPanel.setBackground(this.colorConfig.getMenu());
        this.window.add(menuPanel, BorderLayout.WEST);
        this.setMenuItems();

        this.workPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.workPanel.setBackground(this.colorConfig.getTerminal());
        this.window.add(workPanel, BorderLayout.CENTER);
        this.setWorkSubPanels();

    }

    private void setMenuItems() {
        this.menuItems = new Menu(colorConfig);
        JButton setupButton = this.menuItems.getSetupButton();
        this.menuPanel.add(setupButton);
        setupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SturgeonGUI.this.showSetup();
                SturgeonGUI.this.setSizes();
            }
        });

        JButton runningButton = this.menuItems.getRunningButton();
        this.menuPanel.add(runningButton);
        this.menuPanel.add(menuItems.getPredictionButton());
        this.menuPanel.add(menuItems.getConfidenceButton());
        this.menuPanel.add(menuItems.getCnvPlotButton());
        this.menuPanel.add(menuItems.getStopButton());
        this.setRunningClick(runningButton);
    }

    private void setRunningClick(JButton runningButton) {
        runningButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Setup setupOptions = SturgeonGUI.this.setupOptions;
                JTextComponent log = SturgeonGUI.this.terminalArea;
//                if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP &
//                        setupOptions.validateSetup()) {
                if (true) {
                    SturgeonGUI.this.displayPanel.removeAll();
                    SturgeonGUI.this.setActiveScreen(SturgeonGUI.this.RUNNING);
                    try {
                        SturgeonGUI.this.running = new Running(setupOptions.getInputField().getText(),
                                setupOptions.getOutputField().getText(),
                                setupOptions.getBarcodeField().getText(),
                                setupOptions.getBiomaterial().getBm(),
                                setupOptions.getConfigField().getText(),
                                (boolean) setupOptions.getClassBox().getSelectedItem(),
                                (int) setupOptions.getIterBox().getSelectedItem(),
                                log, SturgeonGUI.this.displayPanel, SturgeonGUI.this.config, menuItems);
                        running.run();
                        SturgeonGUI.this.setSizes();
                    } catch (NullPointerException err) {
                        System.err.println("Error:" + err.getMessage());
                    }
                } else {
                    if (SturgeonGUI.this.activeScreen == SturgeonGUI.this.SETUP) {
                        log.setText(log.getText() + "\n> Validation of the setup has failed!\n" +
                                "Make sure you filled in everything and have given a correct biomaterial ID.");
                    }
                }
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
//        GridBagConstraints actionConstraints = new GridBagConstraints();
//        actionConstraints.gridx = 0;
//        actionConstraints.gridy = 0;
//        actionConstraints.weightx = 1.0; // Expand horizontally
//        actionConstraints.weighty = 0.8; // 80% of vertical space
//        actionConstraints.fill = GridBagConstraints.BOTH;
        this.setupOptions = new Setup(colorConfig);
        this.showSetup();
        this.workPanel.add(displayPanel);

        this.terminalArea = new JTextArea("> Welcome to Sturgeon! Please fill in the setup page.");
        this.terminalArea.setBackground(this.colorConfig.getTerminal());
        this.terminalArea.setForeground(Color.GREEN);
//        GridBagConstraints progressConstraints = new GridBagConstraints();
//        progressConstraints.gridx = 0;
//        progressConstraints.gridy = 1;
//        progressConstraints.weightx = 1.0; // Expand horizontally
//        progressConstraints.weighty = 0.199999; // 20% of vertical space
//        progressConstraints.fill = GridBagConstraints.BOTH;
        JScrollPane scroll = new JScrollPane(this.terminalArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        this.workPanel.add(scroll);
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
                (int) ceil(workSize.height * 0.8)
        ));
        this.displayPanel.setBackground(this.getActiveColor());

        this.terminalArea.setPreferredSize(new Dimension(
                workSize.width,
                (int) floor(workSize.height * 0.2)
        ));
        this.terminalArea.setFont(new Font("Arial", Font.PLAIN,
                (int) ceil((size.width + size.height) * 0.007)));

        if (activeScreen == SETUP) {
            this.setupOptions.setSizes(displayPanel.getBounds());
        } else {
            this.running.setSizes(displayPanel.getBounds());
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
