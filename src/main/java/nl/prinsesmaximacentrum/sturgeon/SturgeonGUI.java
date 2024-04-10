package nl.prinsesmaximacentrum.sturgeon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class SturgeonGUI extends JFrame {

    private Container window;
    private ColorConfig colorConfig;
    private JPanel menuPanel, displayPanel, terminalPanel, workPanel;
    private Menu menuItems;
    private Setup setupOptions;

    public SturgeonGUI(ColorConfig colorConfig) {
        super("Sturgeon");
        this.colorConfig = colorConfig;
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

        this.menuPanel = new JPanel(new GridLayout(8,1));
        this.menuPanel.setBackground(this.colorConfig.getMenu());
        this.window.add(menuPanel, BorderLayout.WEST);
        this.setMenuItems();

        this.workPanel = new JPanel(new GridBagLayout());
        this.workPanel.setBackground(this.colorConfig.getTerminal());
        this.window.add(workPanel, BorderLayout.CENTER);
        this.setWorkSubPanels();

    }

    private void setMenuItems() {
        this.menuItems = new Menu();
        this.menuPanel.add(this.menuItems.getSetupLabel());
        this.menuPanel.add(this.menuItems.getRunningLabel());
    }

    private void setWorkSubPanels() {
        this.displayPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        this.displayPanel.setBackground(this.colorConfig.getDisplay());
        GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.gridx = 0;
        actionConstraints.gridy = 0;
        actionConstraints.weightx = 1.0; // Expand horizontally
        actionConstraints.weighty = 0.8; // 80% of vertical space
        actionConstraints.fill = GridBagConstraints.BOTH;
        this.setupOptions = new Setup();
        setupOptions.addElements(displayPanel);
        this.workPanel.add(displayPanel, actionConstraints);

        this.terminalPanel = new JPanel();
        this.terminalPanel.setBackground(this.colorConfig.getTerminal());
        GridBagConstraints progressConstraints = new GridBagConstraints();
        progressConstraints.gridx = 0;
        progressConstraints.gridy = 1;
        progressConstraints.weightx = 1.0; // Expand horizontally
        progressConstraints.weighty = 0.2; // 20% of vertical space
        progressConstraints.fill = GridBagConstraints.BOTH;
        this.workPanel.add(terminalPanel, progressConstraints);
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

        this.setupOptions.setSizes(displayPanel.getBounds());

        this.revalidate();
        this.repaint();
    }
}
